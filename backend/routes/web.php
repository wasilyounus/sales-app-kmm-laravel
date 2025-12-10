<?php

use Illuminate\Support\Facades\Route;
use Inertia\Inertia;
use App\Http\Controllers\Web\CompanyWebController;
use App\Http\Controllers\Web\DashboardController;
use App\Http\Controllers\Web\ItemWebController;
use App\Http\Controllers\Web\PartyWebController;
use App\Http\Controllers\Web\SaleWebController;
use App\Http\Controllers\Web\QuoteWebController;
use App\Http\Controllers\Web\PurchaseWebController;
use App\Http\Controllers\Web\OrderWebController;
use App\Http\Controllers\Web\InventoryWebController;
use App\Http\Controllers\Web\PaymentWebController;
use App\Http\Controllers\Web\PriceListWebController;
use App\Http\Controllers\Web\DeliveryNoteWebController;
use App\Http\Controllers\Web\GrnWebController;

Route::get('/', function () {
    if (auth()->check()) {
        return redirect()->route('admin.dashboard');
    }
    return redirect()->route('login');
});

use App\Http\Controllers\Auth\AuthController;

Route::middleware('guest')->group(function () {
    Route::get('login', [AuthController::class, 'create'])->name('login');
    Route::post('login', [AuthController::class, 'store']);
});

Route::post('logout', [AuthController::class, 'destroy'])
    ->name('logout')
    ->middleware('auth');

// Admin routes
Route::middleware(['auth'])->prefix('admin')->group(function () {
    // Company Selection
    Route::get('/select-company', [App\Http\Controllers\Web\CompanySelectionController::class, 'index'])->name('company.list');
    Route::post('/select-company', [App\Http\Controllers\Web\CompanySelectionController::class, 'store'])->name('company.select');

    Route::get('/dashboard', [DashboardController::class, 'index'])->name('admin.dashboard');
    Route::resource('companies', CompanyWebController::class);

    Route::resource('items', ItemWebController::class);
    Route::resource('parties', PartyWebController::class);
    Route::resource('sales', SaleWebController::class);
    Route::resource('quotes', QuoteWebController::class);
    Route::resource('purchases', PurchaseWebController::class);
    Route::resource('orders', OrderWebController::class);

    // Inventory
    Route::resource('inventory', InventoryWebController::class)->only(['index']);
    Route::post('inventory/adjust', [InventoryWebController::class, 'adjust'])->name('inventory.adjust');

    // Payments
    Route::resource('payments', PaymentWebController::class)->only(['index', 'store']);

    // Price Lists
    Route::resource('price-lists', PriceListWebController::class);
    Route::post('price-lists/{priceList}/items', [PriceListWebController::class, 'updateItems'])->name('price-lists.update-items');
    Route::delete('price-lists/{priceList}/items/{item}', [PriceListWebController::class, 'removeItem'])->name('price-lists.remove-item');

    // Delivery Notes & GRNs
    Route::resource('delivery-notes', DeliveryNoteWebController::class);
    Route::resource('grns', GrnWebController::class);

    Route::get('/reports', function () {
        return Inertia::render('Reports/Index');
    })->name('reports.index');
});
