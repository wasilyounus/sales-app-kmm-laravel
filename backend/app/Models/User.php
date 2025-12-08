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
        'current_account_id',
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
     * Get all accounts assigned to this user
     */
    public function accounts()
    {
        return $this->belongsToMany(Account::class, 'user_account_permissions')
            ->withPivot('role')
            ->withTimestamps();
    }

    /**
     * Check if user has any accounts assigned
     */
    public function hasAccounts(): bool
    {
        return $this->accounts()->exists();
    }

    /**
     * Get accounts for selection (formatted)
     */
    public function getAccountsForSelection()
    {
        return $this->accounts()->get()->map(function ($account) {
            return [
                'id' => $account->id,
                'name' => $account->name,
                'name_formatted' => $account->name_formatted,
                'role' => $account->pivot->role ?? 'user',
                'visibility' => $account->visibility ?? 'private',
            ];
        });
    }
}
