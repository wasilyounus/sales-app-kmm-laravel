<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Sale;
use App\Models\SaleItem;
use App\Models\PriceList;
use App\Models\PriceListItem;

class BackendImplementationTest extends TestCase
{
    use RefreshDatabase;

    protected $user;
    protected $account;

    public function setUp(): void
    {
        parent::setUp();
        
        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();
        $this->user->accounts()->attach($this->account->id, ['role' => 'admin']);
        
        $this->actingAs($this->user);
    }

    public function test_can_create_and_list_transactions()
    {
        $response = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->postJson('/api/transactions', [
                'type' => 1, // Cash Received
                'amount' => 1000,
                'date' => '2025-12-05',
                'comment' => 'Test Payment'
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('amount', '1000.00');

        $listResponse = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->getJson('/api/transactions');
            
        $listResponse->assertStatus(200)
            ->assertJsonCount(1, 'data');
    }

    public function test_dynamic_price_logic_picks_latest()
    {
        // 1. Setup Data
        $party = Party::create(['name' => 'Test Party', 'account_id' => $this->account->id, 'type' => 'customer']);
        $item = Item::create(['name' => 'Test Item', 'uqc' => 1, 'account_id' => $this->account->id]);
        
        // 2. Create an Old Sale (Price 100)
        $sale = Sale::create(['date' => '2025-01-01', 'party_id' => $party->id, 'account_id' => $this->account->id, 'invoice_number' => 'INV-001', 'subtotal' => 100, 'total_amount' => 100]);
        SaleItem::create(['sale_id' => $sale->id, 'item_id' => $item->id, 'account_id' => $this->account->id, 'qty' => 1, 'price' => 100]);
        
        // 3. Create a Newer Price List (Price 120)
        $pl = PriceList::create(['name' => 'Standard PL', 'account_id' => $this->account->id]);
        $pl->created_at = '2025-02-01 12:00:00'; // Manually set older than verification date but newer than sale
        $pl->updated_at = '2025-02-01 12:00:00';
        $pl->save();
        
        PriceListItem::create(['price_list_id' => $pl->id, 'item_id' => $item->id, 'price' => 120]);
        
        // 4. Create an Even Newer Sale (Price 110)
        $sale2 = Sale::create(['date' => '2025-03-01', 'party_id' => $party->id, 'account_id' => $this->account->id, 'invoice_number' => 'INV-002', 'subtotal' => 110, 'total_amount' => 110]);
        SaleItem::create(['sale_id' => $sale2->id, 'item_id' => $item->id, 'account_id' => $this->account->id, 'qty' => 1, 'price' => 110]);

        // 5. Query Effective Price
        $response = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->getJson("/api/price/effective?item_id={$item->id}&party_id={$party->id}&type=SALE");
            
        // Should be 110 (Latest Sale)
        $response->assertStatus(200)
            ->assertJsonPath('price', '110.00');
            
        // 6. Update Price List to be newest
        $pl->touch(); // Updates updated_at to now
        
         // Query again
        $response2 = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->getJson("/api/price/effective?item_id={$item->id}&party_id={$party->id}&type=SALE");
            
        // Should be 120 (Price List is now newer)
        $response2->assertStatus(200)
             ->assertJsonPath('price', '120.00');
    }
}
