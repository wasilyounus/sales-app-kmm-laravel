<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class GrnItem extends Model
{
    use HasFactory;

    protected $fillable = [
        'grn_id',
        'item_id',
        'quantity',
    ];

    protected $casts = [
        'quantity' => 'decimal:3',
    ];

    public function grn()
    {
        return $this->belongsTo(Grn::class);
    }

    public function item()
    {
        return $this->belongsTo(Item::class);
    }
}
