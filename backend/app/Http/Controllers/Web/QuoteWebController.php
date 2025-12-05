<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Quote;
use App\Models\QuoteItem;
use App\Models\Item;
use App\Models\Party;
use App\Models\Tax;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class QuoteWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    /**
     * Display a listing of quotes.
     */
    public function index(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $query = Quote::query()
            ->with(['party', 'items.item', 'items.tax'])
            ->withCount('items')
            ->where('account_id', $accountId);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('id', 'like', "%{$search}%")
                  ->orWhereHas('party', function($q) use ($search) {
                      $q->where('name', 'like', "%{$search}%");
                  });
            });
        }

        $quotes = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        // Transform data for frontend
        $quotes->getCollection()->transform(function ($quote) {
            $subtotal = $quote->items->sum(function ($item) {
                return $item->qty * $item->price;
            });
            
            $taxAmount = $quote->items->sum(function ($item) {
                if ($item->tax) {
                    $lineTotal = $item->qty * $item->price;
                    $taxRate = ($item->tax->tax1_val ?? 0) + ($item->tax->tax2_val ?? 0);
                    return $lineTotal * ($taxRate / 100);
                }
                return 0;
            });

            return [
                'id' => $quote->id,
                'party_name' => $quote->party ? $quote->party->name : 'Unknown',
                'party_id' => $quote->party_id,
                'date' => $quote->date ? $quote->date->format('Y-m-d') : null,
                'subtotal' => $subtotal,
                'tax_amount' => $taxAmount,
                'total' => $subtotal + $taxAmount,
                'items_count' => $quote->items_count,
                'items' => $quote->items->map(function ($item) {
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
        $totalQuotes = Quote::where('account_id', $accountId)->count();
        $thisMonth = Quote::where('account_id', $accountId)
            ->whereMonth('date', now()->month)
            ->whereYear('date', now()->year)
            ->count();
        $totalValue = Quote::where('account_id', $accountId)
            ->with('items')
            ->get()
            ->sum(function ($quote) {
                return $quote->items->sum(function ($item) {
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

        return Inertia::render('Quotes/Index', [
            'quotes' => $quotes,
            'parties' => $parties,
            'items' => $items,
            'taxes' => $taxes,
            'stats' => [
                'total' => $totalQuotes,
                'this_month' => $thisMonth,
                'total_value' => $totalValue,
            ],
            'filters' => $request->only(['search']),
        ]);
    }

    /**
     * Store a newly created quote.
     */
    public function store(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::beginTransaction();
        try {
            $quote = Quote::create([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
                'account_id' => $accountId,
            ]);

            foreach ($validated['items'] as $item) {
                QuoteItem::create([
                    'quote_id' => $quote->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'account_id' => $accountId,
                ]);
            }

            DB::commit();

            return redirect()->route('quotes.index')->with('success', 'Quote created successfully.');
        } catch (\Exception $e) {
            DB::rollBack();
            return back()->withErrors(['error' => 'Failed to create quote: ' . $e->getMessage()]);
        }
    }

    /**
     * Update the specified quote.
     */
    public function update(Request $request, Quote $quote)
    {
        $accountId = $this->getAccountId();
        
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::beginTransaction();
        try {
            $quote->update([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
            ]);

            // Delete existing items and recreate
            $quote->items()->delete();

            foreach ($validated['items'] as $item) {
                QuoteItem::create([
                    'quote_id' => $quote->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'account_id' => $accountId,
                ]);
            }

            DB::commit();

            return redirect()->route('quotes.index')->with('success', 'Quote updated successfully.');
        } catch (\Exception $e) {
            DB::rollBack();
            return back()->withErrors(['error' => 'Failed to update quote: ' . $e->getMessage()]);
        }
    }

    /**
     * Remove the specified quote.
     */
    public function destroy(Quote $quote)
    {
        try {
            $quote->items()->delete();
            $quote->delete();

            return redirect()->route('quotes.index')->with('success', 'Quote deleted successfully.');
        } catch (\Exception $e) {
            return back()->withErrors(['error' => 'Failed to delete quote: ' . $e->getMessage()]);
        }
    }
}
