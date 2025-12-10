<?php

namespace App\Models;

use App\Traits\HasLog;
// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasApiTokens, HasFactory, Notifiable, HasLog;

    /**
     * The attributes that are mass assignable.
     *
     * @var list<string>
     */
    protected $fillable = [
        'name',
        'email',
        'password',
        'current_company_id',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var list<string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    /**
     * Get all companies assigned to this user
     */
    public function companies()
    {
        return $this->belongsToMany(Company::class, 'user_company_permissions')
            ->withPivot('role')
            ->withTimestamps();
    }

    /**
     * Check if user has any companies assigned
     */
    public function hasCompanies(): bool
    {
        return $this->companies()->exists();
    }

    /**
     * Get companies for selection (formatted)
     */
    public function getCompaniesForSelection()
    {
        return $this->companies()->get()->map(function ($company) {
            return [
                'id' => $company->id,
                'name' => $company->name,
                'name_formatted' => $company->name_formatted,
                'role' => $company->pivot->role ?? 'user',
                'visibility' => $company->visibility ?? 'private',
            ];
        });
    }
}
