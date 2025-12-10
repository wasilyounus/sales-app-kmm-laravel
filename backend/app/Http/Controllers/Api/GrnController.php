<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Grn;
use App\Models\GrnItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class GrnController extends Controller
{
    public function index(Request $request)
    {
        $accountId = $request->input('account_id');

        $query = Grn::with(['items.item', 'purchase.party'])
            ->orderBy('date', 'desc');

        if ($accountId) {
            $query->where('account_id', $accountId);
        }

        $grns = $query->get();

        return response()->json([
            'data' => $grns
        ]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'account_id' => 'required|exists:accounts,id',
            'purchase_id' => 'required|exists:purchases,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'invoice_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        $accountId = $validated['account_id'];

        return DB::transaction(function () use ($validated, $accountId) {
            // Create GRN
            $grn = Grn::create([
                'purchase_id' => $validated['purchase_id'],
                'grn_no' => Grn::generateNumber($accountId),
                'date' => $validated['date'],
                'vehicle_no' => $validated['vehicle_no'] ?? null,
                'invoice_no' => $validated['invoice_no'] ?? null,
                'notes' => $validated['notes'] ?? null,
                'account_id' => $accountId,
            ]);

            // Create items
            foreach ($validated['items'] as $item) {
                GrnItem::create([
                    'grn_id' => $grn->id,
                    'item_id' => $item['item_id'],
                    'quantity' => $item['quantity'],
                ]);
            }

            // Reload and adjust stock (increase for GRN)
            $grn->refresh();
            $grn->adjustStockIncrease();
            $grn->load('items.item', 'purchase.party');

            return response()->json([
                'message' => 'GRN created successfully',
                'data' => $grn
            ], 201);
        });
    }

    public function update(Request $request, $id)
    {
        $grn = Grn::findOrFail($id);

        $validated = $request->validate([
            'purchase_id' => 'sometimes|required|exists:purchases,id',
            'date' => 'sometimes|required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'invoice_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.quantity' => 'required_with:items|numeric|min:0.001',
        ]);

        return DB::transaction(function () use ($validated, $grn) {
            // Reverse stock before updating
            $grn->reverseStockAdjustment();

            // Update GRN details
            $grn->update([
                'date' => $validated['date'] ?? $grn->date,
                'vehicle_no' => $validated['vehicle_no'] ?? $grn->vehicle_no,
                'invoice_no' => $validated['invoice_no'] ?? $grn->invoice_no,
                'notes' => $validated['notes'] ?? $grn->notes,
            ]);

            if (isset($validated['items'])) {
                // Remove old items
                GrnItem::where('grn_id', $grn->id)->delete();

                // Create new items
                foreach ($validated['items'] as $item) {
                    GrnItem::create([
                        'grn_id' => $grn->id,
                        'item_id' => $item['item_id'],
                        'quantity' => $item['quantity'],
                    ]);
                }
            }

            // Adjust stock with new details (increase for GRN)
            $grn->refresh();
            $grn->adjustStockIncrease();
            $grn->load('items.item', 'purchase.party');

            return response()->json([
                'message' => 'GRN updated successfully',
                'data' => $grn
            ]);
        });
    }

    public function show($id)
    {
        $grn = Grn::with(['items.item', 'purchase.party'])
            ->findOrFail($id);

        return response()->json([
            'data' => $grn
        ]);
    }

    public function destroy($id)
    {
        $grn = Grn::with('items')
            ->findOrFail($id);

        // Reverse stock before deleting
        $grn->reverseStockAdjustment();
        $grn->delete();

        return response()->json([
            'message' => 'GRN deleted successfully'
        ]);
    }
}
