<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use App\Traits\HasLog;

class PriceListItem extends Model
{
    use HasLog;
    protected $fillable = ['price_list_id', 'item_id', 'price'];

    protected $casts = [
        'price' => 'decimal:2',
    ];

    public function priceList()
    {
        return $this->belongsTo(PriceList::class);
    }

    public function item()
    {
        return $this->belongsTo(Item::class);
    }
}
