<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\PriceList;
use App\Models\PriceListItem;
use App\Models\Item;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class PriceListWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $query = PriceList::query()
            ->where('account_id', $accountId)
            ->withCount('items');

        if ($request->filled('search')) {
            $query->where('name', 'like', "%{$request->search}%");
        }

        $priceLists = $query->orderBy('name')->paginate(20)->withQueryString();

        return Inertia::render('PriceLists/Index', [
            'priceLists' => $priceLists,
            'filters' => $request->only(['search']),
        ]);
    }

    public function store(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $request->validate([
            'name' => 'required|string|max:255',
        ]);

        PriceList::create([
            'name' => $request->name,
            'account_id' => $accountId,
        ]);

        return redirect()->back()->with('success', 'Price List created successfully.');
    }

    public function update(Request $request, PriceList $priceList)
    {
        $request->validate([
            'name' => 'required|string|max:255',
        ]);

        $priceList->update(['name' => $request->name]);

        return redirect()->back()->with('success', 'Price List updated successfully.');
    }

    public function destroy(PriceList $priceList)
    {
        $priceList->delete();
        return redirect()->back()->with('success', 'Price List deleted successfully.');
    }

    public function show(PriceList $priceList)
    {
        $accountId = $this->getAccountId();
        
        $priceList->load(['items.item']);
        
        $items = Item::where('account_id', $accountId)->orderBy('name')->get(['id', 'name', 'code', 'price']);

        return Inertia::render('PriceLists/Show', [
            'priceList' => $priceList,
            'allItems' => $items,
        ]);
    }

    public function updateItems(Request $request, PriceList $priceList)
    {
        $request->validate([
            'items' => 'array',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
        ]);

        DB::transaction(function () use ($request, $priceList) {
            // We can either sync or update. Let's update/create.
            foreach ($request->items as $itemData) {
                PriceListItem::updateOrCreate(
                    [
                        'price_list_id' => $priceList->id,
                        'item_id' => $itemData['item_id'],
                    ],
                    [
                        'price' => $itemData['price'],
                    ]
                );
            }
        });

        return redirect()->back()->with('success', 'Prices updated successfully.');
    }
    
    public function removeItem(PriceList $priceList, $itemId)
    {
        PriceListItem::where('price_list_id', $priceList->id)
            ->where('item_id', $itemId)
            ->delete();
            
        return redirect()->back()->with('success', 'Item removed from price list.');
    }
}
