<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Company extends Model
{
    use SoftDeletes, \App\Traits\HasLog;

    protected $table = 'companies';

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
        'enable_delivery_notes',
        'enable_grns',
        'allow_negative_stock',
        'dark_mode',
    ];

    public function defaultTax()
    {
        return $this->belongsTo(Tax::class, 'default_tax_id');
    }

    protected $casts = [
        'signature' => 'boolean',
        'tax_application_level' => 'string', // 'account', 'bill', or 'item'
        'enable_delivery_notes' => 'boolean',
        'enable_grns' => 'boolean',
        'allow_negative_stock' => 'boolean',
        'dark_mode' => 'boolean',
    ];

    public function users()
    {
        return $this->belongsToMany(User::class, 'user_company_permissions')
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

    public function locations()
    {
        return $this->hasMany(Location::class);
    }

    public function defaultLocation()
    {
        return $this->hasOne(Location::class)->where('is_default', true);
    }

    public function contacts()
    {
        return $this->hasMany(Contact::class);
    }
}
