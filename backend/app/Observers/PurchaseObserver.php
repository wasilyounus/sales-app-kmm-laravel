<?php

namespace App\Observers;

use App\Models\Purchase;
use App\Models\Stock;

class PurchaseObserver
{
    /**
     * Handle the Purchase "created" event.
     * Increase stock when a purchase is created (ONLY if GRN is disabled).
     */
    public function created(Purchase $purchase): void
    {
        $company = $purchase->company ?? \App\Models\Company::find($purchase->company_id);

        // Only increase stock if GRN is disabled
        // If GRN is enabled, stock will be increased when GRN is created
        if (!$company || $company->enable_grns) {
            \Log::info("Purchase #{$purchase->id}: GRN enabled, skipping stock increment (will happen on GRN creation)");
            return;
        }

        if (!$purchase->location_id) {
            \Log::warning("Purchase #{$purchase->id} has no location_id, skipping stock increment");
            return;
        }

        // Increase stock for each item in the purchase
        foreach ($purchase->items as $purchaseItem) {
            $this->updateStock(
                $purchaseItem->item_id,
                $purchase->location_id,
                $purchase->company_id,
                +$purchaseItem->quantity // Positive = increase
            );
        }
    }

    /**
     * Handle the Purchase "deleted" event.
     * Decrease stock when a purchase is deleted (ONLY if GRN is disabled).
     */
    public function deleted(Purchase $purchase): void
    {
        $company = $purchase->company ?? \App\Models\Company::find($purchase->company_id);

        // Only reverse stock if GRN is disabled
        if (!$company || $company->enable_grns || !$purchase->location_id) {
            return;
        }

        // Reverse the stock increase
        foreach ($purchase->items as $purchaseItem) {
            $this->updateStock(
                $purchaseItem->item_id,
                $purchase->location_id,
                $purchase->company_id,
                -$purchaseItem->quantity // Negative = decrease
            );
        }
    }

    /**
     * Update stock for an item at a location.
     */
    private function updateStock(int $itemId, int $locationId, int $companyId, int $quantityChange): void
    {
        $stock = Stock::firstOrCreate(
            [
                'item_id' => $itemId,
                'location_id' => $locationId,
            ],
            [
                'quantity' => 0,
                'company_id' => $companyId,
            ]
        );

        $stock->increment('quantity', $quantityChange);
    }
}
