<?php

namespace Tests\Feature;

use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountManagementTest extends TestCase
{
    use RefreshDatabase;
    protected $user;

    protected function setUp(): void
    {
        parent::setUp();

        // Create user and account
        $this->user = User::factory()->create();
        $account = Account::factory()->create();
        
        // Assign account to user
        $this->user->accounts()->attach($account->id, ['role' => 'admin']);
        $this->user->update(['current_account_id' => $account->id]);
        
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_can_create_account_with_tax_fields()
    {
        $response = $this->from('/admin/accounts')->post('/admin/accounts', [
            'name' => 'Test Company',
            'name_formatted' => 'TEST COMPANY',
            'desc' => 'Test Description',
            'taxation_type' => 2,
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5',
            'financial_year_start' => '2025-04-01 00:00:00'
        ]);

        $response->assertRedirect('/admin/accounts');

        $this->assertDatabaseHas('accounts', [
            'name' => 'Test Company',
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5'
        ]);
    }

    /** @test */
    public function country_field_is_required()
    {
        $response = $this->from('/admin/accounts')->post('/admin/accounts', [
            'name' => 'Test Company',
            'name_formatted' => 'TEST COMPANY',
            'desc' => 'Test Description',
            'taxation_type' => 2,
            // country is missing
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5',
            'financial_year_start' => '2025-04-01 00:00:00'
        ]);

        $response->assertSessionHasErrors('country');
    }

    /** @test */
    public function it_can_update_account_tax_fields()
    {
        $account = Account::factory()->create([
            'country' => 'India',
            'state' => 'Delhi'
        ]);


        $response = $this->from('/admin/accounts')->put("/admin/accounts/{$account->id}", [
            'name' => $account->name,
            'name_formatted' => $account->name_formatted,
            'desc' => $account->desc,
            'taxation_type' => 2,
            'country' => 'Saudi Arabia',
            'state' => null,
            'tax_number' => 'VAT123456',
            'financial_year_start' => '2025-04-01 00:00:00'
        ]);

        $response->assertRedirect('/admin/accounts');

        $this->assertDatabaseHas('accounts', [
            'id' => $account->id,
            'country' => 'Saudi Arabia',
            'state' => null,
            'tax_number' => 'VAT123456'
        ]);
    }

    /** @test */
    public function state_can_be_nullable()
    {
        $response = $this->from('/admin/accounts')->post('/admin/accounts', [
            'name' => 'Saudi Company',
            'name_formatted' => 'SAUDI COMPANY',
            'desc' => 'Saudi Company Description',
            'taxation_type' => 2,
            'country' => 'Saudi Arabia',
            'state' => null, // no states for Saudi Arabia
            'tax_number' => 'VAT123456',
            'financial_year_start' => '2025-04-01 00:00:00'
        ]);

        $response->assertRedirect('/admin/accounts');

        $this->assertDatabaseHas('accounts', [
            'name' => 'Saudi Company',
            'country' => 'Saudi Arabia',
            'state' => null
        ]);
    }
}
