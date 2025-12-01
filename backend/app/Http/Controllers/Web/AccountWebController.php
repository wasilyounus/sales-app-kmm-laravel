<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Account;
use Illuminate\Http\Request;
use Inertia\Inertia;

class AccountWebController extends Controller
{
    /**
     * Display a listing of accounts
     */
    public function index()
    {
        $accounts = Account::orderBy('id', 'desc')->get();
        
        return Inertia::render('Accounts/Index', [
            'accounts' => $accounts
        ]);
    }

    /**
     * Show the form for creating a new account
     */
    public function create()
    {
        return Inertia::render('Accounts/Create');
    }

    /**
     * Store a newly created account
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'name_formatted' => 'required|string|max:255',
            'desc' => 'nullable|string',
            'taxation_type' => 'required|integer|in:1,2,3',
            'tax_rate' => 'required|integer|min:0|max:100',
            'gst' => 'nullable|string|max:20',
            'address' => 'nullable|string',
            'call' => 'nullable|string',
            'whatsapp' => 'nullable|string',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'financial_year_start' => 'nullable|date_format:Y-m-d H:i:s',
        ]);

        // Create log entry
        $log = \App\Models\Log::create([
            'user_id' => auth()->id(),
            'model' => 'Account',
            'action' => 'created',
            'data' => json_encode(['name' => $validated['name']])
        ]);

        $validated['log_id'] = $log->id;
        $validated['signature'] = $request->boolean('signature');

        Account::create($validated);

        return redirect()->route('accounts.index')
            ->with('success', 'Account created successfully');
    }

    /**
     * Show the form for editing an account
     */
    public function edit($id)
    {
        $account = Account::findOrFail($id);
        
        return Inertia::render('Accounts/Edit', [
            'account' => $account
        ]);
    }

    /**
     * Update the specified account
     */
    public function update(Request $request, $id)
    {
        $account = Account::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'name_formatted' => 'required|string|max:255',
            'desc' => 'nullable|string',
            'taxation_type' => 'required|integer|in:1,2,3',
            'tax_rate' => 'required|integer|min:0|max:100',
            'gst' => 'nullable|string|max:20',
            'address' => 'nullable|string',
            'call' => 'nullable|string',
            'whatsapp' => 'nullable|string',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'financial_year_start' => 'nullable|date_format:Y-m-d H:i:s',
        ]);

        $validated['signature'] = $request->boolean('signature');

        $account->update($validated);

        return redirect()->route('accounts.index')
            ->with('success', 'Account updated successfully');
    }

    /**
     * Remove the specified account
     */
    public function destroy($id)
    {
        $account = Account::findOrFail($id);
        $account->delete();

        return redirect()->route('accounts.index')
            ->with('success', 'Account deleted successfully');
    }
}
