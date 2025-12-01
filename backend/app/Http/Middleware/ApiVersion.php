<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class ApiVersion
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next, string $version = 'v1'): Response
    {
        // Set the API version in the request
        $request->attributes->set('api_version', $version);
        
        // Add version to response headers
        $response = $next($request);
        $response->headers->set('X-API-Version', $version);
        
        return $response;
    }
}
