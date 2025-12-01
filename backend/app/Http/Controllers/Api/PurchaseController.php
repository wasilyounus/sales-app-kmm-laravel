<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Purchase;
use App\Models\PurchaseItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PurchaseController extends Controller
{
    public function index(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Purchase::with(['party', 'items.item', 'items.tax']);
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $purchases = $query->orderBy('date', 'desc')->get();
        
        return response()->json([
            'success' => true,
            'data' => $purchases,
        ]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'account_id' => 'required|exists:accounts,id',
            'log_id' => 'required|integer',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::beginTransaction();
        try {
            $purchase = Purchase::create([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
                'account_id' => $validated['account_id'],
                'log_id' => $validated['log_id'],
            ]);

            foreach ($validated['items'] as $item) {
                PurchaseItem::create([
                    'purchase_id' => $purchase->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'account_id' => $validated['account_id'],
                    'log_id' => $validated['log_id'],
                ]);
            }

            DB::commit();
            $purchase->load(['party', 'items.item', 'items.tax']);

            return response()->json([
                'success' => true,
                'message' => 'Purchase created successfully',
                'data' => $purchase,
            ], 201);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create purchase',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function show($id)
    {
        $purchase = Purchase::with(['party', 'items.item', 'items.tax'])->findOrFail($id);
        
        return response()->json([
            'success' => true,
            'data' => $purchase,
        ]);
    }

    public function update(Request $request, $id)
    {
        $purchase = Purchase::findOrFail($id);
        
        $validated = $request->validate([
            'party_id' => 'sometimes|required|exists:parties,id',
            'date' => 'sometimes|required|date',
            'log_id' => 'sometimes|required|integer',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.price' => 'required_with:items|numeric|min:0',
            'items.*.qty' => 'required_with:items|numeric|min:0',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::beginTransaction();
        try {
            $purchase->update($validated);

            if (isset($validated['items'])) {
                PurchaseItem::where('purchase_id', $purchase->id)->delete();
                
                foreach ($validated['items'] as $item) {
                    PurchaseItem::create([
                        'purchase_id' => $purchase->id,
                        'item_id' => $item['item_id'],
                        'price' => $item['price'],
                        'qty' => $item['qty'],
                        'tax_id' => $item['tax_id'] ?? null,
                        'account_id' => $purchase->account_id,
                        'log_id' => $validated['log_id'] ?? $purchase->log_id,
                    ]);
                }
            }

            DB::commit();
            $purchase->load(['party', 'items.item', 'items.tax']);

            return response()->json([
                'success' => true,
                'message' => 'Purchase updated successfully',
                'data' => $purchase,
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to update purchase',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function destroy($id)
    {
        $purchase = Purchase::findOrFail($id);
        $purchase->delete();

        return response()->json([
            'success' => true,
            'message' => 'Purchase deleted successfully',
        ]);
    }
}
