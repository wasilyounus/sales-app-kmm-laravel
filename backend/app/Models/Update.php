<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use App\Traits\HasLog;

class Update extends Model
{
    use HasLog;
    protected $fillable = [
        'version',
        'apk_url',
        'changelog',
        'force_update',
    ];

    protected $casts = [
        'force_update' => 'boolean',
    ];
}
