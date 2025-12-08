<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Account extends Model
{
    use SoftDeletes, \App\Traits\HasLog, \Illuminate\Database\Eloquent\Factories\HasFactory;

    protected $fillable = [
        'name',
        'name_formatted',
        'desc',
        'taxation_type',
        'address',
        'call',
        'whatsapp',
        'footer_content',
        'signature',
        'log_id',
        'financial_year_start',
        'country',
        'state',
        'tax_number',
        'default_tax_id',
        'tax_application_level',
        'visibility',
    ];

    public function defaultTax()
    {
        return $this->belongsTo(Tax::class, 'default_tax_id');
    }

    protected $casts = [
        'signature' => 'boolean',
        'tax_application_level' => 'string', // 'account', 'bill', or 'item'
    ];

    public function users()
    {
        return $this->belongsToMany(User::class, 'user_account_permissions')
            ->withPivot('role')
            ->withTimestamps();
    }

    public function parties()
    {
        return $this->hasMany(Party::class);
    }

    public function items()
    {
        return $this->hasMany(Item::class);
    }
}
