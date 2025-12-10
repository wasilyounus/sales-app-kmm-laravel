<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class EnsureUserHasCompany
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

            // Check if user has current_company_id in DB or session
            $currentCompanyId = $user->current_company_id ?? session('current_company_id');

            if (!$currentCompanyId) {
                // Don't block company selection routes
                if ($request->is('admin/select-company*')) {
                    return $next($request);
                }

                // Don't create redirect loop - just pass through and let frontend handle
                // The Inertia middleware will provide currentCompany as null
                return $next($request);
            }

            // Validate user still has access to this company
            if (!$user->companies()->where('companies.id', $currentCompanyId)->exists()) {
                session()->forget('current_company_id');
                $user->update(['current_company_id' => null]);

                // Pass through with null company
                return $next($request);
            }
        }

        return $next($request);
    }
}
