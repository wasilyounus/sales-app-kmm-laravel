<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Inertia\Inertia;

class AuthController extends Controller
{
    public function create()
    {
        return Inertia::render('Auth/Login');
    }

    public function store(Request $request)
    {
        $credentials = $request->validate([
            'email' => ['required', 'email'],
            'password' => ['required'],
        ]);

        if (Auth::attempt($credentials, $request->boolean('remember'))) {
            $request->session()->regenerate();

            // Check if user has companies
            $user = Auth::user();
            if (!$user->hasCompanies()) {
                Auth::logout();
                return back()->withErrors([
                    'email' => 'You do not have access to any companies.',
                ]);
            }


            // Auto-select user's first company if they don't have one selected
            $user = Auth::user();
            if (!$user->current_company_id) {
                $firstCompany = $user->companies()->first();
                if ($firstCompany) {
                    $user->update(['current_company_id' => $firstCompany->id]);
                    session(['current_company_id' => $firstCompany->id]);
                }
            }

            return redirect()->intended(route('admin.dashboard'));
        }

        return back()->withErrors([
            'email' => 'The provided credentials do not match our records.',
        ])->onlyInput('email');
    }

    public function destroy(Request $request)
    {
        Auth::guard('web')->logout();

        $request->session()->invalidate();

        $request->session()->regenerateToken();

        return redirect('/');
    }
}
