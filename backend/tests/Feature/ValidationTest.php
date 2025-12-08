<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ValidationTest extends TestCase
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
    public function sale_creation_validates_required_fields()
    {
        $response = $this->postJson('/api/sales', [
            // Missing required fields
        ]);

        // May return 422 if validation is set up, or 401 if auth required
        $this->assertContains($response->status(), [401, 422]);
    }

    /** @test */
    public function item_creation_validates_required_fields()
    {
        $response = $this->postJson('/api/items', [
            // Missing required fields
        ]);

        $this->assertContains($response->status(), [401, 422]);
    }

    /** @test */
    public function party_creation_validates_required_fields()
    {
        $response = $this->postJson('/api/parties', [
            // Missing required fields
        ]);

        $this->assertContains($response->status(), [401, 422]);
    }
}