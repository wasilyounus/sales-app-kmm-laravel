<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Sale;
use App\Models\Purchase;
use App\Models\Quote;
use App\Models\Order;
use App\Models\Party;
use App\Models\Item;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AnalyticsController extends Controller
{
    /**
     * Dashboard analytics
     */
    public function dashboard(Request $request)
    {
        $accountId = $request->input('account_id');

        $data = [
            'overview' => [
                'total_items' => Item::where('account_id', $accountId)->count(),
                'total_parties' => Party::where('account_id', $accountId)->count(),
                'total_quotes' => Quote::where('account_id', $accountId)->count(),
                'total_orders' => Order::where('account_id', $accountId)->count(),
                'total_sales' => Sale::where('account_id', $accountId)->count(),
                'total_purchases' => Purchase::where('account_id', $accountId)->count(),
            ],
            'recent_activity' => [
                'recent_sales' => Sale::where('account_id', $accountId)
                    ->with('party')
                    ->orderBy('date', 'desc')
                    ->limit(5)
                    ->get(),
                'recent_purchases' => Purchase::where('account_id', $accountId)
                    ->with('party')
                    ->orderBy('date', 'desc')
                    ->limit(5)
                    ->get(),
            ],
            'this_month' => $this->getMonthlyStats($accountId),
        ];

        return response()->json([
            'success' => true,
            'data' => $data,
        ]);
    }

    /**
     * Sales trends
     */
    public function salesTrends(Request $request)
    {
        $accountId = $request->input('account_id');
        $days = $request->input('days', 30);

        $startDate = now()->subDays($days)->format('Y-m-d');

        $trends = DB::table('sales')
            ->join('sale_items', 'sales.id', '=', 'sale_items.sale_id')
            ->where('sales.account_id', $accountId)
            ->where('sales.date', '>=', $startDate)
            ->select(
                DB::raw('DATE(sales.date) as date'),
                DB::raw('COUNT(DISTINCT sales.id) as count'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as amount')
            )
            ->groupBy('date')
            ->orderBy('date', 'asc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $trends,
            'period_days' => $days,
        ]);
    }

    /**
     * Purchase trends
     */
    public function purchaseTrends(Request $request)
    {
        $accountId = $request->input('account_id');
        $days = $request->input('days', 30);

        $startDate = now()->subDays($days)->format('Y-m-d');

        $trends = DB::table('purchases')
            ->join('purchase_items', 'purchases.id', '=', 'purchase_items.purchase_id')
            ->where('purchases.account_id', $accountId)
            ->where('purchases.date', '>=', $startDate)
            ->select(
                DB::raw('DATE(purchases.date) as date'),
                DB::raw('COUNT(DISTINCT purchases.id) as count'),
                DB::raw('SUM(purchase_items.price * purchase_items.qty) as amount')
            )
            ->groupBy('date')
            ->orderBy('date', 'asc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $trends,
            'period_days' => $days,
        ]);
    }

    /**
     * Item performance analytics
     */
    public function itemPerformance(Request $request)
    {
        $accountId = $request->input('account_id');
        $itemId = $request->input('item_id');

        $salesData = DB::table('sale_items')
            ->join('sales', 'sale_items.sale_id', '=', 'sales.id')
            ->where('sale_items.item_id', $itemId)
            ->where('sale_items.account_id', $accountId)
            ->select(
                DB::raw('COUNT(*) as total_transactions'),
                DB::raw('SUM(sale_items.qty) as total_quantity_sold'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_revenue'),
                DB::raw('AVG(sale_items.price) as average_price')
            )
            ->first();

        $purchaseData = DB::table('purchase_items')
            ->join('purchases', 'purchase_items.purchase_id', '=', 'purchases.id')
            ->where('purchase_items.item_id', $itemId)
            ->where('purchase_items.account_id', $accountId)
            ->select(
                DB::raw('COUNT(*) as total_transactions'),
                DB::raw('SUM(purchase_items.qty) as total_quantity_purchased'),
                DB::raw('SUM(purchase_items.price * purchase_items.qty) as total_cost'),
                DB::raw('AVG(purchase_items.price) as average_cost')
            )
            ->first();

        return response()->json([
            'success' => true,
            'data' => [
                'sales' => $salesData,
                'purchases' => $purchaseData,
                'profit_per_unit' => ($salesData->average_price ?? 0) - ($purchaseData->average_cost ?? 0),
            ],
        ]);
    }

    /**
     * Party analytics
     */
    public function partyAnalytics(Request $request)
    {
        $accountId = $request->input('account_id');
        $partyId = $request->input('party_id');

        $salesData = DB::table('sales')
            ->join('sale_items', 'sales.id', '=', 'sale_items.sale_id')
            ->where('sales.party_id', $partyId)
            ->where('sales.account_id', $accountId)
            ->select(
                DB::raw('COUNT(DISTINCT sales.id) as total_orders'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_amount'),
                DB::raw('AVG(sale_items.price * sale_items.qty) as average_order_value'),
                DB::raw('MAX(sales.date) as last_order_date')
            )
            ->first();

        $topItems = DB::table('sale_items')
            ->join('items', 'sale_items.item_id', '=', 'items.id')
            ->join('sales', 'sale_items.sale_id', '=', 'sales.id')
            ->where('sales.party_id', $partyId)
            ->where('sales.account_id', $accountId)
            ->select(
                'items.name',
                DB::raw('SUM(sale_items.qty) as total_quantity'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_amount')
            )
            ->groupBy('items.id', 'items.name')
            ->orderBy('total_amount', 'desc')
            ->limit(5)
            ->get();

        return response()->json([
            'success' => true,
            'data' => [
                'summary' => $salesData,
                'top_items' => $topItems,
            ],
        ]);
    }

    /**
     * Conversion funnel analytics
     */
    public function conversionFunnel(Request $request)
    {
        $accountId = $request->input('account_id');
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $quotesQuery = Quote::where('account_id', $accountId);
        $ordersQuery = Order::where('account_id', $accountId);
        $salesQuery = Sale::where('account_id', $accountId);

        if ($startDate) {
            $quotesQuery->where('date', '>=', $startDate);
            $ordersQuery->where('date', '>=', $startDate);
            $salesQuery->where('date', '>=', $startDate);
        }
        if ($endDate) {
            $quotesQuery->where('date', '<=', $endDate);
            $ordersQuery->where('date', '<=', $endDate);
            $salesQuery->where('date', '<=', $endDate);
        }

        $quotesCount = $quotesQuery->count();
        $ordersCount = $ordersQuery->count();
        $salesCount = $salesQuery->count();

        $quoteToOrderRate = $quotesCount > 0 ? ($ordersCount / $quotesCount) * 100 : 0;
        $orderToSaleRate = $ordersCount > 0 ? ($salesCount / $ordersCount) * 100 : 0;
        $quoteToSaleRate = $quotesCount > 0 ? ($salesCount / $quotesCount) * 100 : 0;

        return response()->json([
            'success' => true,
            'data' => [
                'funnel' => [
                    'quotes' => $quotesCount,
                    'orders' => $ordersCount,
                    'sales' => $salesCount,
                ],
                'conversion_rates' => [
                    'quote_to_order' => round($quoteToOrderRate, 2),
                    'order_to_sale' => round($orderToSaleRate, 2),
                    'quote_to_sale' => round($quoteToSaleRate, 2),
                ],
            ],
        ]);
    }

    /**
     * Helper: Get monthly statistics
     */
    private function getMonthlyStats($accountId)
    {
        $startOfMonth = now()->startOfMonth()->format('Y-m-d');
        $endOfMonth = now()->endOfMonth()->format('Y-m-d');

        $sales = Sale::where('account_id', $accountId)
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->with('items')
            ->get();

        $purchases = Purchase::where('account_id', $accountId)
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->with('items')
            ->get();

        $totalSales = $sales->sum(function ($sale) {
            return $sale->items->sum(function ($item) {
                return $item->price * $item->qty;
            });
        });

        $totalPurchases = $purchases->sum(function ($purchase) {
            return $purchase->items->sum(function ($item) {
                return $item->price * $item->qty;
            });
        });

        return [
            'sales_count' => $sales->count(),
            'sales_amount' => $totalSales,
            'purchases_count' => $purchases->count(),
            'purchases_amount' => $totalPurchases,
            'profit' => $totalSales - $totalPurchases,
        ];
    }
}
