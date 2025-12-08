<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Grn;
use App\Models\GrnItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class GrnController extends Controller
{
    public function index($accountId)
    {
        $grns = Grn::with(['items.item', 'purchase.party'])
            ->where('account_id', $accountId)
            ->orderBy('date', 'desc')
            ->get();

        return response()->json([
            'data' => $grns
        ]);
    }

    public function store(Request $request, $accountId)
    {
        $validated = $request->validate([
            'purchase_id' => 'required|exists:purchases,id',
            'date' => 'required|date',
            'vehicle_no' => 'nullable|string|max:50',
            'invoice_no' => 'nullable|string|max:100',
            'notes' => 'nullable|string',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.quantity' => 'required|numeric|min:0.001',
        ]);

        return DB::transaction(function () use ($validated, $accountId) {
            // Create GRN
            $grn = Grn::create([
                'purchase_id' => $validated['purchase_id'],
                'grn_number' => Grn::generateNumber($accountId),
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

    public function show($accountId, $id)
    {
        $grn = Grn::with(['items.item', 'purchase.party'])
            ->where('account_id', $accountId)
            ->findOrFail($id);

        return response()->json([
            'data' => $grn
        ]);
    }

    public function destroy($accountId, $id)
    {
        $grn = Grn::with('items')
            ->where('account_id', $accountId)
            ->findOrFail($id);

        // Reverse stock before deleting
        $grn->reverseStockAdjustment();
        $grn->delete();

        return response()->json([
            'message' => 'GRN deleted successfully'
        ]);
    }
}
