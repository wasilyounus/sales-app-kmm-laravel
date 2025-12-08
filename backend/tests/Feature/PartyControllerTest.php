<?php

namespace Tests\Feature;

use App\Models\Party;
use App\Models\Address;
use App\Models\User;
use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class PartyControllerTest extends TestCase
{
    use RefreshDatabase;

    private User $user;
    private Account $account;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create();
        $this->account = Account::factory()->create();

        // Authenticate for protected routes
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_returns_parties_with_timestamps()
    {
        Party::create([
            'name' => 'Test Customer',
            'phone' => '1234567890',
            'type' => 'customer',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->getJson("/api/parties?account_id={$this->account->id}");

        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'id',
                        'name',
                        'created_at',
                        'updated_at',
                    ]
                ]
            ]);
    }

    /** @test */
    public function it_creates_party_with_address()
    {
        $user = User::factory()->create();
        $this->actingAs($user);
        $data = [
            'name' => 'New Customer',
            'phone' => '9876543210',
            'type' => 'customer',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'addresses' => [
                [
                    'line1' => '123 Main St',
                    'place' => 'Mumbai',
                    'state' => 'Maharashtra',
                    'country' => 'India',
                    'pincode' => '400001',
                ]
            ]
        ];

        $response = $this->postJson('/api/parties', $data);

        $response->assertStatus(201);

        $party = Party::latest()->first();
        $this->assertNotNull($party->created_at);
        $this->assertEquals('New Customer', $party->name);
    }

    /** @test */
    public function it_updates_party_timestamp()
    {
        $party = Party::create([
            'name' => 'Original Party',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $originalUpdatedAt = $party->updated_at;
        sleep(1);

        $response = $this->putJson("/api/parties/{$party->id}", [
            'name' => 'Updated Party',
            'log_id' => 2,
        ]);

        $response->assertStatus(200);

        $party->refresh();
        $this->assertNotEquals($originalUpdatedAt, $party->updated_at);
    }
}
