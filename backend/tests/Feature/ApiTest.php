<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Account;
use App\Models\Item;
use App\Models\Party;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ApiTest extends TestCase
{
    use RefreshDatabase;

    protected $user;
    protected $token;
    protected $account;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create();
        $this->token = $this->user->createToken('test')->plainTextToken;

        $this->account = Account::create([
            'name' => 'Test Account',
            'name_formatted' => 'TEST ACCOUNT',
            'desc' => 'Test',
            'taxation_type' => 1,
            'log_id' => 1,
        ]);
    }

    public function test_can_create_item(): void
    {
        $response = $this->postJson('/api/items', [
            'name' => 'Test Product',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ], [
            'Authorization' => 'Bearer ' . $this->token,
        ]);

        $response->assertStatus(201)
            ->assertJson([
                'success' => true,
                'message' => 'Item created successfully',
            ]);

        $this->assertDatabaseHas('items', [
            'name' => 'Test Product',
        ]);
    }

    public function test_can_list_items(): void
    {
        Item::create([
            'name' => 'Product 1',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->getJson('/api/items?account_id=' . $this->account->id, [
            'Authorization' => 'Bearer ' . $this->token,
        ]);

        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => ['id', 'name', 'uqc'],
                ],
            ]);
    }

    public function test_can_create_party(): void
    {
        $response = $this->postJson('/api/parties', [
            'name' => 'Test Customer',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ], [
            'Authorization' => 'Bearer ' . $this->token,
        ]);

        $response->assertStatus(201);
        $this->assertDatabaseHas('parties', ['name' => 'Test Customer']);
    }

    public function test_can_create_quote_with_items(): void
    {
        $item = Item::create([
            'name' => 'Product',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $party = Party::create([
            'name' => 'Customer',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->postJson('/api/quotes', [
            'party_id' => $party->id,
            'date' => '2024-01-15',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $item->id,
                    'price' => 100,
                    'qty' => 5,
                ],
            ],
        ], [
            'Authorization' => 'Bearer ' . $this->token,
        ]);

        $response->assertStatus(201);
        $this->assertDatabaseHas('quotes', ['party_id' => $party->id]);
        $this->assertDatabaseHas('quote_items', ['item_id' => $item->id]);
    }

    public function test_requires_authentication(): void
    {
        $response = $this->getJson('/api/items');
        $response->assertStatus(401);
    }
}
