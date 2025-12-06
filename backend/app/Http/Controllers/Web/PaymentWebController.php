<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use App\Models\Party;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Inertia\Inertia;

class PaymentWebController extends Controller
{
    private function getAccountId()
    {
        return auth()->user()->current_account_id ?? session('current_account_id');
    }

    public function index(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $query = Transaction::query()
            ->where('account_id', $accountId)
            ->whereIn('type', [
                Transaction::TYPE_CASH_RECEIVED,
                Transaction::TYPE_CASH_PAID,
                Transaction::TYPE_CHEQUE_RECEIVED,
                Transaction::TYPE_CHEQUE_PAID,
                Transaction::TYPE_UPI_RECEIVED,
                Transaction::TYPE_UPI_PAID,
                Transaction::TYPE_NEFT_RECEIVED,
                Transaction::TYPE_NEFT_PAID,
            ]);

        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('comment', 'like', "%{$search}%")
                  ->orWhere('amount', 'like', "%{$search}%");
            });
        }

        $transactions = $query->orderBy('date', 'desc')->paginate(20)->withQueryString();

        // Fetch party names for codes
        $partyIds = $transactions->getCollection()->map(function($t) {
            return [$t->credit_code, $t->debit_code];
        })->flatten()->unique()->filter(fn($id) => $id > 0)->values();
        
        $parties = Party::whereIn('id', $partyIds)->pluck('name', 'id');

        $transactions->getCollection()->transform(function ($t) use ($parties) {
            $isReceived = in_array($t->type, [
                Transaction::TYPE_CASH_RECEIVED, Transaction::TYPE_CHEQUE_RECEIVED, 
                Transaction::TYPE_UPI_RECEIVED, Transaction::TYPE_NEFT_RECEIVED
            ]);

            // Determine other party
            $otherPartyId = $isReceived ? $t->debit_code : $t->credit_code;
            $otherPartyName = $otherPartyId > 0 ? ($parties[$otherPartyId] ?? 'Unknown Party') : 'Cash/Bank';

            return [
                'id' => $t->id,
                'date' => $t->date->format('Y-m-d'),
                'amount' => $t->amount,
                'type' => $t->type,
                'type_label' => $this->getTypeLabel($t->type),
                'is_received' => $isReceived,
                'party_name' => $otherPartyName,
                'party_id' => $otherPartyId > 0 ? $otherPartyId : null,
                'comment' => $t->comment,
            ];
        });

        $allParties = Party::where('account_id', $accountId)->orderBy('name')->get(['id', 'name']);

        return Inertia::render('Payments/Index', [
            'transactions' => $transactions,
            'parties' => $allParties,
            'filters' => $request->only(['search']),
        ]);
    }

    public function store(Request $request)
    {
        $accountId = $this->getAccountId();
        
        $request->validate([
            'party_id' => 'required|exists:parties,id',
            'amount' => 'required|numeric|min:0.01',
            'date' => 'required|date',
            'type' => 'required|in:received,paid',
            'method' => 'required|in:cash,cheque,upi,neft',
            'comment' => 'nullable|string',
        ]);

        $typeMap = [
            'received' => [
                'cash' => Transaction::TYPE_CASH_RECEIVED,
                'cheque' => Transaction::TYPE_CHEQUE_RECEIVED,
                'upi' => Transaction::TYPE_UPI_RECEIVED,
                'neft' => Transaction::TYPE_NEFT_RECEIVED,
            ],
            'paid' => [
                'cash' => Transaction::TYPE_CASH_PAID,
                'cheque' => Transaction::TYPE_CHEQUE_PAID,
                'upi' => Transaction::TYPE_UPI_PAID,
                'neft' => Transaction::TYPE_NEFT_PAID,
            ]
        ];

        $transactionType = $typeMap[$request->type][$request->method];
        
        // Logic:
        // Received: Came from Party (Debit), Gone to Cash (Credit=0)
        // Paid: Came from Cash (Debit=0), Gone to Party (Credit)
        
        $debitCode = $request->type === 'received' ? $request->party_id : 0;
        $creditCode = $request->type === 'paid' ? $request->party_id : 0;

        Transaction::create([
            'account_id' => $accountId,
            'date' => $request->date,
            'amount' => $request->amount,
            'type' => $transactionType,
            'debit_code' => $debitCode,
            'credit_code' => $creditCode,
            'comment' => $request->comment ?? '',
            'log_id' => 1,
        ]);

        return redirect()->back()->with('success', 'Payment recorded successfully.');
    }

    private function getTypeLabel($type)
    {
        return match($type) {
            Transaction::TYPE_CASH_RECEIVED => 'Cash Received',
            Transaction::TYPE_CASH_PAID => 'Cash Paid',
            Transaction::TYPE_CHEQUE_RECEIVED => 'Cheque Received',
            Transaction::TYPE_CHEQUE_PAID => 'Cheque Paid',
            Transaction::TYPE_UPI_RECEIVED => 'UPI Received',
            Transaction::TYPE_UPI_PAID => 'UPI Paid',
            Transaction::TYPE_NEFT_RECEIVED => 'NEFT Received',
            Transaction::TYPE_NEFT_PAID => 'NEFT Paid',
            default => 'Unknown',
        };
    }
}
