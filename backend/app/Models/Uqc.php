<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Uqc extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = false;

    protected $fillable = [
        'quantity',
        'type',
        'uqc',
        'active',
        'log_id',
    ];

    protected $casts = [
        'active' => 'boolean',
    ];
}
