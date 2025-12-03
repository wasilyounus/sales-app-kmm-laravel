<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Item;
use Illuminate\Http\Request;
use Inertia\Inertia;
use Illuminate\Support\Facades\DB;

class ItemWebController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = Item::query()
            ->with(['stock'])
            ->select('items.*')
            ->addSelect([
                'stock_count' => \App\Models\Stock::select('count')
                    ->whereColumn('item_id', 'items.id')
                    ->limit(1)
            ]);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('brand', 'like', "%{$search}%")
                  ->orWhere('alt_name', 'like', "%{$search}%");
            });
        }

        $items = $query->orderBy('name')->paginate(10)->withQueryString();

        $taxes = \App\Models\Tax::where('active', true)->get();
        $uqcs = \App\Models\Uqc::where('active', true)->get();

        return Inertia::render('Items/Index', [
            'items' => $items,
            'filters' => $request->only(['search']),
            'taxes' => $taxes,
            'uqcs' => $uqcs,
        ]);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $taxes = \App\Models\Tax::where('active', true)->get();
        $uqcs = \App\Models\Uqc::where('active', true)->get();
        
        return Inertia::render('Items/Create', [
            'taxes' => $taxes,
            'uqcs' => $uqcs,
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'required|integer',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
            'opening_stock' => 'nullable|numeric|min:0',
        ]);

        DB::transaction(function () use ($validated, $request) {
            // Create Item
            $item = Item::create([
                'name' => $validated['name'],
                'alt_name' => $validated['alt_name'],
                'brand' => $validated['brand'],
                'size' => $validated['size'],
                'uqc' => $validated['uqc'],
                'hsn' => $validated['hsn'],
                'tax_id' => $validated['tax_id'] ?? null,
                'account_id' => auth()->user()->current_account_id,
            ]);

            // Create Stock entry if opening stock provided
            if (isset($validated['opening_stock'])) {
                \App\Models\Stock::create([
                    'item_id' => $item->id,
                    'count' => $validated['opening_stock'],
                    'account_id' => auth()->user()->current_account_id,
                ]);
            }
        });

        return redirect()->back()->with('success', 'Item created successfully');
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Item $item)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'required|integer',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
        ]);

        $item->update($validated);

        return redirect()->back()->with('success', 'Item updated successfully.');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Item $item)
    {
        $item->delete();

        return redirect()->back()->with('success', 'Item deleted successfully.');
    }
}
