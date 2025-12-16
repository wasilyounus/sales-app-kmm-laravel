<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Log;

class TransactionController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $companyId = $request->header('X-Company-ID');
        if (!$companyId) {
            return response()->json(['error' => 'Company ID header missing'], 400);
        }

        $query = Transaction::where('company_id', $companyId);

        // Filter by Type (e.g. Sales, Purchases, Payments)
        if ($request->has('type')) {
            $query->where('type', $request->type);
        }

        // Filter by Date Range
        if ($request->has('start_date') && $request->has('end_date')) {
            $query->whereBetween('date', [$request->start_date, $request->end_date]);
        }

        $transactions = $query->orderBy('date', 'desc')->paginate(50);
        return response()->json($transactions);
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $companyId = $request->header('X-Company-ID');
        if (!$companyId) {
            return response()->json(['error' => 'Company ID header missing'], 400);
        }

        $validator = Validator::make($request->all(), [
            'type' => 'required|integer',
            'amount' => 'required|numeric|min:0',
            'date' => 'required|date',
            // 'party_id' => 'exists:parties,id', // Transactions table doesn't have party_id directly, it uses credit_code/debit_code but those are accounting codes...
            // Wait, looking at current Transaction model, it has 'credit_code' and 'debit_code'.
            // In a simple accounting system, a Payment IN from Party would debit Cash (Asset) and Credit Party (Asset/Receivable).
            // But the current Transaction model seems generic.
            // For now, let's just save the raw data. The frontend likely sends these codes.
        ]);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 422);
        }

        try {
            $transaction = Transaction::create(array_merge(
                [
                    'credit_code' => 0,
                    'debit_code' => 0,
                    'log_id' => null,
                ],
                $request->all(),
                ['company_id' => $companyId]
            ));

            return response()->json($transaction, 201);
        } catch (\Exception $e) {
            Log::error('Transaction create error: ' . $e->getMessage());
            return response()->json(['error' => 'Failed to create transaction'], 500);
        }
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $transaction = Transaction::find($id);

        if (!$transaction) {
            return response()->json(['error' => 'Transaction not found'], 404);
        }

        return response()->json($transaction);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, $id)
    {
        $transaction = Transaction::find($id);

        if (!$transaction) {
            return response()->json(['error' => 'Transaction not found'], 404);
        }

        $transaction->update($request->all());

        return response()->json($transaction);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $transaction = Transaction::find($id);

        if (!$transaction) {
            return response()->json(['error' => 'Transaction not found'], 404);
        }

        $transaction->delete();

        return response()->json(['message' => 'Transaction deleted successfully']);
    }
}
