<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use App\Traits\HasLog;

class PriceList extends Model
{
    use SoftDeletes, HasLog;

    protected $fillable = ['name', 'account_id'];

    public function items()
    {
        return $this->hasMany(PriceListItem::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
