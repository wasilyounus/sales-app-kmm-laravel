<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Sale;
use App\Models\SaleItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class SaleController extends Controller
{
    /**
     * Display a listing of sales
     */
    public function index(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Sale::with(['party', 'items.item', 'items.tax', 'supply']);
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $sales = $query->orderBy('date', 'desc')->get();
        
        return response()->json([
            'success' => true,
            'data' => $sales,
        ]);
    }

    /**
     * Store a newly created sale with items
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'invoice_no' => 'nullable|string|max:255',
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
            $sale = Sale::create([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
                'invoice_no' => $validated['invoice_no'] ?? null,
                'account_id' => $validated['account_id'],
                'log_id' => $validated['log_id'],
            ]);

            foreach ($validated['items'] as $item) {
                SaleItem::create([
                    'sale_id' => $sale->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'account_id' => $validated['account_id'],
                    'log_id' => $validated['log_id'],
                ]);
            }

            DB::commit();

            $sale->load(['party', 'items.item', 'items.tax']);

            return response()->json([
                'success' => true,
                'message' => 'Sale created successfully',
                'data' => $sale,
            ], 201);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create sale',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Display the specified sale
     */
    public function show($id)
    {
        $sale = Sale::with(['party', 'items.item', 'items.tax', 'supply'])->findOrFail($id);
        
        return response()->json([
            'success' => true,
            'data' => $sale,
        ]);
    }

    /**
     * Update the specified sale
     */
    public function update(Request $request, $id)
    {
        $sale = Sale::findOrFail($id);
        
        $validated = $request->validate([
            'party_id' => 'sometimes|required|exists:parties,id',
            'date' => 'sometimes|required|date',
            'invoice_no' => 'nullable|string|max:255',
            'log_id' => 'sometimes|required|integer',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.price' => 'required_with:items|numeric|min:0',
            'items.*.qty' => 'required_with:items|numeric|min:0',
            'items.*.tax_id' => 'nullable|exists:taxes,id',
        ]);

        DB::beginTransaction();
        try {
            $sale->update($validated);

            if (isset($validated['items'])) {
                SaleItem::where('sale_id', $sale->id)->delete();
                
                foreach ($validated['items'] as $item) {
                    SaleItem::create([
                        'sale_id' => $sale->id,
                        'item_id' => $item['item_id'],
                        'price' => $item['price'],
                        'qty' => $item['qty'],
                        'tax_id' => $item['tax_id'] ?? null,
                        'account_id' => $sale->account_id,
                        'log_id' => $validated['log_id'] ?? $sale->log_id,
                    ]);
                }
            }

            DB::commit();

            $sale->load(['party', 'items.item', 'items.tax']);

            return response()->json([
                'success' => true,
                'message' => 'Sale updated successfully',
                'data' => $sale,
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to update sale',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Remove the specified sale (soft delete)
     */
    public function destroy($id)
    {
        $sale = Sale::findOrFail($id);
        $sale->delete();

        return response()->json([
            'success' => true,
            'message' => 'Sale deleted successfully',
        ]);
    }
}
