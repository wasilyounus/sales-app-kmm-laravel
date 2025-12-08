<?php

namespace Tests\Unit\Models;

use App\Models\Order;
use App\Models\OrderItem;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class OrderModelTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function order_model_has_timestamps_enabled()
    {
        $order = new Order();
        $this->assertTrue($order->usesTimestamps());
    }

    /** @test */
    public function order_item_has_tax_id_in_fillable()
    {
        $orderItem = new OrderItem();
        $this->assertContains('tax_id', $orderItem->getFillable());
    }

    /** @test */
    public function order_item_has_timestamps_enabled()
    {
        $orderItem = new OrderItem();
        $this->assertTrue($orderItem->usesTimestamps());
    }


}
