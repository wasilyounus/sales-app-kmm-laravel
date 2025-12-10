<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class JournalEntry extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $fillable = [
        'entry_number',
        'entry_date',
        'posting_date',
        'reference',
        'description',
        'is_posted',
        'is_reversed',
        'reversed_by_id',
        'source_type',
        'source_id',
        'company_id',
        'created_by',
        'posted_by',
        'log_id'
    ];

    protected $casts = [
        'entry_date' => 'date',
        'posting_date' => 'date',
        'is_posted' => 'boolean',
        'is_reversed' => 'boolean',
    ];

    // ========== Relationships ==========

    public function lines()
    {
        return $this->hasMany(JournalEntryLine::class);
    }

    public function company()
    {
        return $this->belongsTo(Company::class);
    }

    public function reversedBy()
    {
        return $this->belongsTo(JournalEntry::class, 'reversed_by_id');
    }

    public function createdBy()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function postedBy()
    {
        return $this->belongsTo(User::class, 'posted_by');
    }

    // ========== Methods ==========

    public function post(): void
    {
        if ($this->is_posted) {
            throw new \Exception('Journal entry already posted');
        }

        // Validate balanced entry
        if (!$this->isBalanced()) {
            throw new \Exception('Journal entry is not balanced. Debits must equal credits.');
        }

        $this->update([
            'is_posted' => true,
            'posting_date' => now(),
            'posted_by' => auth()->id()
        ]);
    }

    public function isBalanced(): bool
    {
        $totalDebits = $this->lines()->sum('debit_amount');
        $totalCredits = $this->lines()->sum('credit_amount');

        return bccomp($totalDebits, $totalCredits, 2) === 0;
    }

    public function reverse(string $reason): JournalEntry
    {
        if (!$this->is_posted) {
            throw new \Exception('Cannot reverse unposted entry');
        }

        if ($this->is_reversed) {
            throw new \Exception('Entry already reversed');
        }

        $reversingEntry = static::create([
            'entry_number' => app(\App\Services\JournalEntryService::class)->generateEntryNumber($this->company_id),
            'entry_date' => now(),
            'description' => "Reversal of entry #{$this->entry_number}",
            'source_type' => 'REVERSAL',
            'source_id' => $this->id,
            'company_id' => $this->company_id,
            'created_by' => auth()->id(),
        ]);

        // Create opposite lines
        foreach ($this->lines as $line) {
            $reversingEntry->lines()->create([
                'coa_id' => $line->coa_id,
                'line_number' => $line->line_number,
                'debit_amount' => $line->credit_amount,
                'credit_amount' => $line->debit_amount,
                'description' => $line->description,
                'party_id' => $line->party_id,
                'party_type' => $line->party_type,
            ]);
        }

        $reversingEntry->post();

        $this->update([
            'is_reversed' => true,
            'reversed_by_id' => $reversingEntry->id
        ]);

        return $reversingEntry;
    }

    public function getTotalDebits(): float
    {
        return (float) $this->lines()->sum('debit_amount');
    }

    public function getTotalCredits(): float
    {
        return (float) $this->lines()->sum('credit_amount');
    }

    // ========== Scopes ==========

    public function scopePosted($query)
    {
        return $query->where('is_posted', true);
    }

    public function scopeUnposted($query)
    {
        return $query->where('is_posted', false);
    }

    public function scopeBySourceType($query, string $type)
    {
        return $query->where('source_type', $type);
    }
}
