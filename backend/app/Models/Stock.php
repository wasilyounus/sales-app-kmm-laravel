<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class Stock extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'item_id',
        'count',
        'account_id',
        'log_id',
    ];

    protected $casts = [
        'count' => 'decimal:3',
    ];

    public function item()
    {
        return $this->belongsTo(Item::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
