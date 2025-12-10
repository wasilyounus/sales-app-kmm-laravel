<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Traits\HasLog;

class DeliveryNote extends Model
{
    use SoftDeletes, HasLog, HasFactory;

    protected $fillable = [
        'sale_id',
        'dn_no',
        'date',
        'vehicle_no',
        'lr_no',
        'notes',
        'company_id',
        'log_id',
    ];

    protected $casts = [
        'date' => 'date',
    ];

    /**
     * Adjust stock when delivery note is created (decrease stock)
     */
    public function adjustStock()
    {
        if (!$this->location_id) {
            \Log::warning("DeliveryNote #{$this->id} has no location_id, skipping stock adjustment");
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

            $stock->decrement('quantity', $item->quantity);
        }
    }

    /**
     * Reverse stock adjustment (for deletion) (increase stock back)
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
                $stock->increment('quantity', $item->quantity);
            }
        }
    }

    public function sale()
    {
        return $this->belongsTo(Sale::class);
    }

    public function items()
    {
        return $this->hasMany(DeliveryNoteItem::class);
    }

    public function account()
    {
        return $this->belongsTo(Company::class);
    }

    /**
     * Generate next DN number for account
     */
    public static function generateNumber($companyId)
    {
        $count = self::where('company_id', $companyId)->count() + 1;
        return 'DN-' . str_pad($count, 4, '0', STR_PAD_LEFT);
    }
}
