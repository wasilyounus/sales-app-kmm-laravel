<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use App\Traits\HasLog;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Uqc extends Model
{
    use SoftDeletes, HasLog, HasFactory;

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
