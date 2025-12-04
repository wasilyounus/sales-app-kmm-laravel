<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Party;
use Illuminate\Http\Request;
use Inertia\Inertia;

class PartyWebController extends Controller
{
    /**
     * Display a listing of parties
     */
    public function index(Request $request)
    {
        $query = Party::query();

        if ($request->has('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                    ->orWhere('phone', 'like', "%{$search}%")
                    ->orWhere('email', 'like', "%{$search}%");
            });
        }

        $parties = $query->with('addresses')->orderBy('id', 'desc')->paginate(10);

        return Inertia::render('Parties/Index', [
            'parties' => $parties,
            'filters' => $request->only(['search'])
        ]);
    }

    /**
     * Store a newly created party
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'tax_number' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:20',
            'email' => 'nullable|email|max:255',
            'addresses' => 'nullable|array',
            'addresses.*.line1' => 'required_with:addresses|string|max:255',
            'addresses.*.line2' => 'nullable|string|max:255',
            'addresses.*.city' => 'required_with:addresses|string|max:100',
            'addresses.*.state' => 'required_with:addresses|string|max:100',
            'addresses.*.pincode' => 'required_with:addresses|numeric',
            'addresses.*.country' => 'required_with:addresses|string|max:100',
        ]);

        $validated['account_id'] = auth()->user()->current_account_id;
        $validated['log_id'] = 1;

        $party = Party::create($validated);

        if (!empty($request->addresses)) {
            foreach ($request->addresses as $addr) {
                $party->addresses()->create([
                    'line1' => $addr['line1'],
                    'line2' => $addr['line2'] ?? null,
                    'city' => $addr['city'],
                    'state' => $addr['state'],
                    'pincode' => $addr['pincode'],
                    'country' => $addr['country'],
                    'account_id' => $party->account_id,
                    'log_id' => 1
                ]);
            }
        }

        return back()->with('success', 'Party created successfully');
    }

    /**
     * Update the specified party
     */
    public function update(Request $request, $id)
    {
        $party = Party::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'tax_number' => 'nullable|string|max:255',
            'phone' => 'nullable|string|max:20',
            'email' => 'nullable|email|max:255',
            'addresses' => 'nullable|array',
            'addresses.*.id' => 'nullable|integer|exists:addresses,id',
            'addresses.*.line1' => 'required_with:addresses|string|max:255',
            'addresses.*.line2' => 'nullable|string|max:255',
            'addresses.*.city' => 'required_with:addresses|string|max:100',
            'addresses.*.state' => 'required_with:addresses|string|max:100',
            'addresses.*.pincode' => 'required_with:addresses|numeric',
            'addresses.*.country' => 'required_with:addresses|string|max:100',
        ]);

        $party->update($validated);

        // Sync addresses
        if ($request->has('addresses')) {
            $currentIds = collect($request->addresses)->pluck('id')->filter()->toArray();
            // Delete removed addresses
            $party->addresses()->whereNotIn('id', $currentIds)->delete();

            foreach ($request->addresses as $addr) {
                if (isset($addr['id'])) {
                    $party->addresses()->where('id', $addr['id'])->update([
                        'line1' => $addr['line1'],
                        'line2' => $addr['line2'] ?? null,
                        'city' => $addr['city'],
                        'state' => $addr['state'],
                        'pincode' => $addr['pincode'],
                        'country' => $addr['country'],
                    ]);
                } else {
                    $party->addresses()->create([
                        'line1' => $addr['line1'],
                        'line2' => $addr['line2'] ?? null,
                        'city' => $addr['city'],
                        'state' => $addr['state'],
                        'pincode' => $addr['pincode'],
                        'country' => $addr['country'],
                        'account_id' => $party->account_id,
                        'log_id' => 1
                    ]);
                }
            }
        }

        return back()->with('success', 'Party updated successfully');
    }

    /**
     * Remove the specified party
     */
    public function destroy($id)
    {
        $party = Party::findOrFail($id);
        $party->delete();

        return back()->with('success', 'Party deleted successfully');
    }
}
