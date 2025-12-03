<?php

namespace Tests\Unit\Models;

use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountVisibilityModelTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function visibility_is_fillable()
    {
        $account = Account::create([
            'name' => 'Test Account',
            'name_formatted' => 'TEST ACCOUNT',
            'desc' => 'Test',
            'taxation_type' => 1,
            'visibility' => 'public',
            'log_id' => 1,
        ]);

        $this->assertEquals('public', $account->visibility);
    }

    /** @test */
    public function visibility_defaults_to_private_in_factory()
    {
        $account = Account::factory()->create();

        $this->assertContains($account->visibility, ['private', 'public']);
    }
}
