<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Sale;
use Illuminate\Http\Request;
use Inertia\Inertia;

class SaleWebController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = Sale::query()
            ->with(['party', 'items'])
            ->withCount('items');

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('invoice_no', 'like', "%{$search}%")
                  ->orWhereHas('party', function($q) use ($search) {
                      $q->where('name', 'like', "%{$search}%");
                  });
            });
        }

        $sales = $query->orderBy('date', 'desc')->paginate(10)->withQueryString();

        // Transform data
        $sales->getCollection()->transform(function ($sale) {
            $amount = $sale->items->sum(function ($item) {
                return $item->qty * $item->price;
            });

            return [
                'id' => $sale->invoice_no,
                'customer' => $sale->party ? $sale->party->name : 'Unknown',
                'date' => $sale->date->format('Y-m-d'),
                'amount' => $amount,
                'status' => 'Completed', // Placeholder
                'items' => $sale->items_count,
            ];
        });

        return Inertia::render('Sales/Index', [
            'sales' => $sales,
            'filters' => $request->only(['search']),
        ]);
    }

    /**
     * Display the specified resource.
     */
    public function show(Sale $sale)
    {
        // Implement show logic if needed, or just return the data for a modal/page
        return Inertia::render('Sales/Show', [
            'sale' => $sale->load(['party', 'items.item']),
        ]);
    }
}
