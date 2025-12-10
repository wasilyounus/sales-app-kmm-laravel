<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class Purchase extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'party_id',
        'location_id',
        'tax_id',
        'date',
        'invoice_no',
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

    public function transport()
    {
        return $this->hasOne(Transport::class);
    }

    public function journalEntry()
    {
        return $this->morphOne(JournalEntry::class, 'source', 'source_type', 'source_id');
    }

    // ========== Computed Attributes ==========

    public function getSubtotalAttribute()
    {
        return $this->items()->sum(\Illuminate\Support\Facades\DB::raw('quantity * price'));
    }

    public function getTaxAmountAttribute()
    {
        if (!$this->tax_id) {
            return 0;
        }
        $tax = $this->tax;
        return $this->subtotal * ($tax->rate / 100);
    }

    public function getTotalAttribute()
    {
        return $this->subtotal + $this->tax_amount;
    }

    // ========== Events ==========

    protected static function boot()
    {
        parent::boot();

        static::created(function ($purchase) {
            // Auto-create journal entry for this purchase
            try {
                app(\App\Services\JournalEntryService::class)->createFromPurchase($purchase);
            } catch (\Exception $e) {
                \Log::error("Failed to create journal entry for purchase #{$purchase->id}: " . $e->getMessage());
            }
        });
    }

    public function items()
    {
        return $this->hasMany(PurchaseItem::class);
    }
}
