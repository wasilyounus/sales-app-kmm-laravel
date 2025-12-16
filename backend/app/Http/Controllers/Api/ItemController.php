<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Item;
use Illuminate\Http\Request;

class ItemController extends Controller
{
    /**
     * Display a listing of items
     */
    public function index(Request $request)
    {
        $companyId = $request->input('company_id');

        $query = Item::query();

        if ($companyId) {
            $query->where('company_id', $companyId);
        }

        $items = $query->get();

        return response()->json([
            'success' => true,
            'data' => $items,
        ]);
    }

    /**
     * Store a newly created item
     */
    public function store(Request $request)
    {
        $request->mergeIfMissing(['uqc' => 43]);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'required|integer',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
            'company_id' => 'required|exists:companies,id',
        ]);

        $item = Item::create($validated);
        $item->refresh();

        return response()->json([
            'success' => true,
            'message' => 'Item created successfully',
            'data' => $item,
        ], 201);
    }

    /**
     * Display the specified item
     */
    public function show($id)
    {
        $item = Item::findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => $item,
        ]);
    }

    /**
     * Update the specified item
     */
    public function update(Request $request, $id)
    {
        $item = Item::findOrFail($id);

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'alt_name' => 'nullable|string|max:255',
            'brand' => 'nullable|string|max:255',
            'size' => 'nullable|string|max:255',
            'uqc' => 'sometimes|required|integer',
            'hsn' => 'nullable|integer',
            'tax_id' => 'nullable|integer|exists:taxes,id',
        ]);

        $item->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Item updated successfully',
            'data' => $item,
        ]);
    }

    /**
     * Remove the specified item (soft delete)
     */
    public function destroy($id)
    {
        $item = Item::findOrFail($id);
        $item->delete();

        return response()->json([
            'success' => true,
            'message' => 'Item deleted successfully',
        ]);
    }
}