<?php

namespace Tests\Unit\Models;

use App\Models\Account;
use App\Models\Tax;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AccountTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function it_has_fillable_tax_fields()
    {
        $fillable = (new Account)->getFillable();
        
        $this->assertContains('country', $fillable);
        $this->assertContains('state', $fillable);
        $this->assertContains('tax_number', $fillable);
        $this->assertContains('default_tax_id', $fillable);
    }

    /** @test */
    public function it_belongs_to_a_default_tax()
    {
        $tax = Tax::factory()->create(['scheme_name' => 'GST 18%']);
        
        $account = Account::factory()->create([
            'default_tax_id' => $tax->id
        ]);

        $this->assertInstanceOf(Tax::class, $account->defaultTax);
        $this->assertEquals('GST 18%', $account->defaultTax->scheme_name);
    }

    /** @test */
    public function it_can_be_created_with_country_and_state()
    {
        $account = Account::factory()->create([
            'name' => 'Test Account',
            'name_formatted' => 'Test Account',
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5'
        ]);

        $this->assertEquals('India', $account->country);
        $this->assertEquals('Maharashtra', $account->state);
        $this->assertEquals('27AAAAA0000A1Z5', $account->tax_number);
    }

    /** @test */
    public function it_can_have_different_countries()
    {
        $indiaAccount = Account::factory()->create(['country' => 'India']);
        $saudiAccount = Account::factory()->create(['country' => 'Saudi Arabia']);
        $uaeAccount = Account::factory()->create(['country' => 'UAE']);

        $this->assertEquals('India', $indiaAccount->country);
        $this->assertEquals('Saudi Arabia', $saudiAccount->country);
        $this->assertEquals('UAE', $uaeAccount->country);
    }

    /** @test */
    public function default_tax_id_can_be_null()
    {
        $account = Account::factory()->create([
            'default_tax_id' => null
        ]);

        $this->assertNull($account->default_tax_id);
        $this->assertNull($account->defaultTax);
    }
}
