<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Grn;
use App\Models\GrnItem;
use App\Models\Purchase;
use App\Models\Item;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class GrnWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();

        $query = Grn::query()
            ->with(['purchase.party', 'items.item'])
            ->withCount('items')
            ->where('account_id', $accountId);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('grn_no', 'like', "%{$search}%")
                    ->orWhere('id', 'like', "%{$search}%")
                    ->orWhereHas('purchase', function ($q) use ($search) {
                        $q->whereHas('party', function ($q) use ($search) {
                            $q->where('name', 'like', "%{$search}%");
                        });
                    });
            });
        }

        $grns = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        $grns->getCollection()->transform(function ($grn) {
            return [
                'id' => $grn->id,
                'grn_no' => $grn->grn_no,
                'date' => $grn->date?->format('Y-m-d'),
                'vehicle_no' => $grn->vehicle_no,
                'invoice_no' => $grn->invoice_no,
                'notes' => $grn->notes,
                'purchase_id' => $grn->purchase_id,
                'party_name' => $grn->purchase?->party?->name ?? 'Unknown',
                'items_count' => $grn->items_count,
                'items' => $grn->items->map(fn($item) => [
                    'id' => $item->id,
                    'item_id' => $item->item_id,
                    'item_name' => $item->item?->name ?? 'Unknown',
                    'quantity' => $item->quantity,
                ]),
            ];
        });

        // Fetch recent purchases for the create modal
        $purchases = Purchase::where('account_id', $accountId)
            ->with('party', 'items.item')
            ->orderBy('date', 'desc')
            ->limit(50)
            ->get()
            ->map(fn($p) => [
                'id' => $p->id,
                'label' => "Purchase #{$p->id} - " . ($p->party?->name ?? 'Unknown'),
                'items' => $p->items->map(fn($i) => [
                    'item_id' => $i->item_id,
                    'item_name' => $i->item?->name,
                    'qty' => $i->qty
                ])
            ]);

        $items = Item::where('account_id', $accountId)->orderBy('name')->get(['id', 'name']);

        return Inertia::render('Grns/Index', [
            'grns' => $grns,
            'purchases' => $purchases,
            'items' => $items,
            'filters' => $request->only(['search']),
        ]);
    }

    public function store(Request $request)
    {
        $accountId = $this->getAccountId();

        $request->validate([
            'purchase_id' => 'required|exists:purchases,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'invoice_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        DB::transaction(function () use ($request, $accountId) {
            $grn = Grn::create([
                'purchase_id' => $request->purchase_id,
                'grn_no' => Grn::generateNumber($accountId),
                'date' => $request->date,
                'vehicle_no' => $request->vehicle_no,
                'invoice_no' => $request->invoice_no,
                'notes' => $request->notes,
                'account_id' => $accountId,
                'log_id' => 1,
            ]);

            foreach ($request->items as $item) {
                GrnItem::create([
                    'grn_id' => $grn->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            $grn->refresh();
            $grn->adjustStockIncrease();
        });

        return redirect()->route('grns.index')->with('success', 'GRN created.');
    }

    public function update(Request $request, Grn $grn)
    {
        $request->validate([
            'purchase_id' => 'required|exists:purchases,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'invoice_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        DB::transaction(function () use ($request, $grn) {
            $grn->reverseStockAdjustment();

            $grn->update([
                'purchase_id' => $request->purchase_id,
                'date' => $request->date,
                'vehicle_no' => $request->vehicle_no,
                'invoice_no' => $request->invoice_no,
                'notes' => $request->notes,
            ]);

            $grn->items()->delete();

            foreach ($request->items as $item) {
                GrnItem::create([
                    'grn_id' => $grn->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            $grn->refresh();
            $grn->adjustStockIncrease();
        });

        return redirect()->route('grns.index')->with('success', 'GRN updated.');
    }

    public function destroy(Grn $grn)
    {
        DB::transaction(function () use ($grn) {
            $grn->reverseStockAdjustment();
            $grn->delete();
        });
        return redirect()->route('grns.index')->with('success', 'GRN deleted.');
    }
}
