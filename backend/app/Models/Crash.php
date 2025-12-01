<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Crash extends Model
{
    protected $fillable = [
        'app_version',
        'stack_trace',
        'device_info',
        'user_id',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
