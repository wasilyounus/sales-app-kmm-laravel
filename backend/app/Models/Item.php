<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Item extends Model
{
    use SoftDeletes, \App\Traits\HasLog, \Illuminate\Database\Eloquent\Factories\HasFactory;

    public $timestamps = true;

    protected $fillable = [
        'name',
        'alt_name',
        'brand',
        'size',
        'uqc',
        'hsn',
        'account_id',
        'log_id',
        'tax_id',
    ];

    public function tax()
    {
        return $this->belongsTo(Tax::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }

    public function stock()
    {
        return $this->hasOne(Stock::class);
    }

    public function quoteItems()
    {
        return $this->hasMany(QuoteItem::class);
    }

    public function orderItems()
    {
        return $this->hasMany(OrderItem::class);
    }

    public function saleItems()
    {
        return $this->hasMany(SaleItem::class);
    }

    public function purchaseItems()
    {
        return $this->hasMany(PurchaseItem::class);
    }

    public function uqc_data()
    {
        return $this->belongsTo(Uqc::class, 'uqc', 'uqc');
    }
}
