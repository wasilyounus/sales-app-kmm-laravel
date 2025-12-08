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
                'invoice_no' => $validated['invoice_no'] ?? Sale::generateNumber($validated['account_id']),
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

            // Auto-create Delivery Note if enabled
            $account = \App\Models\Account::find($validated['account_id']);
            if (!$account->enable_delivery_notes) {
                $dn = \App\Models\DeliveryNote::create([
                    'sale_id' => $sale->id,
                    'account_id' => $sale->account_id,
                    'dn_number' => \App\Models\DeliveryNote::generateNumber($sale->account_id),
                    'date' => $sale->date,
                ]);

                foreach ($validated['items'] as $item) {
                    \App\Models\DeliveryNoteItem::create([
                        'delivery_note_id' => $dn->id,
                        'item_id' => $item['item_id'],
                        'qty' => $item['qty'],
                    ]);
                }

                $dn->adjustStockDecrease();
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

            // Auto-update Delivery Note if enabled
            $account = \App\Models\Account::find($sale->account_id);
            if (!$account->enable_delivery_notes) {
                // Find existing auto-generated DN (assuming 1-to-1 for auto mode)
                $dn = \App\Models\DeliveryNote::where('sale_id', $sale->id)->first();

                if ($dn) {
                    // Reverse old stock
                    $dn->reverseStockAdjustment();

                    // Update DN basic info
                    $dn->update([
                        'date' => $sale->date,
                    ]);

                    // Delete old items
                    \App\Models\DeliveryNoteItem::where('delivery_note_id', $dn->id)->delete();

                    // Re-create items from Sale items
                    // We need to fetch the fresh items from the Sale to be sure
                    $freshItems = $sale->items;
                    foreach ($freshItems as $item) {
                        \App\Models\DeliveryNoteItem::create([
                            'delivery_note_id' => $dn->id,
                            'item_id' => $item->item_id,
                            'qty' => $item->qty,
                        ]);
                    }

                    // Decrease new stock
                    $dn->adjustStockDecrease();
                } else {
                    // If no DN exists (maybe switched to auto mode recently), create one
                    $dn = \App\Models\DeliveryNote::create([
                        'sale_id' => $sale->id,
                        'account_id' => $sale->account_id,
                        'dn_number' => \App\Models\DeliveryNote::generateNumber($sale->account_id),
                        'date' => $sale->date,
                    ]);

                    $freshItems = $sale->items;
                    foreach ($freshItems as $item) {
                        \App\Models\DeliveryNoteItem::create([
                            'delivery_note_id' => $dn->id,
                            'item_id' => $item->item_id,
                            'qty' => $item->qty,
                        ]);
                    }

                    $dn->adjustStockDecrease();
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

        DB::beginTransaction();
        try {
            // Check if we need to auto-delete DN
            $account = \App\Models\Account::find($sale->account_id);
            // Even if enabled/disabled, if there is a linked DN and we are deleting the sale, 
            // should we delete the DN? 
            // If "auto" mode is active, yes.
            // If manual, maybe not? But user said "disabled -> executed with sales".
            // Implementation: If auto mode, strictly follow Sale.
            if (!$account->enable_delivery_notes) {
                $dns = \App\Models\DeliveryNote::where('sale_id', $sale->id)->get();
                foreach ($dns as $dn) {
                    $dn->reverseStockAdjustment(); // Restore stock before deleting
                    $dn->delete();
                }
            }

            $sale->delete();

            DB::commit();

            return response()->json([
                'success' => true,
                'message' => 'Sale deleted successfully',
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to delete sale',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
