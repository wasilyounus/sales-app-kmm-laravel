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
        'grn_number',
        'date',
        'vehicle_no',
        'invoice_no',
        'notes',
        'account_id',
        'log_id',
    ];

    protected $casts = [
        'date' => 'date',
    ];

    /**
     * Increase stock for all items in this GRN
     */
    public function adjustStockIncrease()
    {
        foreach ($this->items as $item) {
            $stock = Stock::firstOrCreate(
                ['item_id' => $item->item_id, 'account_id' => $this->account_id],
                ['count' => 0, 'log_id' => $this->log_id]
            );
            $stock->increment('count', $item->quantity);
        }
    }

    /**
     * Reverse stock adjustment (for deletion)
     */
    public function reverseStockAdjustment()
    {
        foreach ($this->items as $item) {
            $stock = Stock::where('item_id', $item->item_id)
                ->where('account_id', $this->account_id)
                ->first();
            if ($stock) {
                $stock->decrement('count', $item->quantity);
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
        return $this->belongsTo(Account::class);
    }

    /**
     * Generate next GRN number for account
     */
    public static function generateNumber($accountId)
    {
        $count = self::where('account_id', $accountId)->count() + 1;
        return 'GRN-' . str_pad($count, 4, '0', STR_PAD_LEFT);
    }
}
