<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Address extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $fillable = [
        'party_id',
        'line1',
        'line2',
        'line2',
        'place',
        'district',
        'state',
        'country',
        'pincode',
        'latitude',
        'longitude',
        'account_id',
        'log_id',
    ];

    public function party()
    {
        return $this->belongsTo(Party::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
