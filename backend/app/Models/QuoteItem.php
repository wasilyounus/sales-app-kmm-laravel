<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class QuoteItem extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = false;

    protected $fillable = [
        'quote_id',
        'item_id',
        'price',
        'qty',
        'account_id',
        'log_id',
    ];

    protected $casts = [
        'price' => 'decimal:2',
        'qty' => 'decimal:3',
    ];

    public function quote()
    {
        return $this->belongsTo(Quote::class);
    }

    public function item()
    {
        return $this->belongsTo(Item::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
