<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Party;
use Illuminate\Http\Request;

class PartyController extends Controller
{
    /**
     * Display a listing of parties
     */
    public function index(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Party::with('addresses');
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $parties = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $parties,
        ]);
    }

    /**
     * Store a newly created party
     */
    /**
     * Store a newly created party
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'gst' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:255',
            'email' => 'nullable|email|max:255',
            'account_id' => 'required|exists:accounts,id',
            'addresses' => 'nullable|array',
            'addresses.*.line1' => 'required|string|max:255',
            'addresses.*.line2' => 'nullable|string|max:255',
            'addresses.*.city' => 'required|string|max:255',
            'addresses.*.state' => 'required|string|max:255',
            'addresses.*.pincode' => 'required|string|max:20',
            'addresses.*.country' => 'nullable|string|max:255',
        ]);

        \Illuminate\Support\Facades\DB::beginTransaction();
        try {
            $party = Party::create([
                'name' => $validated['name'],
                'gst' => $validated['gst'] ?? null,
                'phone' => $validated['phone'] ?? null,
                'email' => $validated['email'] ?? null,
                'account_id' => $validated['account_id'],
            ]);

            if (isset($validated['addresses'])) {
                foreach ($validated['addresses'] as $addr) {
                    $party->addresses()->create([
                        'account_id' => $validated['account_id'],
                        'line1' => $addr['line1'],
                        'line2' => $addr['line2'] ?? null,
                        'city' => $addr['city'],
                        'state' => $addr['state'],
                        'pincode' => $addr['pincode'],
                        'country' => $addr['country'] ?? 'India',
                    ]);
                }
            }

            \Illuminate\Support\Facades\DB::commit();
            $party->refresh();
            $party->load('addresses');

            return response()->json([
                'success' => true,
                'message' => 'Party created successfully',
                'data' => $party,
            ], 201);
        } catch (\Exception $e) {
            \Illuminate\Support\Facades\DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to create party',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Display the specified party
     */
    public function show($id)
    {
        $party = Party::with('addresses')->findOrFail($id);
        
        return response()->json([
            'success' => true,
            'data' => $party,
        ]);
    }

    /**
     * Update the specified party
     */
    public function update(Request $request, $id)
    {
        $party = Party::findOrFail($id);
        
        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'gst' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:255',
            'email' => 'nullable|email|max:255',
            'addresses' => 'nullable|array',
            'addresses.*.line1' => 'required_with:addresses|string|max:255',
            'addresses.*.line2' => 'nullable|string|max:255',
            'addresses.*.city' => 'required_with:addresses|string|max:255',
            'addresses.*.state' => 'required_with:addresses|string|max:255',
            'addresses.*.pincode' => 'required_with:addresses|string|max:20',
            'addresses.*.country' => 'nullable|string|max:255',
        ]);

        \Illuminate\Support\Facades\DB::beginTransaction();
        try {
            $party->update($validated);

            if (isset($validated['addresses'])) {
                // For simplicity, delete all and recreate. 
                // In a real app, might want to update existing ones by ID.
                $party->addresses()->delete();

                foreach ($validated['addresses'] as $addr) {
                    $party->addresses()->create([
                        'account_id' => $party->account_id,
                        'line1' => $addr['line1'],
                        'line2' => $addr['line2'] ?? null,
                        'city' => $addr['city'],
                        'state' => $addr['state'],
                        'pincode' => $addr['pincode'],
                        'country' => $addr['country'] ?? 'India',
                    ]);
                }
            }

            \Illuminate\Support\Facades\DB::commit();
            $party->refresh();
            $party->load('addresses');

            return response()->json([
                'success' => true,
                'message' => 'Party updated successfully',
                'data' => $party,
            ]);
        } catch (\Exception $e) {
            \Illuminate\Support\Facades\DB::rollBack();
            return response()->json([
                'success' => false,
                'message' => 'Failed to update party',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    /**
     * Remove the specified party (soft delete)
     */
    public function destroy($id)
    {
        $party = Party::findOrFail($id);
        $party->delete();

        return response()->json([
            'success' => true,
            'message' => 'Party deleted successfully',
        ]);
    }
}
