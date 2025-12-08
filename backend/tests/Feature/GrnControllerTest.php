<?php

namespace Tests\Feature;

use App\Models\Grn;
use App\Models\GrnItem;
use App\Models\Purchase;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Stock;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class GrnControllerTest extends TestCase
{
    use RefreshDatabase;

    private User $user;
    private Account $account;
    private Party $party;
    private Item $item;
    private Purchase $purchase;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();
        $this->party = Party::factory()->create(['account_id' => $this->account->id]);
        $this->item = Item::factory()->create(['account_id' => $this->account->id]);

        // Initial stock
        Stock::create([
            'item_id' => $this->item->id,
            'account_id' => $this->account->id,
            'count' => 100,
            'log_id' => 1
        ]);

        $this->purchase = Purchase::factory()->create([
            'account_id' => $this->account->id,
            'party_id' => $this->party->id
        ]);

        // Authenticate for protected routes
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_returns_grns_for_account()
    {
        // Arrange
        $grn = Grn::create([
            'purchase_id' => $this->purchase->id,
            'grn_number' => 'GRN-001',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->getJson("/api/grns?account_id={$this->account->id}");

        // Assert
        $response->assertStatus(200)
            ->assertJsonStructure([
                'data' => [
                    '*' => [
                        'id',
                        'purchase_id',
                        'grn_number',
                        'date',
                        'account_id',
                        'created_at',
                        'updated_at',
                    ]
                ]
            ]);
    }

    /** @test */
    public function it_creates_grn_and_increases_stock()
    {
        // Arrange
        $data = [
            'account_id' => $this->account->id,
            'purchase_id' => $this->purchase->id,
            'date' => '2025-12-08',
            'vehicle_no' => 'TN-01-5678',
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'quantity' => 50,
                ]
            ]
        ];

        // Act
        $response = $this->postJson('/api/grns', $data);

        // Assert
        $response->assertStatus(201);

        $this->assertDatabaseHas('grns', [
            'purchase_id' => $this->purchase->id,
            'account_id' => $this->account->id,
            'vehicle_no' => 'TN-01-5678'
        ]);

        // Check stock increased (100 + 50 = 150)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'account_id' => $this->account->id,
            'count' => 150
        ]);
    }

    /** @test */
    public function it_updates_grn_and_adjusts_stock_correctly()
    {
        // Arrange - Create logic creates GRN and increases stock
        $grn = Grn::create([
            'purchase_id' => $this->purchase->id,
            'grn_number' => 'GRN-002',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        GrnItem::create([
            'grn_id' => $grn->id,
            'item_id' => $this->item->id,
            'quantity' => 50
        ]);

        // Manually increase stock to simulate real creation flow
        $grn->adjustStockIncrease();

        // Verify initial stock (100 + 50 = 150)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 150
        ]);

        // Act - Update quantity to 20
        $updateData = [
            'date' => '2025-12-08',
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'quantity' => 20
                ]
            ]
        ];

        $response = $this->putJson("/api/grns/{$grn->id}", $updateData);

        // Assert
        $response->assertStatus(200);

        // Check stock: 
        // 1. Reverse increase (150 - 50 = 100)
        // 2. Apply new increase (100 + 20 = 120)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 120
        ]);
    }

    /** @test */
    public function it_deletes_grn_and_reverses_stock()
    {
        // Arrange
        $grn = Grn::create([
            'purchase_id' => $this->purchase->id,
            'grn_number' => 'GRN-003',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        GrnItem::create([
            'grn_id' => $grn->id,
            'item_id' => $this->item->id,
            'quantity' => 50
        ]);

        $grn->adjustStockIncrease(); // Stock becomes 150

        // Act
        $response = $this->deleteJson("/api/grns/{$grn->id}");

        // Assert
        $response->assertStatus(200);

        // Stock should be reversed (150 - 50 = 100)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 100
        ]);

        $this->assertSoftDeleted('grns', ['id' => $grn->id]);
    }
}
