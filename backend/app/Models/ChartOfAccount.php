<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class ChartOfAccount extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $table = 'chart_of_accounts';

    protected $fillable = [
        'account_code',
        'account_name',
        'account_type',
        'normal_balance',
        'parent_account_id',
        'is_active',
        'is_system',
        'description',
        'company_id'
    ];

    protected $casts = [
        'is_active' => 'boolean',
        'is_system' => 'boolean',
    ];

    // Account types
    const TYPE_ASSET = 'ASSET';
    const TYPE_LIABILITY = 'LIABILITY';
    const TYPE_EQUITY = 'EQUITY';
    const TYPE_REVENUE = 'REVENUE';
    const TYPE_EXPENSE = 'EXPENSE';

    // Normal balances
    const BALANCE_DEBIT = 'DEBIT';
    const BALANCE_CREDIT = 'CREDIT';

    // ========== Relationships ==========

    public function parent()
    {
        return $this->belongsTo(ChartOfAccount::class, 'parent_account_id');
    }

    public function children()
    {
        return $this->hasMany(ChartOfAccount::class, 'parent_account_id');
    }

    public function company()
    {
        return $this->belongsTo(Company::class);
    }

    public function journalLines()
    {
        return $this->hasMany(JournalEntryLine::class, 'coa_id');
    }

    // ========== Helpers ==========

    public function isDebitAccount(): bool
    {
        return $this->normal_balance === self::BALANCE_DEBIT;
    }

    public function isCreditAccount(): bool
    {
        return $this->normal_balance === self::BALANCE_CREDIT;
    }

    public function isAsset(): bool
    {
        return $this->account_type === self::TYPE_ASSET;
    }

    public function isLiability(): bool
    {
        return $this->account_type === self::TYPE_LIABILITY;
    }

    public function isRevenue(): bool
    {
        return $this->account_type === self::TYPE_REVENUE;
    }

    public function isExpense(): bool
    {
        return $this->account_type === self::TYPE_EXPENSE;
    }

    // ========== Scopes ==========

    public function scopeActive($query)
    {
        return $query->where('is_active', true);
    }

    public function scopeByType($query, string $type)
    {
        return $query->where('account_type', $type);
    }

    public function scopeByCode($query, string $code)
    {
        return $query->where('account_code', $code);
    }
}
