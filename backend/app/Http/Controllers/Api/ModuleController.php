<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class ModuleController extends Controller
{
    public function index()
    {
        $modules = \App\Models\Module::where('is_enabled', true)
            ->orderBy('sort_order')
            ->get();

        return response()->json($modules);
    }
}
