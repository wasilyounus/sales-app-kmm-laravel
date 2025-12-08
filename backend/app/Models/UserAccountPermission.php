<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use App\Traits\HasLog;

class UserAccountPermission extends Model
{
    use HasLog;
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
