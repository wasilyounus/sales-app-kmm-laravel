<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class QuoteItem extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'quote_id',
        'item_id',
        'price',
        'qty',
        'tax_id',
        'account_id',
        'log_id',
    ];

    protected $casts = [
        'price' => 'decimal:2',
        'qty' => 'decimal:3',
    ];

    public function quote()
    {
        return $this->belongsTo(Quote::class);
    }

    public function item()
    {
        return $this->belongsTo(Item::class);
    }

    public function tax()
    {
        return $this->belongsTo(Tax::class);
    }

    public function account()
    {
        return $this->belongsTo(Account::class);
    }
}
