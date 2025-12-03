<?php

namespace Tests\Unit\Models;

use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class UserAccountRelationshipTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function user_has_accounts_relationship()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create();

        $user->accounts()->attach($account->id, ['role' => 'admin']);

        $this->assertInstanceOf(\Illuminate\Database\Eloquent\Collection::class, $user->accounts);
        $this->assertCount(1, $user->accounts);
        $this->assertEquals($account->id, $user->accounts->first()->id);
    }

    /** @test */
    public function user_accounts_include_pivot_role()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create();

        $user->accounts()->attach($account->id, ['role' => 'admin']);

        $userAccount = $user->accounts()->first();
        $this->assertEquals('admin', $userAccount->pivot->role);
    }

    /** @test */
    public function has_accounts_returns_true_when_user_has_accounts()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create();

        $user->accounts()->attach($account->id, ['role' => 'user']);

        $this->assertTrue($user->hasAccounts());
    }

    /** @test */
    public function has_accounts_returns_false_when_user_has_no_accounts()
    {
        $user = User::factory()->create();

        $this->assertFalse($user->hasAccounts());
    }

    /** @test */
    public function get_accounts_for_selection_returns_formatted_data()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create([
            'name' => 'Test Company',
            'name_formatted' => 'TEST COMPANY',
            'visibility' => 'private'
        ]);

        $user->accounts()->attach($account->id, ['role' => 'admin']);

        $accounts = $user->getAccountsForSelection();

        $this->assertCount(1, $accounts);
        $accountData = $accounts->first();

        $this->assertEquals($account->id, $accountData['id']);
        $this->assertEquals('Test Company', $accountData['name']);
        $this->assertEquals('TEST COMPANY', $accountData['name_formatted']);
        $this->assertEquals('admin', $accountData['role']);
        $this->assertEquals('private', $accountData['visibility']);
    }

    /** @test */
    public function current_account_id_is_fillable()
    {
        $user = User::factory()->create();
        $account = Account::factory()->create();

        $user->update(['current_account_id' => $account->id]);

        $this->assertEquals($account->id, $user->fresh()->current_account_id);
    }

    /** @test */
    public function user_can_have_multiple_accounts()
    {
        $user = User::factory()->create();
        $account1 = Account::factory()->create(['name' => 'Account 1']);
        $account2 = Account::factory()->create(['name' => 'Account 2']);

        $user->accounts()->attach($account1->id, ['role' => 'admin']);
        $user->accounts()->attach($account2->id, ['role' => 'user']);

        $this->assertCount(2, $user->accounts);
    }
}
