<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class EnsureUserHasAccount
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        if (auth()->check()) {
            $user = auth()->user();
            
            // Check if user has current_account_id in DB or session
            $currentAccountId = $user->current_account_id ?? session('current_account_id');
            
            if (!$currentAccountId) {
                // User needs to select an account
                // Don't block account selection routes
                if ($request->is('admin/select-account*')) {
                    return $next($request);
                }
                
                // Redirect to a page that shows account selection
                return redirect()->route('admin.dashboard')
                    ->with('show_account_selection', true);
            }
            
            // Validate user still has access to this account
            if (!$user->accounts()->where('accounts.id', $currentAccountId)->exists()) {
                session()->forget('current_account_id');
                return redirect()->route('admin.dashboard')
                    ->with('show_account_selection', true)
                    ->with('error', 'Account access revoked. Please select another account.');
            }
        }

        return $next($request);
    }
}
