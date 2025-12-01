<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Update extends Model
{
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
