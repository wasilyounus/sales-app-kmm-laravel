<?php

namespace Tests\Feature;

use App\Models\Sale;
use App\Models\SaleItem;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class SaleControllerTest extends TestCase
{
    use RefreshDatabase;

    private User $user;
    private Account $account;
    private Party $party;
    private Item $item;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();
        $this->party = Party::factory()->create(['account_id' => $this->account->id]);
        $this->item = Item::factory()->create(['account_id' => $this->account->id]);

        // Authenticate for protected routes
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_returns_sales_with_timestamps()
    {
        // Create a sale
        $sale = Sale::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'invoice_no' => 'INV-001',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->getJson("/api/sales?account_id={$this->account->id}");

        // Assert
        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'id',
                        'party_id',
                        'date',
                        'invoice_no',
                        'account_id',
                        'log_id',
                        'created_at',  // ✅ Should have timestamp
                        'updated_at',  // ✅ Should have timestamp
                        'deleted_at',
                    ]
                ]
            ]);

        $this->assertNotNull($response->json('data.0.created_at'));
        $this->assertNotNull($response->json('data.0.updated_at'));
    }

    /** @test */
    public function it_creates_sale_with_items_and_timestamps()
    {
        // Arrange
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'invoice_no' => 'INV-002',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 100.00,
                    'qty' => 2,
                    'tax_id' => null,
                ]
            ]
        ];

        // Act
        $response = $this->postJson('/api/sales', $data);

        // Assert
        $response->assertStatus(201)
            ->assertJson([
                'success' => true,
                'message' => 'Sale created successfully',
            ])
            ->assertJsonStructure([
                'data' => [
                    'id',
                    'created_at',
                    'updated_at',
                    'items' => [
                        '*' => [
                            'id',
                            'tax_id',
                            'created_at',
                            'updated_at',
                        ]
                    ]
                ]
            ]);

        $this->assertDatabaseHas('sales', [
            'party_id' => $this->party->id,
            'invoice_no' => 'INV-002',
        ]);

        $this->assertDatabaseHas('sale_items', [
            'item_id' => $this->item->id,
            'price' => 100.00,
        ]);
    }

    /** @test */
    public function it_updates_sale_and_modifies_updated_at_timestamp()
    {
        // Arrange - Create initial sale
        $sale = Sale::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'invoice_no' => 'INV-003',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $originalUpdatedAt = $sale->updated_at;

        // Wait a moment to ensure timestamp will be different
        sleep(1);

        // Act - Update the sale
        $response = $this->putJson("/api/sales/{$sale->id}", [
            'date' => '2025-12-07',  // Changed date
            'log_id' => 2,
        ]);

        // Assert
        $response->assertStatus(200);

        $sale->refresh();
        $this->assertEquals('2025-12-07', $sale->date->format('Y-m-d'));
        $this->assertNotEquals($originalUpdatedAt, $sale->updated_at);  // ✅ Timestamp changed
    }

    /** @test */
    public function it_soft_deletes_sale_and_sets_deleted_at()
    {
        // Arrange
        $sale = Sale::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->deleteJson("/api/sales/{$sale->id}");

        // Assert
        $response->assertStatus(200);

        $this->assertSoftDeleted('sales', ['id' => $sale->id]);

        $sale->refresh();
        $this->assertNotNull($sale->deleted_at);  // ✅ Soft delete timestamp set
    }

    /** @test */
    public function sale_items_have_tax_id_field()
    {
        // Arrange
        $sale = Sale::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $saleItem = SaleItem::create([
            'sale_id' => $sale->id,
            'item_id' => $this->item->id,
            'price' => 150.00,
            'qty' => 3,
            'tax_id' => \App\Models\Tax::factory()->create()->id,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->getJson("/api/sales/{$sale->id}");

        // Assert
        $response->assertStatus(200)
            ->assertJsonPath('data.items.0.tax_id', $saleItem->tax_id);
    }

    /** @test */
    public function it_auto_generates_invoice_no_if_not_provided()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-09',
            // 'invoice_no' => omitted
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 100.00,
                    'qty' => 1,
                ]
            ]
        ];

        $response = $this->postJson('/api/sales', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('sales', [
            'party_id' => $this->party->id,
            'invoice_no' => 'INV-0001',
        ]);
    }
}
