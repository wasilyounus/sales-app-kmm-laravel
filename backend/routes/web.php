<?php

use Illuminate\Support\Facades\Route;
use Inertia\Inertia;
use App\Http\Controllers\Web\AccountWebController;
use App\Http\Controllers\Web\DashboardController;
use App\Http\Controllers\Web\ItemWebController;
use App\Http\Controllers\Web\PartyWebController;
use App\Http\Controllers\Web\SaleWebController;

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
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('admin.dashboard');
    Route::resource('accounts', AccountWebController::class);

    Route::resource('items', ItemWebController::class);
    Route::resource('parties', PartyWebController::class);
    Route::resource('sales', SaleWebController::class);
    Route::get('/reports', function () { return Inertia::render('Reports/Index'); })->name('reports.index');
});
