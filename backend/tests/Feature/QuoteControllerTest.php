<?php

namespace Tests\Feature;

use App\Models\Quote;
use App\Models\QuoteItem;
use App\Models\User;
use App\Models\Account;
use App\Models\Party;
use App\Models\Item;
use App\Models\Tax;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class QuoteControllerTest extends TestCase
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
    public function it_returns_quotes_with_items_and_timestamps()
    {
        // Create quote with items
        $quote = Quote::create([
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        QuoteItem::create([
            'quote_id' => $quote->id,
            'item_id' => $this->item->id,
            'price' => 200.00,
            'qty' => 3,
            'tax_id' => $this->tax->id,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        // Act
        $response = $this->getJson("/api/quotes?account_id={$this->account->id}");

        // Assert
        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'id',
                        'party_id',
                        'date',
                        'account_id',
                        'log_id',
                        'created_at',
                        'updated_at',
                        'items' => [
                            '*' => [
                                'id',
                                'quote_id',
                                'item_id',
                                'price',
                                'qty',
                                'tax_id',  // ✅ Tax ID present
                                'created_at',
                                'updated_at',
                            ]
                        ]
                    ]
                ]
            ]);

        $this->assertNotNull($response->json('data.0.created_at'));
        $this->assertNotNull($response->json('data.0.items.0.tax_id'));
    }

    /** @test */
    public function it_creates_quote_with_items_having_tax_id()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 150.00,
                    'qty' => 2,
                    'tax_id' => $this->tax->id,  // ✅ Providing tax
                ]
            ]
        ];

        $response = $this->postJson('/api/quotes', $data);

        $response->assertStatus(201)
            ->assertJson(['success' => true]);

        $this->assertDatabaseHas('quote_items', [
            'item_id' => $this->item->id,
            'tax_id' => $this->tax->id,
        ]);
    }

    /** @test */
    public function quote_items_can_have_null_tax_id()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-06',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 100.00,
                    'qty' => 1,
                    'tax_id' => null,  // No tax
                ]
            ]
        ];

        $response = $this->postJson('/api/quotes', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('quote_items', [
            'item_id' => $this->item->id,
            'tax_id' => null,
        ]);
    }

    /** @test */
    public function it_creates_quote_with_quote_no()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-08',
            'quote_no' => 'QT-TEST-789',
            'account_id' => $this->account->id,
            'log_id' => 1,
            'items' => [
                [
                    'item_id' => $this->item->id,
                    'price' => 150.00,
                    'qty' => 3,
                ]
            ]
        ];

        $response = $this->postJson('/api/quotes', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('quotes', [
            'party_id' => $this->party->id,
            'quote_no' => 'QT-TEST-789',
        ]);
    }

    /** @test */
    public function it_auto_generates_quote_no_if_not_provided()
    {
        $data = [
            'party_id' => $this->party->id,
            'date' => '2025-12-09',
            // 'quote_no' => omitted
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

        $response = $this->postJson('/api/quotes', $data);

        $response->assertStatus(201);

        $this->assertDatabaseHas('quotes', [
            'party_id' => $this->party->id,
            'quote_no' => 'QT-0001',
        ]);
    }
}
