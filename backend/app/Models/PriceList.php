<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class PriceList extends Model
{
    use SoftDeletes;

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
