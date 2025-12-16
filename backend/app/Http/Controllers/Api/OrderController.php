<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\OrderItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class OrderController extends Controller
{
    public function index(Request $request)
    {
        $companyId = $request->input('company_id');

        $query = Order::with(['party', 'items.item']);

        if ($companyId) {
            $query->where('company_id', $companyId);
        }

        $orders = $query->orderBy('date', 'desc')->get();

        return response()->json([
            'success' => true,
            'data' => $orders,
        ]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'order_no' => 'nullable|string|max:255',
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
            $order = Order::create([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
                'order_no' => $validated['order_no'] ?? Order::generateNumber($validated['company_id']),
                'company_id' => $validated['company_id'],
                'log_id' => $validated['log_id'],
            ]);

            foreach ($validated['items'] as $item) {
                OrderItem::create([
                    'order_id' => $order->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'tax_id' => $item['tax_id'] ?? null,
                    'company_id' => $validated['company_id'],
                    'log_id' => $validated['log_id'],
                ]);
            }

            DB::commit();
            $order->load(['party', 'items.item']);

            return response()->json([
                'success' => true,
                'message' => 'Order created successfully',
                'data' => $order,
            ], 201);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create order',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function show($id)
    {
        $order = Order::with(['party', 'items.item'])->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => $order,
        ]);
    }

    public function update(Request $request, $id)
    {
        $order = Order::findOrFail($id);

        $validated = $request->validate([
            'party_id' => 'sometimes|required|exists:parties,id',
            'date' => 'sometimes|required|date',
            'order_no' => 'nullable|string|max:255',
            'log_id' => 'sometimes|required|integer',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.price' => 'required_with:items|numeric|min:0',
            'items.*.qty' => 'required_with:items|numeric|min:0',
        ]);

        DB::beginTransaction();
        try {
            $order->update($validated);

            if (isset($validated['items'])) {
                OrderItem::where('order_id', $order->id)->delete();

                foreach ($validated['items'] as $item) {
                    OrderItem::create([
                        'order_id' => $order->id,
                        'item_id' => $item['item_id'],
                        'price' => $item['price'],
                        'qty' => $item['qty'],
                        'account_id' => $order->account_id,
                        'log_id' => $validated['log_id'] ?? $order->log_id,
                    ]);
                }
            }

            DB::commit();
            $order->load(['party', 'items.item']);

            return response()->json([
                'success' => true,
                'message' => 'Order updated successfully',
                'data' => $order,
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to update order',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function destroy($id)
    {
        $order = Order::findOrFail($id);
        $order->delete();

        return response()->json([
            'success' => true,
            'message' => 'Order deleted successfully',
        ]);
    }
}
