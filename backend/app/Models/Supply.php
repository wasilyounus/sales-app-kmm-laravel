<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Supply extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    public $timestamps = true;

    protected $fillable = [
        'sale_id',
        'vehicle_no',
        'date',
        'place',
        'transport_gst',
        'company_id',
        'log_id',
    ];

    protected $casts = [
        'date' => 'date',
    ];

    public function sale()
    {
        return $this->belongsTo(Sale::class);
    }

    public function account()
    {
        return $this->belongsTo(Company::class);
    }
}
