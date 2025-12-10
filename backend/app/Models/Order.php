<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Order extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $fillable = [
        'party_id',
        'tax_id',
        'date',
        'order_no',
        'company_id',
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
        return $this->belongsTo(Company::class);
    }

    public function items()
    {
        return $this->hasMany(OrderItem::class);
    }

    public static function generateNumber($companyId)
    {
        $count = self::where('company_id', $companyId)->count() + 1;
        return 'ORD-' . str_pad($count, 4, '0', STR_PAD_LEFT);
    }
}
