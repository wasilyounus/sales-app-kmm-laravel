<?php

namespace App\Services;

use App\Models\JournalEntry;
use App\Models\JournalEntryLine;
use App\Models\ChartOfAccount;
use App\Models\Sale;
use App\Models\Purchase;
use Illuminate\Support\Facades\DB;

class JournalEntryService
{
    /**
     * Create journal entry from a Sale
     * 
     * Entry:
     * DR: Accounts Receivable (or Cash) - Total with tax
     * CR: Sales Revenue - Subtotal
     * CR: Tax Payable - Tax amount
     */
    public function createFromSale(Sale $sale): JournalEntry
    {
        return DB::transaction(function () use ($sale) {
            // Get accounts
            $arAccount = $this->getChartOfAccountByCode($sale->company_id, '1130'); // Accounts Receivable
            $salesAccount = $this->getChartOfAccountByCode($sale->company_id, '4100'); // Sales Revenue
            $taxAccount = $this->getChartOfAccountByCode($sale->company_id, '2120'); // Tax Payable

            // Create journal entry
            $entry = JournalEntry::create([
                'entry_number' => $this->generateEntryNumber($sale->company_id),
                'entry_date' => $sale->date,
                'reference' => $sale->sale_no ?? "SALE-{$sale->id}",
                'description' => "Sale to {$sale->party->name}",
                'source_type' => 'SALE',
                'source_id' => $sale->id,
                'company_id' => $sale->company_id,
                'created_by' => auth()->id(),
            ]);

            $lineNumber = 1;

            // Debit: Accounts Receivable (Full amount including tax)
            $entry->lines()->create([
                'line_number' => $lineNumber++,
                'coa_id' => $arAccount->id,
                'description' => "Sale #{$sale->sale_no}",
                'debit_amount' => $sale->total,
                'credit_amount' => 0,
                'party_id' => $sale->party_id,
                'party_type' => 'CUSTOMER',
            ]);

            // Credit: Sales Revenue (Subtotal)
            $entry->lines()->create([
                'line_number' => $lineNumber++,
                'coa_id' => $salesAccount->id,
                'description' => "Sale #{$sale->sale_no}",
                'debit_amount' => 0,
                'credit_amount' => $sale->subtotal,
            ]);

            // Credit: Tax Payable (if tax exists)
            if ($sale->tax_amount > 0) {
                $entry->lines()->create([
                    'line_number' => $lineNumber++,
                    'coa_id' => $taxAccount->id,
                    'description' => "Tax on Sale #{$sale->sale_no}",
                    'debit_amount' => 0,
                    'credit_amount' => $sale->tax_amount,
                ]);
            }

            // Post the entry
            $entry->post();

            return $entry;
        });
    }

    /**
     * Create journal entry from a Purchase
     * 
     * Entry:
     * DR: Purchases (COGS) - Subtotal
     * DR: Tax Receivable - Tax amount
     * CR: Accounts Payable (or Cash) - Total with tax
     */
    public function createFromPurchase(Purchase $purchase): JournalEntry
    {
        return DB::transaction(function () use ($purchase) {
            // Get accounts
            $apAccount = $this->getChartOfAccountByCode($purchase->company_id, '2110'); // Accounts Payable
            $purchasesAccount = $this->getChartOfAccountByCode($purchase->company_id, '5110'); // Purchases (COGS)
            $taxAccount = $this->getChartOfAccountByCode($purchase->company_id, '2120'); // Tax (as receivable in some jurisdictions)

            // Create journal entry
            $entry = JournalEntry::create([
                'entry_number' => $this->generateEntryNumber($purchase->company_id),
                'entry_date' => $purchase->date,
                'reference' => $purchase->purchase_no ?? "PURCHASE-{$purchase->id}",
                'description' => "Purchase from {$purchase->party->name}",
                'source_type' => 'PURCHASE',
                'source_id' => $purchase->id,
                'company_id' => $purchase->company_id,
                'created_by' => auth()->id(),
            ]);

            $lineNumber = 1;

            // Debit: Purchases/COGS (Subtotal)
            $entry->lines()->create([
                'line_number' => $lineNumber++,
                'coa_id' => $purchasesAccount->id,
                'description' => "Purchase #{$purchase->purchase_no}",
                'debit_amount' => $purchase->subtotal,
                'credit_amount' => 0,
            ]);

            // Debit: Tax (if tax exists)
            if ($purchase->tax_amount > 0) {
                $entry->lines()->create([
                    'line_number' => $lineNumber++,
                    'coa_id' => $taxAccount->id,
                    'description' => "Tax on Purchase #{$purchase->purchase_no}",
                    'debit_amount' => $purchase->tax_amount,
                    'credit_amount' => 0,
                ]);
            }

            // Credit: Accounts Payable (Full amount including tax)
            $entry->lines()->create([
                'line_number' => $lineNumber++,
                'coa_id' => $apAccount->id,
                'description' => "Purchase #{$purchase->purchase_no}",
                'debit_amount' => 0,
                'credit_amount' => $purchase->total,
                'party_id' => $purchase->party_id,
                'party_type' => 'VENDOR',
            ]);

            // Post the entry
            $entry->post();

            return $entry;
        });
    }

    /**
     * Create journal entry for an expense
     */
    public function createFromExpense(array $data): JournalEntry
    {
        return DB::transaction(function () use ($data) {
            // Get accounts
            $expenseAccount = $this->getChartOfAccountByCode($data['company_id'], $data['expense_account_code']);
            $cashAccount = $this->getChartOfAccountByCode($data['company_id'], '1110'); // Cash

            // Create journal entry
            $entry = JournalEntry::create([
                'entry_number' => $this->generateEntryNumber($data['company_id']),
                'entry_date' => $data['date'],
                'reference' => $data['reference'] ?? null,
                'description' => $data['description'],
                'source_type' => 'EXPENSE',
                'company_id' => $data['company_id'],
                'created_by' => auth()->id(),
            ]);

            // Debit: Expense Account
            $entry->lines()->create([
                'line_number' => 1,
                'coa_id' => $expenseAccount->id,
                'description' => $data['description'],
                'debit_amount' => $data['amount'],
                'credit_amount' => 0,
                'party_id' => $data['party_id'] ?? null,
                'party_type' => $data['party_type'] ?? null,
            ]);

            // Credit: Cash
            $entry->lines()->create([
                'line_number' => 2,
                'coa_id' => $cashAccount->id,
                'description' => $data['description'],
                'debit_amount' => 0,
                'credit_amount' => $data['amount'],
            ]);

            // Post the entry
            $entry->post();

            return $entry;
        });
    }

    /**
     * Generate unique entry number
     */
    public function generateEntryNumber(int $accountId): string
    {
        $year = now()->year;
        $month = str_pad(now()->month, 2, '0', STR_PAD_LEFT);

        $lastEntry = JournalEntry::where('company_id', $accountId)
            ->where('entry_number', 'like', "JE-{$year}-{$month}-%")
            ->orderBy('entry_number', 'desc')
            ->first();

        if ($lastEntry) {
            $lastNumber = (int) substr($lastEntry->entry_number, -4);
            $nextNumber = $lastNumber + 1;
        } else {
            $nextNumber = 1;
        }

        return sprintf('JE-%s-%s-%04d', $year, $month, $nextNumber);
    }

    /**
     * Get Chart of Account by code
     */
    protected function getChartOfAccountByCode(int $accountId, string $code): ChartOfAccount
    {
        $coa = ChartOfAccount::where('company_id', $accountId)
            ->where('account_code', $code)
            ->where('is_active', true)
            ->first();

        if (!$coa) {
            throw new \Exception("Chart of Account with code {$code} not found for this company");
        }

        return $coa;
    }
}
