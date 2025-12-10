<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class Grn extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'purchase_id',
        'grn_no',
        'date',
        'vehicle_no',
        'invoice_no',
        'notes',
        'company_id',
        'log_id',
    ];

    protected $casts = [
        'date' => 'date',
    ];

    /**
     * Adjust stock when GRN is created (increase stock)
     */
    public function adjustStock()
    {
        if (!$this->location_id) {
            \Log::warning("GRN #{$this->id} has no location_id, skipping stock adjustment");
            return;
        }

        foreach ($this->items as $item) {
            $stock = Stock::firstOrCreate(
                [
                    'item_id' => $item->item_id,
                    'location_id' => $this->location_id,
                ],
                [
                    'quantity' => 0,
                    'company_id' => $this->company_id,
                ]
            );

            $stock->increment('quantity', $item->quantity);
        }
    }

    /**
     * Reverse stock adjustment (for deletion) (decrease stock back)
     */
    public function reverseStockAdjustment()
    {
        if (!$this->location_id) {
            return;
        }

        foreach ($this->items as $item) {
            $stock = Stock::where('item_id', $item->item_id)
                ->where('location_id', $this->location_id)
                ->first();
            if ($stock) {
                $stock->decrement('quantity', $item->quantity);
            }
        }
    }

    public function purchase()
    {
        return $this->belongsTo(Purchase::class);
    }

    public function items()
    {
        return $this->hasMany(GrnItem::class);
    }

    public function account()
    {
        return $this->belongsTo(Company::class);
    }

    /**
     * Generate next GRN number for account
     */
    public static function generateNumber($companyId)
    {
        $count = self::where('company_id', $companyId)->count() + 1;
        return 'GRN-' . str_pad($count, 4, '0', STR_PAD_LEFT);
    }
}
