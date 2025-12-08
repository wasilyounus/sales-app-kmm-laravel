<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Transport extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = true;

    protected $fillable = [
        'name',
        'tax_number',
        'contact',
        'active',
        'account_id',
        'log_id',
    ];

    protected $casts = [
        'active' => 'boolean',
    ];

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
