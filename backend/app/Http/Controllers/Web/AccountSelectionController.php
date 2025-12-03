<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class AccountSelectionController extends Controller
{
    /**
     * Store the selected account in session
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'account_id' => 'required|integer|exists:accounts,id',
        ]);

        $accountId = $validated['account_id'];

        // Verify user has access to this account
        $hasAccess = auth()->user()->accounts()->where('accounts.id', $accountId)->exists();

        if (!$hasAccess) {
            return response()->json([
                'message' => 'You do not have access to this account.'
            ], 403);
        }

        // Store in session
        session(['current_account_id' => $accountId]);

        // Also update user's current_account_id if column exists
        if (auth()->user()) {
            auth()->user()->update(['current_account_id' => $accountId]);
        }

        return response()->json([
            'message' => 'Account selected successfully.',
            'account_id' => $accountId,
        ]);
    }

    /**
     * Get user's available accounts
     */
    public function index()
    {
        $accounts = auth()->user()->getAccountsForSelection();

        return response()->json([
            'accounts' => $accounts,
        ]);
    }
}
