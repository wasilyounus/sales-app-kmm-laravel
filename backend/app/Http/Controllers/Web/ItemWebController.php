<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Item;
use App\Models\Tax;
use App\Models\Uqc;
use Illuminate\Http\Request;
use Inertia\Inertia;

class ItemWebController extends Controller
{
    /**
     * Display a listing of items
     */
    public function index(Request $request)
    {
        $query = Item::query();

        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                    ->orWhere('brand', 'like', "%{$search}%");
            });
        }

        $items = $query->orderBy('id', 'desc')->paginate(10);
        $taxes = Tax::where('active', true)->get();
        $uqcs = Uqc::where('active', true)->get();

        return Inertia::render('Items/Index', [
            'items' => $items,
            'taxes' => $taxes,
            'uqcs' => $uqcs,
            'filters' => $request->only(['search'])
        ]);
    }

    /**
     * Store a newly created item
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'required|integer|exists:uqcs,id',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
        ]);

        $validated['account_id'] = auth()->user()->current_account_id;

        // Create log entry (simplified for now)
        $validated['log_id'] = 1;

        Item::create($validated);

        return back()->with('success', 'Item created successfully');
    }

    /**
     * Update the specified item
     */
    public function update(Request $request, $id)
    {
        $item = Item::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'required|integer|exists:uqcs,id',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
        ]);

        $item->update($validated);

        return back()->with('success', 'Item updated successfully');
    }

    /**
     * Remove the specified item
     */
    public function destroy($id)
    {
        $item = Item::findOrFail($id);
        $item->delete();

        return back()->with('success', 'Item deleted successfully');
    }
}
