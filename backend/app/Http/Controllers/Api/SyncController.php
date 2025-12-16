<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Item;
use App\Models\Party;
use App\Models\Address;
use App\Models\Quote;
use App\Models\Order;
use App\Models\Sale;
use App\Models\Purchase;
use App\Models\DeliveryNote;
use App\Models\Grn;
use App\Models\Uqc;
use App\Models\Tax;
use Illuminate\Http\Request;

class SyncController extends Controller
{
    /**
     * Sync all master data for mobile app
     */
    public function masterData(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $data = [
            'items' => Item::where('company_id', $companyId)
                ->where('updated_at', '>', $timestamp)
                ->get(),
            'parties' => Party::where('company_id', $companyId)
                ->where('updated_at', '>', $timestamp)
                ->with('addresses')
                ->get(),
            'uqcs' => Uqc::where('active', true)
                ->where('updated_at', '>', $timestamp)
                ->get(),
            'taxes' => Tax::where('active', true)
                ->where('updated_at', '>', $timestamp)
                ->get(),
            'timestamp' => now()->toDateTimeString(),
        ];

        return response()->json([
            'success' => true,
            'data' => $data,
        ]);
    }

    /**
     * Sync quotes with items
     */
    public function quotes(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $quotes = Quote::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['party', 'items.item'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $quotes,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Sync orders with items
     */
    public function orders(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $orders = Order::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['party', 'items.item'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $orders,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Sync sales with items
     */
    public function sales(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $sales = Sale::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['party', 'items.item', 'items.tax', 'supply'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $sales,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Sync purchases with items
     */
    public function purchases(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $purchases = Purchase::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['party', 'items.item', 'items.tax'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $purchases,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Sync delivery notes with items
     */
    public function deliveryNotes(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $deliveryNotes = DeliveryNote::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['sale.party', 'items.item'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $deliveryNotes,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Sync GRNs (Goods Received Notes) with items
     */
    public function grns(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $grns = Grn::where('company_id', $companyId)
            ->where('updated_at', '>', $timestamp)
            ->with(['purchase.party', 'items.item'])
            ->get();

        return response()->json([
            'success' => true,
            'data' => $grns,
            'timestamp' => now()->toDateTimeString(),
        ]);
    }

    /**
     * Full sync - all data at once
     */
    public function fullSync(Request $request)
    {
        $companyId = $request->input('company_id');
        $timestamp = $request->input('timestamp', '1970-01-01 00:00:00');

        $data = [
            'master_data' => [
                'items' => Item::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->get(),
                'parties' => Party::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->with('addresses')
                    ->get(),
                'uqcs' => Uqc::where('active', true)->get(),
                'taxes' => Tax::where('active', true)->get(),
            ],
            'transactions' => [
                'quotes' => Quote::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->with(['party', 'items.item'])
                    ->get(),
                'orders' => Order::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->with(['party', 'items.item'])
                    ->get(),
                'sales' => Sale::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->with(['party', 'items.item', 'items.tax'])
                    ->get(),
                'purchases' => Purchase::where('company_id', $companyId)
                    ->where('updated_at', '>', $timestamp)
                    ->with(['party', 'items.item', 'items.tax'])
                    ->get(),
            ],
            'timestamp' => now()->toDateTimeString(),
        ];

        return response()->json([
            'success' => true,
            'data' => $data,
        ]);
    }

    /**
     * Get sync status
     */
    public function status(Request $request)
    {
        $companyId = $request->input('company_id');

        $status = [
            'items_count' => Item::where('company_id', $companyId)->count(),
            'parties_count' => Party::where('company_id', $companyId)->count(),
            'quotes_count' => Quote::where('company_id', $companyId)->count(),
            'orders_count' => Order::where('company_id', $companyId)->count(),
            'sales_count' => Sale::where('company_id', $companyId)->count(),
            'purchases_count' => Purchase::where('company_id', $companyId)->count(),
            'last_updated' => now()->toDateTimeString(),
        ];

        return response()->json([
            'success' => true,
            'data' => $status,
        ]);
    }
}
