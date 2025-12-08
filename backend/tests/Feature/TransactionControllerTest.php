<?php

namespace Tests\Feature;

use App\Models\Transaction;
use App\Models\User;
use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class TransactionControllerTest extends TestCase
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
    public function it_returns_transactions_with_timestamps()
    {
        Transaction::create([
            'credit_code' => 1,
            'debit_code' => 2,
            'type' => Transaction::TYPE_CASH_RECEIVED,
            'amount' => 1000.00,
            'date' => '2025-12-07',
            'comment' => 'Test payment',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->getJson("/api/transactions");

        $response->assertStatus(200)
            ->assertJsonStructure([
                'current_page',
                'data' => [
                    '*' => [
                        'id',
                        'amount',
                        'type',
                        'date',
                        'created_at',
                        'updated_at',
                    ]
                ]
            ]);

        $this->assertNotNull($response->json('data.0.created_at'));
    }

    /** @test */
    public function it_creates_transaction_with_timestamps()
    {
        $data = [
            'date' => '2025-12-07',
            'amount' => 500.00,
            'type' => 1, // Use integer type (e.g. CASH_RECEIVED)
            'method' => 'cash',
            'party_id' => \App\Models\Party::factory()->create(['account_id' => $this->account->id])->id,
            'comment' => 'Payment received',
            'log_id' => 1,
        ];

        $response = $this->withHeaders(['X-Account-ID' => $this->account->id])
            ->postJson('/api/transactions', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('transactions', [
            'amount' => 500.00,
            'account_id' => $this->account->id,
        ]);

        $transaction = Transaction::latest()->first();
        $this->assertNotNull($transaction->created_at);
        $this->assertNotNull($transaction->updated_at);
    }
}
