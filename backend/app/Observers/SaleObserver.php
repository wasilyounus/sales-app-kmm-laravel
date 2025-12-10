<?php

namespace App\Observers;

use App\Models\Sale;
use App\Models\Stock;

class SaleObserver
{
    /**
     * Handle the Sale "creating" event.
     * Validate stock availability before creating sale (if validation enabled).
     */
    public function creating(Sale $sale): void
    {
        $company = $sale->company ?? \App\Models\Company::find($sale->company_id);

        // Only validate if:
        // 1. DN is disabled (stock managed by Sale, not DN)
        // 2. Negative stock is NOT allowed (validation enabled)
        if (
            $company &&
            !$company->enable_delivery_notes &&
            !$company->allow_negative_stock &&
            $sale->location_id
        ) {
            $this->validateStock($sale);
        }
    }

    /**
     * Handle the Sale "created" event.
     * Decrease stock when a sale is created (ONLY if DN is disabled).
     */
    public function created(Sale $sale): void
    {
        $company = $sale->company ?? \App\Models\Company::find($sale->company_id);

        // Only decrease stock if DN is disabled
        // If DN is enabled, stock will be decreased when DN is created
        if (!$company || $company->enable_delivery_notes) {
            \Log::info("Sale #{$sale->id}: DN enabled, skipping stock decrement (will happen on DN creation)");
            return;
        }

        if (!$sale->location_id) {
            \Log::warning("Sale #{$sale->id} has no location_id, skipping stock decrement");
            return;
        }

        // Decrease stock for each item in the sale
        foreach ($sale->items as $saleItem) {
            $this->updateStock(
                $saleItem->item_id,
                $sale->location_id,
                $sale->company_id,
                -$saleItem->quantity // Negative = decrease
            );
        }
    }

    /**
     * Handle the Sale "deleted" event.
     * Restore stock when a sale is deleted (ONLY if DN is disabled).
     */
    public function deleted(Sale $sale): void
    {
        $company = $sale->company ?? \App\Models\Company::find($sale->company_id);

        // Only restore stock if DN is disabled
        if (!$company || $company->enable_delivery_notes || !$sale->location_id) {
            return;
        }

        // Restore stock (reverse the sale)
        foreach ($sale->items as $saleItem) {
            $this->updateStock(
                $saleItem->item_id,
                $sale->location_id,
                $sale->company_id,
                +$saleItem->quantity // Positive = increase
            );
        }
    }

    /**
     * Validate that sufficient stock exists for all items.
     * 
     * @throws InsufficientStockException
     */
    private function validateStock(Sale $sale): void
    {
        foreach ($sale->items as $saleItem) {
            $stock = Stock::where('item_id', $saleItem->item_id)
                ->where('location_id', $sale->location_id)
                ->first();

            $availableQty = $stock ? $stock->quantity : 0;

            if ($availableQty < $saleItem->quantity) {
                throw new \Exception(
                    "Insufficient stock for item #{$saleItem->item_id}. " .
                    "Available: {$availableQty}, Required: {$saleItem->quantity}"
                );
            }
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
