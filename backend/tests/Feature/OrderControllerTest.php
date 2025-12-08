<?php

namespace Tests\Feature;

use App\Models\Order;
use App\Models\OrderItem;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Tax;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class OrderControllerTest extends TestCase
{
    use RefreshDatabase;

    private User $user;
    private Account $account;
    private Party $party;
    private Item $item;
    private Tax $tax;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();
        $this->party = Party::factory()->create(['account_id' => $this->account->id]);
        $this->item = Item::factory()->create(['account_id' => $this->account->id]);
        $this->tax = Tax::factory()->create();

        // Authenticate for protected routes
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_returns_orders_with_timestamps_and_tax_ids()
    {
        $order = Order::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        OrderItem::create([
            'order_id' => $order->id,
            'item_id' => $this->item->id,
            'price' => 300.00,
            'qty' => 5,
            'tax_id' => $this->tax->id,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->getJson("/api/orders?account_id={$this->account->id}");

        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'created_at',
                        'updated_at',
                        'items' => [
                            '*' => ['tax_id', 'created_at', 'updated_at']
                        ]
                    ]
                ]
            ]);
    }

    /** @test */
    public function it_creates_order_with_tax_on_items()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 250.00,
                    'qty' => 10,
                    'tax_id' => $this->tax->id,
                ]
            ]
        ];

        $response = $this->postJson('/api/orders', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('order_items', [
            'item_id' => $this->item->id,
            'tax_id' => $this->tax->id,
        ]);
    }
}
