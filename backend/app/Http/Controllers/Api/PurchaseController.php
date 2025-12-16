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
        $companyId = $request->input('company_id');

        $query = Purchase::with(['party', 'items.item', 'items.tax']);

        if ($companyId) {
            $query->where('company_id', $companyId);
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
            'invoice_no' => 'nullable|string|max:255',
            'company_id' => 'required|exists:companies,id',
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
                'invoice_no' => $validated['invoice_no'] ?? null,
                'company_id' => $validated['company_id'],
                'log_id' => $validated['log_id'],
            ]);

            foreach ($validated['items'] as $item) {
                PurchaseItem::create([
                    'purchase_id' => $purchase->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'company_id' => $validated['company_id'],
                    'log_id' => $validated['log_id'],
                ]);
            }

            // Auto-create GRN if enabled
            $account = \App\Models\Company::find($validated['account_id']);
            if (!$account->enable_grns) {
                // Determine GRN Number (using helper if available or similar logic)
                // Assuming Grn::generateNumber mirrors DeliveryNote::generateNumber
                $grn = \App\Models\Grn::create([
                    'purchase_id' => $purchase->id,
                    'account_id' => $purchase->account_id,
                    'grn_number' => \App\Models\Grn::generateNumber($purchase->account_id),
                    'date' => $purchase->date,
                ]);

                foreach ($validated['items'] as $item) {
                    \App\Models\GrnItem::create([
                        'grn_id' => $grn->id,
                        'item_id' => $item['item_id'],
                        'qty' => $item['qty'],
                    ]);
                }

                $grn->adjustStockIncrease();
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

            // Auto-update GRN if enabled
            $account = \App\Models\Company::find($purchase->account_id);
            if (!$account->enable_grns) {
                // Find existing GRN (1-to-1 assumption for auto mode)
                $grn = \App\Models\Grn::where('purchase_id', $purchase->id)->first();

                if ($grn) {
                    $grn->reverseStockAdjustment(); // Revert old stock

                    $grn->update(['date' => $purchase->date]);

                    \App\Models\GrnItem::where('grn_id', $grn->id)->delete();

                    $freshItems = $purchase->items;
                    foreach ($freshItems as $item) {
                        \App\Models\GrnItem::create([
                            'grn_id' => $grn->id,
                            'item_id' => $item->item_id,
                            'qty' => $item->qty,
                        ]);
                    }

                    $grn->adjustStockIncrease(); // New stock
                } else {
                    // Create new if missing
                    $grn = \App\Models\Grn::create([
                        'purchase_id' => $purchase->id,
                        'account_id' => $purchase->account_id,
                        'grn_number' => \App\Models\Grn::generateNumber($purchase->account_id),
                        'date' => $purchase->date,
                    ]);

                    $freshItems = $purchase->items;
                    foreach ($freshItems as $item) {
                        \App\Models\GrnItem::create([
                            'grn_id' => $grn->id,
                            'item_id' => $item->item_id,
                            'qty' => $item->qty,
                        ]);
                    }
                    $grn->adjustStockIncrease();
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

        DB::beginTransaction();
        try {
            // Auto-delete GRN check
            $account = \App\Models\Company::find($purchase->account_id);
            if (!$account->enable_grns) {
                $grns = \App\Models\Grn::where('purchase_id', $purchase->id)->get();
                foreach ($grns as $grn) {
                    $grn->reverseStockAdjustment();
                    $grn->delete();
                }
            }

            $purchase->delete();

            DB::commit();

            return response()->json([
                'success' => true,
                'message' => 'Purchase deleted successfully',
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to delete purchase',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
}
