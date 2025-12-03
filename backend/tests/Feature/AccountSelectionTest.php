<?php

namespace Tests\Feature;

use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountSelectionTest extends TestCase
{
    use RefreshDatabase;
    protected $user;
    protected $account1;
    protected $account2;

    protected function setUp(): void
    {
        parent::setUp();

        // Create user
        $this->user = User::factory()->create();

        // Create two accounts
        $this->account1 = Account::factory()->create(['name' => 'Account 1']);
        $this->account2 = Account::factory()->create(['name' => 'Account 2']);

        // Assign both accounts to user
        $this->user->accounts()->attach($this->account1->id, ['role' => 'admin']);
        $this->user->accounts()->attach($this->account2->id, ['role' => 'user']);

        $this->actingAs($this->user);
    }

    /** @test */
    public function user_can_fetch_their_accounts()
    {
        $response = $this->get('/admin/select-account');

        $response->assertStatus(200);
        $response->assertJsonStructure([
            'accounts' => [
                '*' => ['id', 'name', 'name_formatted', 'role', 'visibility']
            ]
        ]);

        $accounts = $response->json('accounts');
        $this->assertCount(2, $accounts);
    }

    /** @test */
    public function user_can_select_an_account_they_have_access_to()
    {
        $response = $this->post('/admin/select-account', [
            'account_id' => $this->account1->id
        ]);

        $response->assertStatus(200);
        $response->assertJson([
            'message' => 'Account selected successfully.',
            'account_id' => $this->account1->id
        ]);

        // Verify session was set
        $this->assertEquals($this->account1->id, session('current_account_id'));

        // Verify user's current_account_id was updated
        $this->assertEquals($this->account1->id, $this->user->fresh()->current_account_id);
    }

    /** @test */
    public function user_cannot_select_account_they_dont_have_access_to()
    {
        $unauthorizedAccount = Account::factory()->create(['name' => 'Unauthorized']);

        $response = $this->post('/admin/select-account', [
            'account_id' => $unauthorizedAccount->id
        ]);

        $response->assertStatus(403);
        $response->assertJson([
            'message' => 'You do not have access to this account.'
        ]);
    }

    /** @test */
    public function selecting_account_requires_valid_account_id()
    {
        $response = $this->postJson('/admin/select-account', [
            'account_id' => 99999 // Non-existent
        ]);

        $response->assertStatus(422); // Validation error
        $response->assertJsonValidationErrors('account_id');
    }

    /** @test */
    public function selecting_account_requires_account_id_parameter()
    {
        $response = $this->postJson('/admin/select-account', []);

        $response->assertStatus(422);
        $response->assertJsonValidationErrors('account_id');
    }

    /** @test */
    public function middleware_redirects_when_no_account_selected()
    {
        // Don't set current_account_id
        $this->user->update(['current_account_id' => null]);
        session()->forget('current_account_id');

        $response = $this->get('/admin/dashboard');

        $response->assertRedirect('/admin/dashboard');
        $response->assertSessionHas('show_account_selection', true);
    }

    /** @test */
    public function middleware_allows_access_with_valid_account()
    {
        // Set valid current account
        $this->user->update(['current_account_id' => $this->account1->id]);

        $response = $this->get('/admin/dashboard');

        $response->assertStatus(200);
    }

    /** @test */
    public function middleware_redirects_when_account_access_revoked()
    {
        // Set current account
        $this->user->update(['current_account_id' => $this->account1->id]);

        // Remove user's access to the account
        $this->user->accounts()->detach($this->account1->id);

        $response = $this->get('/admin/dashboard');

        $response->assertRedirect('/admin/dashboard');
        $response->assertSessionHas('show_account_selection', true);
        $response->assertSessionHas('error', 'Account access revoked. Please select another account.');
    }

    /** @test */
    public function user_with_single_account_can_auto_select()
    {
        // Remove second account
        $this->user->accounts()->detach($this->account2->id);

        $response = $this->get('/admin/select-account');

        $accounts = $response->json('accounts');
        $this->assertCount(1, $accounts);
        $this->assertEquals($this->account1->id, $accounts[0]['id']);
    }

    /** @test */
    public function account_selection_persists_across_requests()
    {
        $this->post('/admin/select-account', [
            'account_id' => $this->account1->id
        ]);

        // Make another request
        $response = $this->get('/admin/items');

        // Should not be redirected
        $response->assertStatus(200);
    }
}
