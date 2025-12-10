<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class Quote extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'party_id',
        'tax_id',
        'date',
        'quote_no',
        'company_id',
        'log_id',
    ];

    public function tax()
    {
        return $this->belongsTo(Tax::class);
    }

    protected $casts = [
        'date' => 'date',
    ];

    public function party()
    {
        return $this->belongsTo(Party::class);
    }

    public function account()
    {
        return $this->belongsTo(Company::class);
    }

    public function items()
    {
        return $this->hasMany(QuoteItem::class);
    }

    public static function generateNumber($companyId)
    {
        $count = self::where('company_id', $companyId)->count() + 1;
        return 'QT-' . str_pad($count, 4, '0', STR_PAD_LEFT);
    }
}
