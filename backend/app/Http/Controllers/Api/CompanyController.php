<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Company;
use Illuminate\Http\Request;

class CompanyController extends Controller
{
    /**
     * Display a listing of accounts accessible to the authenticated user
     */
    public function index()
    {
        $user = auth()->user();

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthenticated',
                'data' => [],
            ], 401);
        }

        $companies = $user->getCompaniesForSelection();

        return response()->json([
            'success' => true,
            'data' => $companies,
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

        $company = Company::create($validated);

        return response()->json([
            'success' => true,
            'message' => 'Company created successfully',
            'data' => $company,
        ], 201);
    }

    /**
     * Display the specified account
     */
    public function show($id)
    {
        $company = Company::with('contacts')->findOrFail($id);

        return response()->json([
            'success' => true,
            'data' => $company,
        ]);
    }

    /**
     * Update the specified account
     */
    public function update(Request $request, $id)
    {
        $company = Company::findOrFail($id);

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

        $company->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Company updated successfully',
            'data' => $company,
        ]);
    }
}
