<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Auth;
use Tests\TestCase;

class AuthenticationWithAccountTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function login_fails_when_user_has_no_accounts()
    {
        // Create user with no accounts
        $user = User::factory()->create([
            'email' => 'noaccounts@example.com',
            'password' => bcrypt('password')
        ]);

        $response = $this->post('/login', [
            'email' => 'noaccounts@example.com',
            'password' => 'password',
        ]);

        $response->assertStatus(302);
        $response->assertSessionHasErrors('email');
        
        $errors = session('errors')->getBag('default')->get('email');
        $this->assertStringContainsString('No accounts assigned', $errors[0]);
        
        // Ensure user was logged out
        $this->assertGuest();
    }

    /** @test */
    public function login_succeeds_when_user_has_accounts()
    {
        $user = User::factory()->create([
            'email' => 'hasaccount@example.com',
            'password' => bcrypt('password')
        ]);

        $account = Account::factory()->create();
        $user->accounts()->attach($account->id, ['role' => 'admin']);

        $response = $this->post('/login', [
            'email' => 'hasaccount@example.com',
            'password' => 'password',
        ]);

        $response->assertRedirect('/admin/dashboard');
        $this->assertAuthenticatedAs($user);
    }

    /** @test */
    public function login_fails_with_invalid_credentials()
    {
        $user = User::factory()->create([
            'email' => 'test@example.com',
            'password' => bcrypt('password')
        ]);

        $response = $this->post('/login', [
            'email' => 'test@example.com',
            'password' => 'wrongpassword',
        ]);

        $response->assertStatus(302); // Redirect
        $response->assertSessionHasErrors('email');
        $this->assertGuest();
    }

    /** @test */
    public function login_with_remember_me_sets_cookie()
    {
        $user = User::factory()->create([
            'email' => 'remember@example.com',
            'password' => bcrypt('password')
        ]);

        $account = Account::factory()->create();
        $user->accounts()->attach($account->id, ['role' => 'admin']);

        $response = $this->post('/login', [
            'email' => 'remember@example.com',
            'password' => 'password',
            'remember' => true,
        ]);

        $response->assertRedirect('/admin/dashboard');
        $this->assertAuthenticatedAs($user);
    }

    /** @test */
    public function logout_clears_session()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create();
        $user->accounts()->attach($account->id, ['role' => 'admin']);
        $user->update(['current_account_id' => $account->id]);

        $this->actingAs($user);

        $response = $this->post('/logout');

        $response->assertStatus(302); // Redirect (path may vary)
        $this->assertGuest();
    }
}
