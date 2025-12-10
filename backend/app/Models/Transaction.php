<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Transaction extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $fillable = [
        'credit_code',
        'debit_code',
        'type',
        'amount',
        'date',
        'comment',
        'company_id',
        'log_id',
    ];

    protected $casts = [
        'amount' => 'decimal:2',
        'date' => 'date',
    ];

    public function account()
    {
        return $this->belongsTo(Company::class);
    }

    // Transaction type constants
    const TYPE_CASH_RECEIVED = 1;
    const TYPE_CASH_PAID = 2;
    const TYPE_PURCHASE = 3;
    const TYPE_SALE = 4;
    const TYPE_PURCHASE_RETURN = 5;
    const TYPE_SALE_RETURN = 6;
    const TYPE_INITIAL = 7;
    const TYPE_ADJUSTMENT = 8;
    const TYPE_DISCOUNT = 9;
    const TYPE_OTHER = 10;
    const TYPE_CHEQUE_RECEIVED = 11;
    const TYPE_CHEQUE_PAID = 12;
    const TYPE_NEFT_RECEIVED = 13;
    const TYPE_NEFT_PAID = 14;
    const TYPE_UPI_RECEIVED = 15;
    const TYPE_UPI_PAID = 16;
    const TYPE_SET_TO_ZERO = 17;
    const TYPE_ROUND_OFF = 18;
}
