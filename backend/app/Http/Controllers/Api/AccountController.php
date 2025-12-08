<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Account;
use Illuminate\Http\Request;

class AccountController extends Controller
{
    /**
     * Display a listing of accounts
     */
    public function index()
    {
        $accounts = Account::all();

        return response()->json([
            'success' => true,
            'data' => $accounts,
        ]);
    }

    /**
     * Store a newly created account
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'name_formatted' => 'required|string|max:255',
            'desc' => 'required|string|max:255',
            'taxation_type' => 'sometimes|integer',
            'tax_number' => 'nullable|string|max:255',
            'address' => 'nullable|string',
            'call' => 'nullable|string|max:255',
            'whatsapp' => 'nullable|string|max:255',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'log_id' => 'required|integer',
            'financial_year_start' => 'nullable|date_format:Y-m-d H:i:s',
        ]);

        $account = Account::create($validated);

        return response()->json([
            'success' => true,
            'message' => 'Account created successfully',
            'data' => $account,
        ], 201);
    }

    /**
     * Display the specified account
     */
    public function show($id)
    {
        $account = Account::findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => $account,
        ]);
    }

    /**
     * Update the specified account
     */
    public function update(Request $request, $id)
    {
        $account = Account::findOrFail($id);

        $validated = $request->validate([
            'name' => 'sometimes|required|string|max:255',
            'name_formatted' => 'sometimes|required|string|max:255',
            'desc' => 'sometimes|required|string|max:255',
            'taxation_type' => 'sometimes|integer',
            'tax_number' => 'nullable|string|max:255',
            'address' => 'nullable|string',
            'call' => 'nullable|string|max:255',
            'whatsapp' => 'nullable|string|max:255',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'log_id' => 'sometimes|required|integer',
            'financial_year_start' => 'nullable|date_format:Y-m-d H:i:s',
            'default_tax_id' => 'nullable|integer|exists:taxes,id',
            'country' => 'nullable|string|max:255',
            'state' => 'nullable|string|max:255',
            'enable_delivery_notes' => 'sometimes|boolean',
            'enable_grns' => 'sometimes|boolean',
        ]);

        $account->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Account updated successfully',
            'data' => $account,
        ]);
    }
}
