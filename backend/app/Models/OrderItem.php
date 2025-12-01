<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class OrderItem extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = false;

    protected $fillable = [
        'order_id',
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

    public function order()
    {
        return $this->belongsTo(Order::class);
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
