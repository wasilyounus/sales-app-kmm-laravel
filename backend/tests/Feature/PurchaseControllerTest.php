<?php

namespace Tests\Feature;

use App\Models\Purchase;
use App\Models\PurchaseItem;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Tax;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class PurchaseControllerTest extends TestCase
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
    public function it_returns_purchases_with_timestamps()
    {
        $purchase = Purchase::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-07',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        PurchaseItem::create([
            'purchase_id' => $purchase->id,
            'item_id' => $this->item->id,
            'price' => 80.00,
            'qty' => 10,
            'tax_id' => $this->tax->id,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->getJson("/api/purchases?account_id={$this->account->id}");

        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'id',
                        'created_at',
                        'updated_at',
                        'items' => [
                            '*' => ['tax_id', 'created_at', 'updated_at']
                        ]
                    ]
                ]
            ]);

        $this->assertNotNull($response->json('data.0.created_at'));
        $this->assertNotNull($response->json('data.0.items.0.tax_id'));
    }

    /** @test */
    public function it_creates_purchase_with_tax_on_items()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-07',
            'invoice_no' => 'INV-TEST-123',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 75.00,
                    'qty' => 20,
                    'tax_id' => $this->tax->id,
                ]
            ]
        ];

        $response = $this->postJson('/api/purchases', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('purchase_items', [
            'item_id' => $this->item->id,
            'tax_id' => $this->tax->id,
        ]);

        $this->assertDatabaseHas('purchases', [
            'party_id' => $this->party->id,
            'invoice_no' => 'INV-TEST-123',
        ]);
    }

    /** @test */
    public function it_updates_purchase_and_modifies_timestamp()
    {
        $purchase = Purchase::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-07',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $originalUpdatedAt = $purchase->updated_at;
        sleep(1);

        $response = $this->putJson("/api/purchases/{$purchase->id}", [
            'date' => '2025-12-08',
            'log_id' => 2,
        ]);

        $response->assertStatus(200);

        $purchase->refresh();
        $this->assertNotEquals($originalUpdatedAt, $purchase->updated_at);
    }

    /** @test */
    public function it_soft_deletes_purchase()
    {
        $purchase = Purchase::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-07',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->deleteJson("/api/purchases/{$purchase->id}");

        $response->assertStatus(200);
        $this->assertSoftDeleted('purchases', ['id' => $purchase->id]);
    }
}
