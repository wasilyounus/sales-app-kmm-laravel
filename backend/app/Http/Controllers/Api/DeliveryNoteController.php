<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\DeliveryNote;
use App\Models\DeliveryNoteItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DeliveryNoteController extends Controller
{
    public function index($accountId)
    {
        $deliveryNotes = DeliveryNote::with(['items.item', 'sale.party'])
            ->where('account_id', $accountId)
            ->orderBy('date', 'desc')
            ->get();

        return response()->json([
            'data' => $deliveryNotes
        ]);
    }

    public function store(Request $request, $accountId)
    {
        $validated = $request->validate([
            'sale_id' => 'required|exists:sales,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'lr_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        return DB::transaction(function () use ($validated, $accountId) {
            // Create delivery note
            $deliveryNote = DeliveryNote::create([
                'sale_id' => $validated['sale_id'],
                'dn_number' => DeliveryNote::generateNumber($accountId),
                'date' => $validated['date'],
                'vehicle_no' => $validated['vehicle_no'] ?? null,
                'lr_no' => $validated['lr_no'] ?? null,
                'notes' => $validated['notes'] ?? null,
                'account_id' => $accountId,
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

    public function show($accountId, $id)
    {
        $deliveryNote = DeliveryNote::with(['items.item', 'sale.party'])
            ->where('account_id', $accountId)
            ->findOrFail($id);

        return response()->json([
            'data' => $deliveryNote
        ]);
    }

    public function destroy($accountId, $id)
    {
        $deliveryNote = DeliveryNote::with('items')
            ->where('account_id', $accountId)
            ->findOrFail($id);

        // Reverse stock before deleting
        $deliveryNote->reverseStockAdjustment();
        $deliveryNote->delete();

        return response()->json([
            'message' => 'Delivery note deleted successfully'
        ]);
    }
}
