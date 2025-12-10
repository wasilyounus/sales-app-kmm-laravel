<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\DeliveryNote;
use App\Models\DeliveryNoteItem;
use App\Models\Sale;
use App\Models\Item;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class DeliveryNoteWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();

        $query = DeliveryNote::query()
            ->with(['sale.party', 'items.item'])
            ->withCount('items')
            ->where('account_id', $accountId);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('dn_no', 'like', "%{$search}%")
                    ->orWhere('id', 'like', "%{$search}%")
                    ->orWhereHas('sale', function ($q) use ($search) {
                        $q->whereHas('party', function ($q) use ($search) {
                            $q->where('name', 'like', "%{$search}%");
                        });
                    });
            });
        }

        $deliveryNotes = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        $deliveryNotes->getCollection()->transform(function ($dn) {
            return [
                'id' => $dn->id,
                'dn_no' => $dn->dn_no,
                'date' => $dn->date?->format('Y-m-d'),
                'vehicle_no' => $dn->vehicle_no,
                'lr_no' => $dn->lr_no,
                'notes' => $dn->notes,
                'sale_id' => $dn->sale_id,
                'party_name' => $dn->sale?->party?->name ?? 'Unknown',
                'items_count' => $dn->items_count,
                'items' => $dn->items->map(fn($item) => [
                    'id' => $item->id,
                    'item_id' => $item->item_id,
                    'item_name' => $item->item?->name ?? 'Unknown',
                    'quantity' => $item->quantity,
                ]),
            ];
        });

        // Fetch recent sales for the create modal (simplified)
        $sales = Sale::where('account_id', $accountId)
            ->with('party', 'items.item')
            ->orderBy('date', 'desc')
            ->limit(50)
            ->get()
            ->map(fn($sale) => [
                'id' => $sale->id,
                'label' => "Sale #{$sale->id} - " . ($sale->party?->name ?? 'Unknown'),
                'items' => $sale->items->map(fn($i) => [
                    'item_id' => $i->item_id,
                    'item_name' => $i->item?->name,
                    'qty' => $i->qty
                ])
            ]);

        $items = Item::where('account_id', $accountId)->orderBy('name')->get(['id', 'name']);

        return Inertia::render('DeliveryNotes/Index', [
            'deliveryNotes' => $deliveryNotes,
            'sales' => $sales,
            'items' => $items,
            'filters' => $request->only(['search']),
        ]);
    }

    public function store(Request $request)
    {
        $accountId = $this->getAccountId();

        $request->validate([
            'sale_id' => 'required|exists:sales,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'lr_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        DB::transaction(function () use ($request, $accountId) {
            $dn = DeliveryNote::create([
                'sale_id' => $request->sale_id,
                'dn_no' => DeliveryNote::generateNumber($accountId),
                'date' => $request->date,
                'vehicle_no' => $request->vehicle_no,
                'lr_no' => $request->lr_no,
                'notes' => $request->notes,
                'account_id' => $accountId,
                'log_id' => 1,
            ]);

            foreach ($request->items as $item) {
                DeliveryNoteItem::create([
                    'delivery_note_id' => $dn->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            $dn->refresh();
            $dn->adjustStockDecrease();
        });

        return redirect()->route('delivery-notes.index')->with('success', 'Delivery Note created.');
    }

    public function update(Request $request, DeliveryNote $deliveryNote)
    {
        $request->validate([
            'sale_id' => 'required|exists:sales,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'lr_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        DB::transaction(function () use ($request, $deliveryNote) {
            $deliveryNote->reverseStockAdjustment();

            $deliveryNote->update([
                'sale_id' => $request->sale_id,
                'date' => $request->date,
                'vehicle_no' => $request->vehicle_no,
                'lr_no' => $request->lr_no,
                'notes' => $request->notes,
            ]);

            $deliveryNote->items()->delete();

            foreach ($request->items as $item) {
                DeliveryNoteItem::create([
                    'delivery_note_id' => $deliveryNote->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            $deliveryNote->refresh();
            $deliveryNote->adjustStockDecrease();
        });

        return redirect()->route('delivery-notes.index')->with('success', 'Delivery Note updated.');
    }

    public function destroy(DeliveryNote $deliveryNote)
    {
        DB::transaction(function () use ($deliveryNote) {
            $deliveryNote->reverseStockAdjustment();
            $deliveryNote->delete();
        });
        return redirect()->route('delivery-notes.index')->with('success', 'Delivery Note deleted.');
    }
}
