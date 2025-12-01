<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Party extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = true;

    protected $fillable = [
        'name',
        'gst',
        'phone',
        'email',
        'account_id',
        'log_id',
    ];

    public function account()
    {
        return $this->belongsTo(Account::class);
    }

    public function addresses()
    {
        return $this->hasMany(Address::class);
    }

    public function quotes()
    {
        return $this->hasMany(Quote::class);
    }

    public function orders()
    {
        return $this->hasMany(Order::class);
    }

    public function sales()
    {
        return $this->hasMany(Sale::class);
    }

    public function purchases()
    {
        return $this->hasMany(Purchase::class);
    }
}
