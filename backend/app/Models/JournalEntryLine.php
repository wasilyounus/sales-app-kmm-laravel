<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class JournalEntryLine extends Model
{
    protected $fillable = [
        'journal_entry_id',
        'line_number',
        'coa_id',
        'description',
        'debit_amount',
        'credit_amount',
        'party_id',
        'party_type'
    ];

    protected $casts = [
        'debit_amount' => 'decimal:2',
        'credit_amount' => 'decimal:2',
    ];

    // ========== Relationships ==========

    public function journalEntry()
    {
        return $this->belongsTo(JournalEntry::class);
    }

    public function chartOfAccount()
    {
        return $this->belongsTo(ChartOfAccount::class, 'coa_id');
    }

    public function party()
    {
        return $this->belongsTo(Party::class);
    }

    // ========== Helpers ==========

    public function isDebit(): bool
    {
        return $this->debit_amount > 0;
    }

    public function isCredit(): bool
    {
        return $this->credit_amount > 0;
    }

    public function getAmount(): float
    {
        return (float) ($this->debit_amount ?: $this->credit_amount);
    }
}
