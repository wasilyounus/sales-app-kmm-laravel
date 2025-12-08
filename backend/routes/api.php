<?php

use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\AccountController;
use App\Http\Controllers\Api\ItemController;
use App\Http\Controllers\Api\PartyController;
use App\Http\Controllers\Api\QuoteController;
use App\Http\Controllers\Api\OrderController;
use App\Http\Controllers\Api\SaleController;
use App\Http\Controllers\Api\PurchaseController;
use App\Http\Controllers\Api\ResourceController;
use App\Http\Controllers\Api\SyncController;
use App\Http\Controllers\Api\ReportController;
use App\Http\Controllers\Api\AnalyticsController;
use App\Http\Controllers\Api\TransactionController;
use App\Http\Controllers\Api\PriceListController;
use App\Http\Controllers\Api\DeliveryNoteController;
use App\Http\Controllers\Api\GrnController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
*/

// Public routes
Route::post('register', [AuthController::class, 'register']);
Route::post('login', [AuthController::class, 'login']);

// Protected routes
Route::middleware('auth:sanctum')->group(function () {
    // Authentication
    Route::post('logout', [AuthController::class, 'logout']);
    Route::get('user', [AuthController::class, 'user']);

    // Account Selection
    Route::prefix('admin')->group(function () {
        Route::get('select-account', [App\Http\Controllers\Web\AccountSelectionController::class, 'index']);
        Route::post('select-account', [App\Http\Controllers\Web\AccountSelectionController::class, 'store']);
    });

    // Accounts
    Route::get('accounts', [AccountController::class, 'index']);
    Route::post('accounts', [AccountController::class, 'store']);
    Route::get('accounts/{id}', [AccountController::class, 'show']);
    Route::put('accounts/{id}', [AccountController::class, 'update']);
    Route::post('accounts/{id}', [AccountController::class, 'update']); // For mobile compatibility

    // Items
    Route::get('items', [ItemController::class, 'index']);
    Route::post('items', [ItemController::class, 'store']);
    Route::get('items/{id}', [ItemController::class, 'show']);
    Route::put('items/{id}', [ItemController::class, 'update']);
    Route::delete('items/{id}', [ItemController::class, 'destroy']);

    // Parties
    Route::get('parties', [PartyController::class, 'index']);
    Route::post('parties', [PartyController::class, 'store']);
    Route::get('parties/{id}', [PartyController::class, 'show']);
    Route::put('parties/{id}', [PartyController::class, 'update']);
    Route::delete('parties/{id}', [PartyController::class, 'destroy']);

    // Quotes
    Route::get('quotes', [QuoteController::class, 'index']);
    Route::post('quotes', [QuoteController::class, 'store']);
    Route::get('quotes/{id}', [QuoteController::class, 'show']);
    Route::put('quotes/{id}', [QuoteController::class, 'update']);
    Route::delete('quotes/{id}', [QuoteController::class, 'destroy']);
    Route::get('quoteItems', [QuoteController::class, 'items']);

    // Orders
    Route::get('orders', [OrderController::class, 'index']);
    Route::post('orders', [OrderController::class, 'store']);
    Route::get('orders/{id}', [OrderController::class, 'show']);
    Route::put('orders/{id}', [OrderController::class, 'update']);
    Route::delete('orders/{id}', [OrderController::class, 'destroy']);

    // Sales
    Route::get('sales', [SaleController::class, 'index']);
    Route::post('sales', [SaleController::class, 'store']);
    Route::get('sales/{id}', [SaleController::class, 'show']);
    Route::put('sales/{id}', [SaleController::class, 'update']);
    Route::delete('sales/{id}', [SaleController::class, 'destroy']);

    // Purchases
    Route::get('purchases', [PurchaseController::class, 'index']);
    Route::post('purchases', [PurchaseController::class, 'store']);
    Route::get('purchases/{id}', [PurchaseController::class, 'show']);
    Route::put('purchases/{id}', [PurchaseController::class, 'update']);
    Route::delete('purchases/{id}', [PurchaseController::class, 'destroy']);

    // Resources (Read-only)
    Route::get('taxes', [ResourceController::class, 'taxes']);
    Route::get('uqcs', [ResourceController::class, 'uqcs']);
    Route::get('stocks', [ResourceController::class, 'stocks']);
    Route::get('transports', [ResourceController::class, 'transports']);
    Route::get('addresses', [ResourceController::class, 'addresses']);

    // Sync Endpoints
    Route::prefix('sync')->group(function () {
        Route::get('master-data', [SyncController::class, 'masterData']);
        Route::get('quotes', [SyncController::class, 'quotes']);
        Route::get('orders', [SyncController::class, 'orders']);
        Route::get('sales', [SyncController::class, 'sales']);
        Route::get('purchases', [SyncController::class, 'purchases']);
        Route::get('delivery-notes', [SyncController::class, 'deliveryNotes']);
        Route::get('grns', [SyncController::class, 'grns']);
        Route::get('full', [SyncController::class, 'fullSync']);
        Route::get('status', [SyncController::class, 'status']);
    });

    // Reporting Endpoints
    Route::prefix('reports')->group(function () {
        Route::get('sales-summary', [ReportController::class, 'salesSummary']);
        Route::get('purchase-summary', [ReportController::class, 'purchaseSummary']);
        Route::get('top-items', [ReportController::class, 'topSellingItems']);
        Route::get('top-customers', [ReportController::class, 'topCustomers']);
        Route::get('sales-by-period', [ReportController::class, 'salesByPeriod']);
        Route::get('profit-loss', [ReportController::class, 'profitLoss']);
    });

    // Analytics Endpoints
    Route::prefix('analytics')->group(function () {
        Route::get('dashboard', [AnalyticsController::class, 'dashboard']);
        Route::get('sales-trends', [AnalyticsController::class, 'salesTrends']);
        Route::get('purchase-trends', [AnalyticsController::class, 'purchaseTrends']);
        Route::get('item-performance', [AnalyticsController::class, 'itemPerformance']);
        Route::get('party-analytics', [AnalyticsController::class, 'partyAnalytics']);
        Route::get('conversion-funnel', [AnalyticsController::class, 'conversionFunnel']);
    });

    // Payments / Transactions
    Route::get('transactions', [TransactionController::class, 'index']);
    Route::post('transactions', [TransactionController::class, 'store']);
    Route::get('transactions/{id}', [TransactionController::class, 'show']);
    Route::put('transactions/{id}', [TransactionController::class, 'update']);
    Route::delete('transactions/{id}', [TransactionController::class, 'destroy']);

    // Alias payments to transactions for semantic clarity if needed
    Route::get('payments', [TransactionController::class, 'index']);
    Route::post('payments', [TransactionController::class, 'store']);
    Route::get('payments/{id}', [TransactionController::class, 'show']);
    Route::put('payments/{id}', [TransactionController::class, 'update']);
    Route::delete('payments/{id}', [TransactionController::class, 'destroy']);

    // Price Lists
    Route::get('price-lists', [PriceListController::class, 'index']);
    Route::post('price-lists', [PriceListController::class, 'store']);
    Route::get('price-lists/{id}', [PriceListController::class, 'show']);
    Route::put('price-lists/{id}', [PriceListController::class, 'update']);
    Route::delete('price-lists/{id}', [PriceListController::class, 'destroy']);
    Route::post('price-lists/{id}/items', [PriceListController::class, 'addItems']);
    Route::get('price/effective', [PriceListController::class, 'getEffectivePrice']);

    // Delivery Notes
    Route::get('delivery-notes', [DeliveryNoteController::class, 'index']);
    Route::post('delivery-notes', [DeliveryNoteController::class, 'store']);
    Route::get('delivery-notes/{id}', [DeliveryNoteController::class, 'show']);
    Route::put('delivery-notes/{id}', [DeliveryNoteController::class, 'update']);
    Route::delete('delivery-notes/{id}', [DeliveryNoteController::class, 'destroy']);

    // GRNs (Goods Received Notes)
    Route::get('grns', [GrnController::class, 'index']);
    Route::post('grns', [GrnController::class, 'store']);
    Route::get('grns/{id}', [GrnController::class, 'show']);
    Route::put('grns/{id}', [GrnController::class, 'update']);
    Route::delete('grns/{id}', [GrnController::class, 'destroy']);

    // Test endpoint
    Route::get('test', function () {
        return response()->json([
            'success' => true,
            'message' => 'API is working!',
            'timestamp' => now(),
        ]);
    });
});

