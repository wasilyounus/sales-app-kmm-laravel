<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Quote;
use App\Models\QuoteItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class QuoteController extends Controller
{
    /**
     * Display a listing of quotes
     */
    public function index(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Quote::with(['party', 'items.item']);
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $quotes = $query->orderBy('date', 'desc')->get();
        
        return response()->json([
            'success' => true,
            'data' => $quotes,
        ]);
    }

    /**
     * Store a newly created quote with items
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'party_id' => 'required|exists:parties,id',
            'date' => 'required|date',
            'account_id' => 'required|exists:accounts,id',
            'items' => 'required|array|min:1',
            'items.*.item_id' => 'required|exists:items,id',
            'items.*.price' => 'required|numeric|min:0',
            'items.*.qty' => 'required|numeric|min:0',
        ]);

        DB::beginTransaction();
        try {
            $quote = Quote::create([
                'party_id' => $validated['party_id'],
                'date' => $validated['date'],
                'account_id' => $validated['account_id'],
            ]);
            
            $quote->refresh(); // Refresh to get auto-generated log_id

            foreach ($validated['items'] as $item) {
                QuoteItem::create([
                    'quote_id' => $quote->id,
                    'item_id' => $item['item_id'],
                    'price' => $item['price'],
                    'qty' => $item['qty'],
                    'account_id' => $validated['account_id'],
                ]);
            }

            DB::commit();

            $quote->load(['party', 'items.item']);

            return response()->json([
                'success' => true,
                'message' => 'Quote created successfully',
                'data' => $quote,
            ], 201);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create quote',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Display the specified quote
     */
    public function show($id)
    {
        $quote = Quote::with(['party', 'items.item'])->findOrFail($id);
        
        return response()->json([
            'success' => true,
            'data' => $quote,
        ]);
    }

    /**
     * Update the specified quote
     */
    public function update(Request $request, $id)
    {
        $quote = Quote::findOrFail($id);
        
        $validated = $request->validate([
            'party_id' => 'sometimes|required|exists:parties,id',
            'date' => 'sometimes|required|date',
            'items' => 'sometimes|array|min:1',
            'items.*.item_id' => 'required_with:items|exists:items,id',
            'items.*.price' => 'required_with:items|numeric|min:0',
            'items.*.qty' => 'required_with:items|numeric|min:0',
        ]);

        DB::beginTransaction();
        try {
            $quote->update($validated);

            if (isset($validated['items'])) {
                // Delete existing items
                QuoteItem::where('quote_id', $quote->id)->delete();
                
                // Create new items (they will auto-generate their own log_id via HasLog trait)
                foreach ($validated['items'] as $item) {
                    QuoteItem::create([
                        'quote_id' => $quote->id,
                        'item_id' => $item['item_id'],
                        'price' => $item['price'],
                        'qty' => $item['qty'],
                        'account_id' => $quote->account_id,
                    ]);
                }
            }

            DB::commit();

            $quote->load(['party', 'items.item']);

            return response()->json([
                'success' => true,
                'message' => 'Quote updated successfully',
                'data' => $quote,
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to update quote',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Remove the specified quote (soft delete)
     */
    public function destroy($id)
    {
        $quote = Quote::findOrFail($id);
        $quote->delete();

        return response()->json([
            'success' => true,
            'message' => 'Quote deleted successfully',
        ]);
    }

    /**
     * Get all quote items for an account
     */
    public function items(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = QuoteItem::query();
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $items = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $items,
        ]);
    }
}
