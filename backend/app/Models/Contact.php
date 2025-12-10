<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class Contact extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'company_id',
        'party_id',
        'name',
        'phone',
        'email',
        'designation',
        'is_primary',
        'log_id',
    ];

    protected $casts = [
        'is_primary' => 'boolean',
    ];

    public function company()
    {
        return $this->belongsTo(Company::class);
    }

    public function party()
    {
        return $this->belongsTo(Party::class);
    }
}
