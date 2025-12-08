<?php

namespace Tests\Feature;

use App\Models\Item;
use App\Models\User;
use App\Models\Account;
use App\Models\Tax;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ItemControllerTest extends TestCase
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
    public function it_returns_items_with_timestamps()
    {
        $tax = Tax::factory()->create();
        Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id,
        ]);

        $response = $this->getJson("/api/items?account_id={$this->account->id}");

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
    public function it_creates_item_with_timestamps()
    {
        $data = [
            'name' => 'New Item',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ];

        $response = $this->postJson('/api/items', $data);

        $response->assertStatus(201);

        $item = Item::latest()->first();
        $this->assertNotNull($item->created_at);
        $this->assertNotNull($item->updated_at);
    }

    /** @test */
    public function it_updates_item_and_changes_timestamp()
    {
        $item = Item::create([
            'name' => 'Original Name',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $originalUpdatedAt = $item->updated_at;
        sleep(1);

        $response = $this->putJson("/api/items/{$item->id}", [
            'name' => 'Updated Name',
            'log_id' => 2,
        ]);

        $response->assertStatus(200);

        $item->refresh();
        $this->assertEquals('Updated Name', $item->name);
        $this->assertNotEquals($originalUpdatedAt, $item->updated_at);
    }

    /** @test */
    public function it_deletes_item_with_soft_delete()
    {
        $item = Item::create([
            'name' => 'To Delete',
            'uqc' => 1,
            'account_id' => $this->account->id,
            'log_id' => 1,
        ]);

        $response = $this->deleteJson("/api/items/{$item->id}");

        $response->assertStatus(200);
        $this->assertSoftDeleted('items', ['id' => $item->id]);
    }
}
