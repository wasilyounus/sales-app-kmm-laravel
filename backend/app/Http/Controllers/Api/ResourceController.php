<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Tax;
use App\Models\Uqc;
use App\Models\Stock;
use App\Models\Transport;
use Illuminate\Http\Request;

class ResourceController extends Controller
{
    /**
     * Get all taxes (global entity)
     */
    public function taxes()
    {
        $taxes = Tax::where('active', true)->get();
        
        return response()->json([
            'success' => true,
            'data' => $taxes,
        ]);
    }

    /**
     * Get all UQCs (global entity)
     */
    public function uqcs()
    {
        $uqcs = Uqc::where('active', true)->get();
        
        return response()->json([
            'success' => true,
            'data' => $uqcs,
        ]);
    }

    /**
     * Get stocks for an account
     */
    public function stocks(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Stock::with('item');
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $stocks = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $stocks,
        ]);
    }

    /**
     * Get transports for an account
     */
    public function transports(Request $request)
    {
        $accountId = $request->input('account_id');
        
        $query = Transport::where('active', true);
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $transports = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $transports,
        ]);
    }

    /**
     * Get addresses for a party
     */
    public function addresses(Request $request)
    {
        $partyId = $request->input('party_id');
        $accountId = $request->input('account_id');
        
        $query = \App\Models\Address::query();
        
        if ($partyId) {
            $query->where('party_id', $partyId);
        }
        
        if ($accountId) {
            $query->where('account_id', $accountId);
        }
        
        $addresses = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $addresses,
        ]);
    }
}
