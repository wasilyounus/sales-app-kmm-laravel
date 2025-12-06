<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Sale;
use App\Models\SaleItem;
use App\Models\Item;
use App\Models\Party;
use App\Models\Tax;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class SaleWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    /**
     * Display a listing of sales.
     */
    public function index(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $query = Sale::query()
            ->with(['party', 'items.item', 'items.tax', 'tax'])
            ->withCount('items')
            ->where('account_id', $accountId);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('invoice_no', 'like', "%{$search}%")
                  ->orWhereHas('party', function($q) use ($search) {
                      $q->where('name', 'like', "%{$search}%");
                  });
            });
        }

        $sales = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        // Transform data for frontend
        $sales->getCollection()->transform(function ($sale) {
            $subtotal = $sale->items->sum(function ($item) {
                return $item->qty * $item->price;
            });
            
            $taxAmount = $sale->items->sum(function ($item) {
                if ($item->tax) {
                    $lineTotal = $item->qty * $item->price;
                    $taxRate = ($item->tax->tax1_val ?? 0) + ($item->tax->tax2_val ?? 0);
                    return $lineTotal * ($taxRate / 100);
                }
                return 0;
            });

            return [
                'id' => $sale->id,
                'invoice_no' => $sale->invoice_no,
                'party_name' => $sale->party ? $sale->party->name : 'Unknown',
                'party_id' => $sale->party_id,
                'date' => $sale->date ? $sale->date->format('Y-m-d') : null,
                'subtotal' => $subtotal,
                'tax_amount' => $taxAmount,
                'total' => $subtotal + $taxAmount,
                'items_count' => $sale->items_count,
                'tax_id' => $sale->tax_id,
                'items' => $sale->items->map(function ($item) {
                    return [
                        'id' => $item->id,
                        'item_id' => $item->item_id,
                        'item_name' => $item->item ? $item->item->name : 'Unknown',
                        'price' => $item->price,
                        'qty' => $item->qty,
                        'tax_id' => $item->tax_id,
                        'tax_name' => $item->tax ? $item->tax->scheme_name : null,
                    ];
                }),
            ];
        });

        // Get stats
        $totalSales = Sale::where('account_id', $accountId)->count();
        $thisMonth = Sale::where('account_id', $accountId)
            ->whereMonth('date', now()->month)
            ->whereYear('date', now()->year)
            ->count();
        $totalValue = Sale::where('account_id', $accountId)
            ->with('items')
            ->get()
            ->sum(function ($sale) {
                return $sale->items->sum(function ($item) {
                    return $item->qty * $item->price;
                });
            });

        // Get parties and items with taxes
        $parties = Party::where('account_id', $accountId)->orderBy('name')->get(['id', 'name']);
        $items = Item::where('account_id', $accountId)
            ->with('tax')
            ->orderBy('name')
            ->get()
            ->map(function ($item) {
                return [
                    'id' => $item->id,
                    'name' => $item->name,
                    'tax_id' => $item->tax_id,
                ];
            });
        
        // Get account's country to filter taxes
        $account = \App\Models\Account::find($accountId);
        $taxCountry = $account?->country;
        
        $taxes = Tax::where('active', true)
            ->when($taxCountry, function($query) use ($taxCountry) {
                return $query->where('country', $taxCountry);
            })
            ->get()
            ->map(function ($tax) {
                $rate = ($tax->tax1_val ?? 0) + ($tax->tax2_val ?? 0);
                return [
                    'id' => $tax->id,
                    'name' => $tax->scheme_name,
                    'rate' => $rate,
                ];
            });

        return Inertia::render('Sales/Index', [
            'sales' => $sales,
            'parties' => $parties,
            'items' => $items,
            'taxes' => $taxes,
            'stats' => [
                'total' => $totalSales,
                'this_month' => $thisMonth,
                'total_value' => $totalValue,
            ],
            'filters' => $request->only(['search']),
            // Account tax settings
            'taxSettings' => [
                'level' => $account?->tax_application_level ?? 'item',
                'default_tax_id' => $account?->default_tax_id,
            ],
        ]);
    }

    /**
     * Store a newly created sale.
     */
    public function store(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'invoice_no' => 'nullable|string|max:50',
            'tax_id' => 'nullable|exists:taxes,id',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::transaction(function () use ($request, $accountId) {
            $sale = Sale::create([
                'party_id' => $request->party_id,
                'date' => $request->date,
                'invoice_no' => $request->invoice_no,
                'tax_id' => $request->tax_id,
                'account_id' => $accountId,
                'log_id' => 1,
            ]);

            foreach ($request->items as $item) {
                SaleItem::create([
                    'sale_id' => $sale->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                ]);
            }
        });

        return redirect()->route('sales.index')->with('success', 'Sale created successfully.');
    }

    /**
     * Update the specified sale.
     */
    public function update(Request $request, Sale $sale)
    {
        $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'invoice_no' => 'nullable|string|max:50',
            'tax_id' => 'nullable|exists:taxes,id',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::transaction(function () use ($request, $sale) {
            $sale->update([
                'party_id' => $request->party_id,
                'date' => $request->date,
                'invoice_no' => $request->invoice_no,
                'tax_id' => $request->tax_id,
            ]);

            // Delete existing items and recreate
            $sale->items()->delete();

            foreach ($request->items as $item) {
                SaleItem::create([
                    'sale_id' => $sale->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                ]);
            }
        });

        return redirect()->route('sales.index')->with('success', 'Sale updated successfully.');
    }

    /**
     * Remove the specified sale.
     */
    public function destroy(Sale $sale)
    {
        $sale->delete();
        return redirect()->route('sales.index')->with('success', 'Sale deleted successfully.');
    }

    /**
     * Display the specified resource.
     */
    public function show(Sale $sale)
    {
        return Inertia::render('Sales/Show', [
            'sale' => $sale->load(['party', 'items.item', 'items.tax']),
        ]);
    }
}
