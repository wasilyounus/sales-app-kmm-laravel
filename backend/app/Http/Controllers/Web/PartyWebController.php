<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Party;
use Illuminate\Http\Request;
use Inertia\Inertia;
use Illuminate\Support\Facades\DB;

class PartyWebController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = Party::query()
            ->withCount(['sales', 'purchases']);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('email', 'like', "%{$search}%")
                  ->orWhere('phone', 'like', "%{$search}%");
            });
        }

        $parties = $query->orderBy('name')->paginate(10)->withQueryString();

        // Transform data to include inferred type and balance (placeholder)
        $parties->getCollection()->transform(function ($party) {
            $party->type = $party->sales_count > 0 ? 'Customer' : ($party->purchases_count > 0 ? 'Supplier' : 'Customer');
            $party->balance = 0; // Placeholder for now
            $party->status = 'Active'; // Placeholder
            return $party;
        });

        return Inertia::render('Parties/Index', [
            'parties' => $parties,
            'filters' => $request->only(['search']),
        ]);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'nullable|email|max:255',
            'phone' => 'nullable|string|max:20',
            'gst' => 'nullable|string|max:20',
        ]);

        Party::create([
            'name' => $validated['name'],
            'email' => $validated['email'],
            'phone' => $validated['phone'],
            'gst' => $validated['gst'],
            'account_id' => 1, // Default
            'log_id' => 0, // Default
        ]);

        return redirect()->back()->with('success', 'Party created successfully.');
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Party $party)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'nullable|email|max:255',
            'phone' => 'nullable|string|max:20',
            'gst' => 'nullable|string|max:20',
        ]);

        $party->update($validated);

        return redirect()->back()->with('success', 'Party updated successfully.');
    }
}
