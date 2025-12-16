<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\DeliveryNote;
use App\Models\DeliveryNoteItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DeliveryNoteController extends Controller
{
    public function index(Request $request)
    {
        $companyId = $request->input('company_id');

        $query = DeliveryNote::with(['items.item', 'sale.party'])
            ->orderBy('date', 'desc');

        if ($companyId) {
            $query->where('company_id', $companyId);
        }

        $deliveryNotes = $query->get();

        return response()->json([
            'data' => $deliveryNotes
        ]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'company_id' => 'required|exists:companies,id',
            'sale_id' => 'required|exists:sales,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'lr_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        $accountId = $validated['account_id'];

        return DB::transaction(function () use ($validated, $accountId) {
            // Create delivery note
            $deliveryNote = DeliveryNote::create([
                'sale_id' => $validated['sale_id'],
                'dn_no' => DeliveryNote::generateNumber($accountId),
                'date' => $validated['date'],
                'vehicle_no' => $validated['vehicle_no'] ?? null,
                'lr_no' => $validated['lr_no'] ?? null,
                'notes' => $validated['notes'] ?? null,
                'company_id' => $accountId,
            ]);

            // Create items
            foreach ($validated['items'] as $item) {
                DeliveryNoteItem::create([
                    'delivery_note_id' => $deliveryNote->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            // Reload and adjust stock (decrease for delivery)
            $deliveryNote->refresh();
            $deliveryNote->adjustStockDecrease();
            $deliveryNote->load('items.item', 'sale.party');

            return response()->json([
                'message' => 'Delivery note created successfully',
                'data' => $deliveryNote
            ], 201);
        });
    }

    public function update(Request $request, $id)
    {
        $deliveryNote = DeliveryNote::findOrFail($id);

        $validated = $request->validate([
            'sale_id' => 'sometimes|required|exists:sales,id',
            'date' => 'sometimes|required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'lr_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.quantity' => 'required_with:items|numeric|min:0.001',
        ]);

        return DB::transaction(function () use ($validated, $deliveryNote) {
            // Reverse stock before updating
            $deliveryNote->reverseStockAdjustment();

            // Update delivery note details
            $deliveryNote->update([
                'date' => $validated['date'] ?? $deliveryNote->date,
                'vehicle_no' => $validated['vehicle_no'] ?? $deliveryNote->vehicle_no,
                'lr_no' => $validated['lr_no'] ?? $deliveryNote->lr_no,
                'notes' => $validated['notes'] ?? $deliveryNote->notes,
            ]);

            if (isset($validated['items'])) {
                // Remove old items
                DeliveryNoteItem::where('delivery_note_id', $deliveryNote->id)->delete();

                // Create new items
                foreach ($validated['items'] as $item) {
                    DeliveryNoteItem::create([
                        'delivery_note_id' => $deliveryNote->id,
                        'item_id' => $item['item_id'],
                        'quantity' => $item['quantity'],
                    ]);
                }
            }

            // Adjust stock with new details (decrease for delivery)
            $deliveryNote->refresh();
            $deliveryNote->adjustStockDecrease();
            $deliveryNote->load('items.item', 'sale.party');

            return response()->json([
                'message' => 'Delivery note updated successfully',
                'data' => $deliveryNote
            ]);
        });
    }

    public function show($id)
    {
        $deliveryNote = DeliveryNote::with(['items.item', 'sale.party'])
            ->findOrFail($id);

        return response()->json([
            'data' => $deliveryNote
        ]);
    }

    public function destroy($id)
    {
        $deliveryNote = DeliveryNote::with('items')
            ->findOrFail($id);

        // Reverse stock before deleting
        $deliveryNote->reverseStockAdjustment();
        $deliveryNote->delete();

        return response()->json([
            'message' => 'Delivery note deleted successfully'
        ]);
    }
}
