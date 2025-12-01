<?php

namespace Tests\Feature;

use App\Models\Account;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountManagementTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        
        // Create and authenticate a user
        $this->user = User::factory()->create();
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_can_create_account_with_tax_fields()
    {
        $response = $this->post('/admin/accounts', [
            'name' => 'Test Company',
            'name_formatted' => 'TEST COMPANY',
            'taxation_type' => 2,
            'tax_rate' => 18,
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5'
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
        $response = $this->post('/admin/accounts', [
            'name' => 'Test Company',
            'name_formatted' => 'TEST COMPANY',
            'taxation_type' => 2,
            'tax_rate' => 18,
            // country is missing
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5'
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

        $response = $this->put("/admin/accounts/{$account->id}", [
            'name' => $account->name,
            'name_formatted' => $account->name_formatted,
            'taxation_type' => 2,
            'tax_rate' => 18,
            'country' => 'Saudi Arabia',
            'state' => null,
            'tax_number' => 'VAT123456'
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
        $response = $this->post('/admin/accounts', [
            'name' => 'Saudi Company',
            'name_formatted' => 'SAUDI COMPANY',
            'taxation_type' => 2,
            'tax_rate' => 15,
            'country' => 'Saudi Arabia',
            'state' => null, // no states for Saudi Arabia
            'tax_number' => 'VAT123456'
        ]);

        $response->assertRedirect('/admin/accounts');
        
        $this->assertDatabaseHas('accounts', [
            'name' => 'Saudi Company',
            'country' => 'Saudi Arabia',
            'state' => null
        ]);
    }
}
