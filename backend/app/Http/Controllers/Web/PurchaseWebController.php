<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Purchase;
use App\Models\PurchaseItem;
use App\Models\Item;
use App\Models\Party;
use App\Models\Tax;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class PurchaseWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();

        $query = Purchase::query()
            ->with(['party', 'items.item', 'items.tax', 'tax'])
            ->withCount('items')
            ->where('account_id', $accountId);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('id', 'like', "%{$search}%")
                    ->orWhereHas('party', function ($q) use ($search) {
                        $q->where('name', 'like', "%{$search}%");
                    });
            });
        }

        $purchases = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        $purchases->getCollection()->transform(function ($purchase) {
            $subtotal = $purchase->items->sum(fn($item) => $item->qty * $item->price);
            $taxAmount = $purchase->items->sum(function ($item) {
                if ($item->tax) {
                    $lineTotal = $item->qty * $item->price;
                    $taxRate = ($item->tax->tax1_val ?? 0) + ($item->tax->tax2_val ?? 0);
                    return $lineTotal * ($taxRate / 100);
                }
                return 0;
            });

            return [
                'id' => $purchase->id,
                'party_name' => $purchase->party?->name ?? 'Unknown',
                'party_id' => $purchase->party_id,
                'date' => $purchase->date?->format('Y-m-d'),
                'subtotal' => $subtotal,
                'tax_amount' => $taxAmount,
                'total' => $subtotal + $taxAmount,
                'items_count' => $purchase->items_count,
                'tax_id' => $purchase->tax_id,
                'items' => $purchase->items->map(fn($item) => [
                    'id' => $item->id,
                    'item_id' => $item->item_id,
                    'item_name' => $item->item?->name ?? 'Unknown',
                    'price' => $item->price,
                    'qty' => $item->qty,
                    'tax_id' => $item->tax_id,
                ]),
            ];
        });

        $totalPurchases = Purchase::where('account_id', $accountId)->count();
        $thisMonth = Purchase::where('account_id', $accountId)
            ->whereMonth('date', now()->month)->whereYear('date', now()->year)->count();
        $totalValue = Purchase::where('account_id', $accountId)->with('items')->get()
            ->sum(fn($p) => $p->items->sum(fn($i) => $i->qty * $i->price));

        $parties = Party::where('account_id', $accountId)->orderBy('name')->get(['id', 'name']);
        $items = Item::where('account_id', $accountId)->with('tax')->orderBy('name')->get()
            ->map(fn($item) => ['id' => $item->id, 'name' => $item->name, 'tax_id' => $item->tax_id]);
        // Get account's country to filter taxes
        $account = \App\Models\Company::find($accountId);
        $taxes = Tax::where('active', true)
            ->when($account?->country, fn($q, $c) => $q->where('country', $c))->get()
            ->map(fn($t) => ['id' => $t->id, 'name' => $t->scheme_name, 'rate' => ($t->tax1_val ?? 0) + ($t->tax2_val ?? 0)]);

        return Inertia::render('Purchases/Index', [
            'purchases' => $purchases,
            'parties' => $parties,
            'items' => $items,
            'taxes' => $taxes,
            'stats' => ['total' => $totalPurchases, 'this_month' => $thisMonth, 'total_value' => $totalValue],
            'filters' => $request->only(['search']),
            'taxSettings' => ['level' => $account?->tax_application_level ?? 'item', 'default_tax_id' => $account?->default_tax_id],
        ]);
    }

    public function store(Request $request)
    {
        $accountId = $this->getAccountId();

        $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'tax_id' => 'nullable|exists:taxes,id',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::transaction(function () use ($request, $accountId) {
            $purchase = Purchase::create([
                'party_id' => $request->party_id,
                'date' => $request->date,
                'tax_id' => $request->tax_id,
                'account_id' => $accountId,
                'log_id' => 1,
            ]);

            foreach ($request->items as $item) {
                PurchaseItem::create([
                    'purchase_id' => $purchase->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                ]);
            }
        });

        return redirect()->route('purchases.index')->with('success', 'Purchase created.');
    }

    public function update(Request $request, Purchase $purchase)
    {
        $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'tax_id' => 'nullable|exists:taxes,id',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0.01',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::transaction(function () use ($request, $purchase) {
            $purchase->update([
                'party_id' => $request->party_id,
                'date' => $request->date,
                'tax_id' => $request->tax_id,
            ]);
            $purchase->items()->delete();
            foreach ($request->items as $item) {
                PurchaseItem::create([
                    'purchase_id' => $purchase->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                ]);
            }
        });

        return redirect()->route('purchases.index')->with('success', 'Purchase updated.');
    }

    public function destroy(Purchase $purchase)
    {
        $purchase->delete();
        return redirect()->route('purchases.index')->with('success', 'Purchase deleted.');
    }
}
