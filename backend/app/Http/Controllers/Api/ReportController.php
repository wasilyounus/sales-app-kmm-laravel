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

class ReportController extends Controller
{
    /**
     * Sales summary report
     */
    public function salesSummary(Request $request)
    {
        $accountId = $request->input('account_id');
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $query = Sale::where('account_id', $accountId)
            ->with(['items']);

        if ($startDate) {
            $query->where('date', '>=', $startDate);
        }
        if ($endDate) {
            $query->where('date', '<=', $endDate);
        }

        $sales = $query->get();

        $summary = [
            'total_sales' => $sales->count(),
            'total_amount' => $sales->sum(function ($sale) {
                return $sale->items->sum(function ($item) {
                    return $item->price * $item->qty;
                });
            }),
            'average_sale' => $sales->count() > 0 
                ? $sales->sum(function ($sale) {
                    return $sale->items->sum(function ($item) {
                        return $item->price * $item->qty;
                    });
                }) / $sales->count()
                : 0,
            'period' => [
                'start' => $startDate ?? 'All time',
                'end' => $endDate ?? 'Present',
            ],
        ];

        return response()->json([
            'success' => true,
            'data' => $summary,
        ]);
    }

    /**
     * Purchase summary report
     */
    public function purchaseSummary(Request $request)
    {
        $accountId = $request->input('account_id');
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $query = Purchase::where('account_id', $accountId)
            ->with(['items']);

        if ($startDate) {
            $query->where('date', '>=', $startDate);
        }
        if ($endDate) {
            $query->where('date', '<=', $endDate);
        }

        $purchases = $query->get();

        $summary = [
            'total_purchases' => $purchases->count(),
            'total_amount' => $purchases->sum(function ($purchase) {
                return $purchase->items->sum(function ($item) {
                    return $item->price * $item->qty;
                });
            }),
            'average_purchase' => $purchases->count() > 0
                ? $purchases->sum(function ($purchase) {
                    return $purchase->items->sum(function ($item) {
                        return $item->price * $item->qty;
                    });
                }) / $purchases->count()
                : 0,
            'period' => [
                'start' => $startDate ?? 'All time',
                'end' => $endDate ?? 'Present',
            ],
        ];

        return response()->json([
            'success' => true,
            'data' => $summary,
        ]);
    }

    /**
     * Top selling items
     */
    public function topSellingItems(Request $request)
    {
        $accountId = $request->input('account_id');
        $limit = $request->input('limit', 10);
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $query = DB::table('sale_items')
            ->join('items', 'sale_items.item_id', '=', 'items.id')
            ->join('sales', 'sale_items.sale_id', '=', 'sales.id')
            ->where('sale_items.account_id', $accountId)
            ->select(
                'items.id',
                'items.name',
                DB::raw('SUM(sale_items.qty) as total_quantity'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_amount'),
                DB::raw('COUNT(DISTINCT sale_items.sale_id) as transaction_count')
            )
            ->groupBy('items.id', 'items.name')
            ->orderBy('total_quantity', 'desc')
            ->limit($limit);

        if ($startDate) {
            $query->where('sales.date', '>=', $startDate);
        }
        if ($endDate) {
            $query->where('sales.date', '<=', $endDate);
        }

        $topItems = $query->get();

        return response()->json([
            'success' => true,
            'data' => $topItems,
        ]);
    }

    /**
     * Top customers by sales
     */
    public function topCustomers(Request $request)
    {
        $accountId = $request->input('account_id');
        $limit = $request->input('limit', 10);
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $query = DB::table('sales')
            ->join('parties', 'sales.party_id', '=', 'parties.id')
            ->join('sale_items', 'sales.id', '=', 'sale_items.sale_id')
            ->where('sales.account_id', $accountId)
            ->select(
                'parties.id',
                'parties.name',
                DB::raw('COUNT(DISTINCT sales.id) as total_orders'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_amount')
            )
            ->groupBy('parties.id', 'parties.name')
            ->orderBy('total_amount', 'desc')
            ->limit($limit);

        if ($startDate) {
            $query->where('sales.date', '>=', $startDate);
        }
        if ($endDate) {
            $query->where('sales.date', '<=', $endDate);
        }

        $topCustomers = $query->get();

        return response()->json([
            'success' => true,
            'data' => $topCustomers,
        ]);
    }

    /**
     * Sales by period (daily, monthly, yearly)
     */
    public function salesByPeriod(Request $request)
    {
        $accountId = $request->input('account_id');
        $period = $request->input('period', 'monthly'); // daily, monthly, yearly
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        $dateFormat = match($period) {
            'daily' => '%Y-%m-%d',
            'monthly' => '%Y-%m',
            'yearly' => '%Y',
            default => '%Y-%m',
        };

        $query = DB::table('sales')
            ->join('sale_items', 'sales.id', '=', 'sale_items.sale_id')
            ->where('sales.account_id', $accountId)
            ->select(
                DB::raw("DATE_FORMAT(sales.date, '$dateFormat') as period"),
                DB::raw('COUNT(DISTINCT sales.id) as total_sales'),
                DB::raw('SUM(sale_items.price * sale_items.qty) as total_amount')
            )
            ->groupBy('period')
            ->orderBy('period', 'asc');

        if ($startDate) {
            $query->where('sales.date', '>=', $startDate);
        }
        if ($endDate) {
            $query->where('sales.date', '<=', $endDate);
        }

        $salesData = $query->get();

        return response()->json([
            'success' => true,
            'data' => $salesData,
            'period_type' => $period,
        ]);
    }

    /**
     * Profit/Loss report
     */
    public function profitLoss(Request $request)
    {
        $accountId = $request->input('account_id');
        $startDate = $request->input('start_date');
        $endDate = $request->input('end_date');

        // Calculate total sales
        $salesQuery = Sale::where('account_id', $accountId)
            ->with(['items']);
        
        if ($startDate) $salesQuery->where('date', '>=', $startDate);
        if ($endDate) $salesQuery->where('date', '<=', $endDate);
        
        $sales = $salesQuery->get();
        $totalSales = $sales->sum(function ($sale) {
            return $sale->items->sum(function ($item) {
                return $item->price * $item->qty;
            });
        });

        // Calculate total purchases
        $purchasesQuery = Purchase::where('account_id', $accountId)
            ->with(['items']);
        
        if ($startDate) $purchasesQuery->where('date', '>=', $startDate);
        if ($endDate) $purchasesQuery->where('date', '<=', $endDate);
        
        $purchases = $purchasesQuery->get();
        $totalPurchases = $purchases->sum(function ($purchase) {
            return $purchase->items->sum(function ($item) {
                return $item->price * $item->qty;
            });
        });

        $profit = $totalSales - $totalPurchases;
        $profitMargin = $totalSales > 0 ? ($profit / $totalSales) * 100 : 0;

        return response()->json([
            'success' => true,
            'data' => [
                'total_sales' => $totalSales,
                'total_purchases' => $totalPurchases,
                'gross_profit' => $profit,
                'profit_margin_percentage' => round($profitMargin, 2),
                'period' => [
                    'start' => $startDate ?? 'All time',
                    'end' => $endDate ?? 'Present',
                ],
            ],
        ]);
    }
}
