<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Item;
use App\Models\Stock;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class InventoryWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $query = Item::query()
            ->where('account_id', $accountId)
            ->with(['stock' => function($q) use ($accountId) {
                $q->where('account_id', $accountId);
            }]);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where('name', 'like', "%{$search}%");
        }

        $items = $query->orderBy('name')->paginate(20)->withQueryString();

        $items->getCollection()->transform(function ($item) {
            return [
                'id' => $item->id,
                'name' => $item->name,
                'code' => $item->code,
                'category' => $item->category,
                'unit' => $item->unit,
                'current_stock' => $item->stock ? (float)$item->stock->count : 0,
                'price' => $item->price,
            ];
        });

        // Stats
        $totalItems = Item::where('account_id', $accountId)->count();
        $lowStockItems = Item::where('account_id', $accountId)
            ->whereHas('stock', function($q) {
                $q->where('count', '<', 10); // Example threshold
            })->count();
        
        // Calculate total inventory value
        $totalValue = Item::where('account_id', $accountId)
            ->with('stock')
            ->get()
            ->sum(function ($item) {
                return ($item->stock ? $item->stock->count : 0) * $item->price;
            });

        return Inertia::render('Inventory/Index', [
            'items' => $items,
            'stats' => [
                'total_items' => $totalItems,
                'low_stock' => $lowStockItems,
                'total_value' => $totalValue,
            ],
            'filters' => $request->only(['search']),
        ]);
    }

    public function adjust(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $request->validate([
            'item_id' => 'required|exists:items,id',
            'adjustment' => 'required|numeric', // Positive to add, negative to subtract
            'reason' => 'nullable|string',
        ]);

        DB::transaction(function () use ($request, $accountId) {
            $stock = Stock::firstOrNew([
                'item_id' => $request->item_id,
                'account_id' => $accountId,
            ]);

            $currentCount = $stock->exists ? $stock->count : 0;
            $stock->count = $currentCount + $request->adjustment;
            $stock->log_id = 1; // Placeholder
            $stock->save();

            // Here we could also log the adjustment to a stock_history table if we had one
        });

        return redirect()->back()->with('success', 'Stock adjusted successfully.');
    }
}
