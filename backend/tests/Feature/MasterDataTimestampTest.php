<?php

namespace Tests\Feature;

use App\Models\Account;
use App\Models\Tax;
use App\Models\Uqc;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class MasterDataTimestampTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        $user = \App\Models\User::factory()->create();
        $this->actingAs($user);
    }

    /** @test */
    public function tax_model_has_timestamps_enabled()
    {
        $tax = new Tax();
        $this->assertTrue($tax->usesTimestamps());
    }

    /** @test */
    public function uqc_model_has_timestamps_enabled()
    {
        $uqc = new Uqc();
        $this->assertTrue($uqc->usesTimestamps());
    }

    /** @test */
    public function account_model_has_timestamps_enabled()
    {
        $account = new Account();
        $this->assertTrue($account->usesTimestamps());
    }

    /** @test */
    public function tax_returns_timestamps_in_api_response()
    {
        $tax = Tax::factory()->create();

        $response = $this->getJson('/api/taxes');

        $response->assertStatus(200)
            ->assertJsonStructure([
                'success',
                'data' => [
                    '*' => [
                        'id',
                        'scheme_name',
                        'created_at',
                        'updated_at',
                    ]
                ]
            ]);
    }
}
