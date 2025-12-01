<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class UserAccountPermission extends Model
{
    protected $fillable = [
        'user_id',
        'account_id',
        'role',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
