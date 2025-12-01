<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Tax extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = false;

    protected $fillable = [
        'scheme_name',
        'tax1_name',
        'tax1_val',
        'tax2_name',
        'tax2_val',
        'tax3_name',
        'tax3_val',
        'tax4_name',
        'tax4_val',
        'active',
        'log_id',
    ];

    protected $casts = [
        'active' => 'boolean',
    ];

    public function saleItems()
    {
        return $this->hasMany(SaleItem::class);
    }

    public function purchaseItems()
    {
        return $this->hasMany(PurchaseItem::class);
    }
}
