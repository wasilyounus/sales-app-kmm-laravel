<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class PurchaseItem extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $fillable = [
        'purchase_id',
        'item_id',
        'price',
        'qty',
        'tax_id',
        'company_id',
        'log_id',
    ];

    protected $casts = [
        'price' => 'decimal:2',
        'qty' => 'decimal:3',
    ];

    public function purchase()
    {
        return $this->belongsTo(Purchase::class);
    }

    public function item()
    {
        return $this->belongsTo(Item::class);
    }

    public function tax()
    {
        return $this->belongsTo(Tax::class);
    }

    public function account()
    {
        return $this->belongsTo(Company::class);
    }
}
