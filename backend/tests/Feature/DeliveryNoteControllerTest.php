<?php

namespace Tests\Feature;

use App\Models\DeliveryNote;
use App\Models\DeliveryNoteItem;
use App\Models\Sale;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Stock;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class DeliveryNoteControllerTest extends TestCase
{
    use RefreshDatabase;

    private User $user;
    private Account $account;
    private Party $party;
    private Item $item;
    private Sale $sale;

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

        $this->sale = Sale::factory()->create([
            'account_id' => $this->account->id,
            'party_id' => $this->party->id
        ]);

        // Authenticate for protected routes
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_returns_delivery_notes_for_account()
    {
        // Arrange
        $deliveryNote = DeliveryNote::create([
            'sale_id' => $this->sale->id,
            'dn_number' => 'DN-001',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->getJson("/api/delivery-notes?account_id={$this->account->id}");

        // Assert
        $response->assertStatus(200)
            ->assertJsonStructure([
                'data' => [
                    '*' => [
                        'id',
                        'sale_id',
                        'dn_number',
                        'date',
                        'account_id',
                        'created_at',
                        'updated_at',
                    ]
                ]
            ]);
    }

    /** @test */
    public function it_creates_delivery_note_and_decreases_stock()
    {
        // Arrange
        $data = [
            'account_id' => $this->account->id,
            'sale_id' => $this->sale->id,
            'date' => '2025-12-08',
            'vehicle_no' => 'KA-01-1234',
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'quantity' => 10,
                ]
            ]
        ];

        // Act
        $response = $this->postJson('/api/delivery-notes', $data);

        // Assert
        $response->assertStatus(201);
        
        $this->assertDatabaseHas('delivery_notes', [
            'sale_id' => $this->sale->id,
            'account_id' => $this->account->id,
            'vehicle_no' => 'KA-01-1234'
        ]);

        // Check stock decreased (100 - 10 = 90)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'account_id' => $this->account->id,
            'count' => 90
        ]);
    }

    /** @test */
    public function it_updates_delivery_note_and_adjusts_stock_correctly()
    {
        // Arrange - Create logic creates DN and decreases stock
        $dn = DeliveryNote::create([
            'sale_id' => $this->sale->id,
            'dn_number' => 'DN-002',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);
        
        DeliveryNoteItem::create([
            'delivery_note_id' => $dn->id,
            'item_id' => $this->item->id,
            'quantity' => 10
        ]);
        
        // Manually decrease stock to simulate real creation flow
        $dn->adjustStockDecrease(); 
        
        // Verify initial stock (100 - 10 = 90)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 90
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

        $response = $this->putJson("/api/delivery-notes/{$dn->id}", $updateData);

        // Assert
        $response->assertStatus(200);

        // Check stock: 
        // 1. Reverse decrease (90 + 10 = 100)
        // 2. Apply new decrease (100 - 20 = 80)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 80
        ]);
    }

    /** @test */
    public function it_deletes_delivery_note_and_reverses_stock()
    {
        // Arrange
        $dn = DeliveryNote::create([
            'sale_id' => $this->sale->id,
            'dn_number' => 'DN-003',
            'date' => '2025-12-08',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);
        
        DeliveryNoteItem::create([
            'delivery_note_id' => $dn->id,
            'item_id' => $this->item->id,
            'quantity' => 10
        ]);
        
        $dn->adjustStockDecrease(); // Stock becomes 90

        // Act
        $response = $this->deleteJson("/api/delivery-notes/{$dn->id}");

        // Assert
        $response->assertStatus(200);
        
        // Stock should be reversed (90 + 10 = 100)
        $this->assertDatabaseHas('stocks', [
            'item_id' => $this->item->id,
            'count' => 100
        ]);

        $this->assertSoftDeleted('delivery_notes', ['id' => $dn->id]);
    }
}
