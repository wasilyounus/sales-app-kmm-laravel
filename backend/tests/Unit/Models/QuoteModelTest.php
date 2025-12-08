<?php

namespace Tests\Unit\Models;

use App\Models\Quote;
use App\Models\QuoteItem;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class QuoteModelTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function quote_model_has_timestamps_enabled()
    {
        // The model should NOT have $timestamps = false
        $quote = new Quote();

        $this->assertTrue($quote->usesTimestamps());
    }

    /** @test */
    public function quote_item_model_has_tax_id_in_fillable()
    {
        $quoteItem = new QuoteItem();

        $this->assertContains('tax_id', $quoteItem->getFillable());
    }

    /** @test */
    public function quote_item_model_has_timestamps_enabled()
    {
        $quoteItem = new QuoteItem();

        $this->assertTrue($quoteItem->usesTimestamps());
    }




}
