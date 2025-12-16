<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\PriceList;
use App\Models\PriceListItem;
use App\Models\Sale;
use App\Models\SaleItem;
use App\Models\Quote;
use App\Models\QuoteItem;
use App\Models\Purchase;
use App\Models\PurchaseItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;

class PriceListController extends Controller
{
    /**
     * Get effective price for an item and party based on history and price lists.
     * Logic:
     * 1. Check latest SALE price for this item & party (if type=SALE)
     * 2. Check latest QUOTE price for this item & party (if type=SALE)
     * 3. Check latest PURCHASE price for this item & party (if type=PURCHASE)
     * 4. Check Price List assigned to party/global.
     * 
     * Returns the latest price found.
     */
    public function getEffectivePrice(Request $request)
    {
        $companyId = $request->header('X-Company-ID');
        if (!$companyId) {
            return response()->json(['error' => 'Company ID header missing'], 400);
        }

        $request->validate([
            'item_id' => 'required|integer',
            'party_id' => 'required|integer',
            'type' => 'required|string|in:SALE,PURCHASE' // SALE or PURCHASE
        ]);

        $itemId = $request->item_id;
        $partyId = $request->party_id;
        $type = $request->type;

        $candidates = [];

        // 1. Check Sales / Purchases History
        if ($type === 'SALE') {
            $latestSaleItem = SaleItem::join('sales', 'sales.id', '=', 'sale_items.sale_id')
                ->where('sales.company_id', $companyId)
                ->where('sales.party_id', $partyId)
                ->where('sale_items.item_id', $itemId)
                ->orderBy('sales.date', 'desc')
                ->select('sale_items.price', 'sales.date')
                ->first();

            if ($latestSaleItem) {
                $candidates[] = [
                    'source' => 'Last Sale',
                    'price' => $latestSaleItem->price,
                    'date' => $latestSaleItem->date
                ];
            }

            // Check Quotes
            $latestQuoteItem = QuoteItem::join('quotes', 'quotes.id', '=', 'quote_items.quote_id')
                ->where('quotes.company_id', $companyId)
                ->where('quotes.party_id', $partyId)
                ->where('quote_items.item_id', $itemId)
                ->orderBy('quotes.date', 'desc')
                ->select('quote_items.price', 'quotes.date')
                ->first();

            if ($latestQuoteItem) {
                $candidates[] = [
                    'source' => 'Last Quote',
                    'price' => $latestQuoteItem->price,
                    'date' => $latestQuoteItem->date
                ];
            }

        } else if ($type === 'PURCHASE') {
            $latestPurchaseItem = PurchaseItem::join('purchases', 'purchases.id', '=', 'purchase_items.purchase_id')
                ->where('purchases.company_id', $companyId)
                ->where('purchases.party_id', $partyId)
                ->where('purchase_items.item_id', $itemId)
                ->orderBy('purchases.date', 'desc')
                ->select('purchase_items.price', 'purchases.date')
                ->first();

            if ($latestPurchaseItem) {
                $candidates[] = [
                    'source' => 'Last Purchase',
                    'price' => $latestPurchaseItem->price,
                    'date' => $latestPurchaseItem->date
                ];
            }
        }

        // 2. Check Price Lists
        // Assuming Price Lists are global for now, or we pick the default one.
        // User said "each product have a pricelist".
        // Let's find any price list item for this product.
        // In a complex system, we would check if the Party has a specific price_list_id assigned.
        // For now, we query all price lists valid for this item.

        $priceListItems = PriceListItem::join('price_lists', 'price_lists.id', '=', 'price_list_items.price_list_id')
            ->where('price_lists.company_id', $companyId)
            ->where('price_list_items.item_id', $itemId)
            ->select('price_list_items.price', 'price_lists.updated_at as date', 'price_lists.name')
            ->get();

        foreach ($priceListItems as $pli) {
            $candidates[] = [
                'source' => 'Price List: ' . $pli->name,
                'price' => $pli->price,
                'date' => $pli->date // Price List update date
            ];
        }

        // 3. Determine Winner (Latest Date)
        if (empty($candidates)) {
            return response()->json(['price' => 0, 'source' => 'None']);
        }

        // Sort by date desc
        usort($candidates, function ($a, $b) {
            return strtotime($b['date']) - strtotime($a['date']);
        });

        $winner = $candidates[0];

        return response()->json($winner);
    }

    public function index(Request $request)
    {
        $companyId = $request->header('X-Company-ID');
        $query = PriceList::where('company_id', $companyId)->withCount('items');
        if ($request->has('search')) {
            $query->where('name', 'like', '%' . $request->search . '%');
        }
        return response()->json($query->paginate(20));
    }

    public function store(Request $request)
    {
        $accountId = $request->header('X-Company-ID');
        $request->validate([
            'name' => 'required|string|max:255',
        ]);

        $priceList = PriceList::create([
            'name' => $request->name,
            'account_id' => $accountId
        ]);

        return response()->json($priceList, 201);
    }

    public function show($id)
    {
        $priceList = PriceList::with('items.item')->findOrFail($id);
        return response()->json($priceList);
    }

    public function update(Request $request, $id)
    {
        $priceList = PriceList::findOrFail($id);
        $priceList->update($request->only('name'));
        return response()->json($priceList);
    }

    public function destroy($id)
    {
        $priceList = PriceList::findOrFail($id);
        $priceList->delete();
        return response()->json(['message' => 'Deleted successfully']);
    }

    // Add items to price list
    public function addItems(Request $request, $id)
    {
        $request->validate([
            'items' => 'required|array',
            'items.*.item_id' => 'required|integer|exists:items,id',
            'items.*.price' => 'required|numeric|min:0'
        ]);

        $priceList = PriceList::findOrFail($id);

        foreach ($request->items as $itemData) {
            PriceListItem::updateOrCreate(
                [
                    'price_list_id' => $id,
                    'item_id' => $itemData['item_id']
                ],
                ['price' => $itemData['price']]
            );
        }

        // Touch the price list to update 'updated_at' so it becomes the latest price source
        $priceList->touch();

        return response()->json(['message' => 'Items added/updated successfully']);
    }
}
