<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class CompanySelectionController extends Controller
{
    /**
     * Store the selected company in session
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'company_id' => 'required|integer|exists:companies,id',
        ]);

        $companyId = $validated['company_id'];

        // Verify user has access to this company
        $hasAccess = auth()->user()->companies()->where('companies.id', $companyId)->exists();

        if (!$hasAccess) {
            return response()->json([
                'message' => 'You do not have access to this company.'
            ], 403);
        }

        // Store in session
        session(['current_company_id' => $companyId]);

        // Also update user's current_company_id
        if (auth()->user()) {
            auth()->user()->update(['current_company_id' => $companyId]);
        }

        return response()->json([
            'message' => 'Company selected successfully.',
            'company_id' => $companyId,
        ]);
    }

    /**
     * Get user's available companies
     */
    public function index()
    {
        $companies = auth()->user()->companies;

        return response()->json([
            'companies' => $companies,
        ]);
    }
}
