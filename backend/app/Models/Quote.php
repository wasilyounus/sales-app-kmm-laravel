<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Quote extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = false;

    protected $fillable = [
        'party_id',
        'tax_id',
        'date',
        'account_id',
        'log_id',
    ];

    public function tax()
    {
        return $this->belongsTo(Tax::class);
    }

    protected $casts = [
        'date' => 'date',
    ];

    public function party()
    {
        return $this->belongsTo(Party::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }

    public function items()
    {
        return $this->hasMany(QuoteItem::class);
    }
}
