<?php

namespace Tests\Feature;

use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountVisibilityTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function accounts_default_to_private_visibility()
    {
        $account = Account::factory()->create([
            'name' => 'Private Account',
        ]);

        $this->assertEquals('private', $account->visibility);
    }

    /** @test */
    public function accounts_can_be_created_as_public()
    {
        $account = Account::factory()->create([
            'name' => 'Public Account',
            'visibility' => 'public',
        ]);

        $this->assertEquals('public', $account->visibility);
    }

    /** @test */
    public function account_visibility_is_included_in_selection_response()
    {
        $user = User::factory()->create();
        
        $privateAccount = Account::factory()->create([
            'name' => 'Private Account',
            'visibility' => 'private'
        ]);
        
        $publicAccount = Account::factory()->create([
            'name' => 'Public Account',
            'visibility' => 'public'
        ]);

        $user->accounts()->attach($privateAccount->id, ['role' => 'admin']);
        $user->accounts()->attach($publicAccount->id, ['role' => 'user']);

        $this->actingAs($user);

        $response = $this->get('/admin/select-account');

        $response->assertStatus(200);
        
        $accounts = $response->json('accounts');
        
        $private = collect($accounts)->firstWhere('id', $privateAccount->id);
        $public = collect($accounts)->firstWhere('id', $publicAccount->id);
        
        $this->assertEquals('private', $private['visibility']);
        $this->assertEquals('public', $public['visibility']);
    }

    /** @test */
    public function visibility_can_be_updated()
    {
        $account = Account::factory()->create([
            'visibility' => 'private'
        ]);

        $account->update(['visibility' => 'public']);

        $this->assertEquals('public', $account->fresh()->visibility);
    }
}
