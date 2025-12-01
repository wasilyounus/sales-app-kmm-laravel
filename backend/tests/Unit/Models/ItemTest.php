<?php

namespace Tests\Unit\Models;

use App\Models\Item;
use App\Models\Tax;
use App\Models\Account;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ItemTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function it_has_tax_id_in_fillable()
    {
        $fillable = (new Item)->getFillable();
        
       $this->assertContains('tax_id', $fillable);
    }

    /** @test */
    public function it_belongs_to_a_tax()
    {
        $account = Account::factory()->create();
        $tax = Tax::factory()->create(['scheme_name' => 'GST 18%']);
        
        $item = Item::factory()->create([
            'account_id' => $account->id,
            'tax_id' => $tax->id
        ]);

        $this->assertInstanceOf(Tax::class, $item->tax);
        $this->assertEquals('GST 18%', $item->tax->scheme_name);
    }

    /** @test */
    public function tax_id_can_be_null()
    {
        $account = Account::factory()->create();
        
        $item = Item::factory()->create([
            'account_id' => $account->id,
            'tax_id' => null
        ]);

        $this->assertNull($item->tax_id);
        $this->assertNull($item->tax);
    }

    /** @test */
    public function it_can_be_created_with_tax()
    {
        $account = Account::factory()->create();
        $tax = Tax::factory()->create();

        $item = Item::factory()->create([
            'name' => 'Test Product',
            'account_id' => $account->id,
            'tax_id' => $tax->id,
            'uqc' => 1
        ]);

        $this->assertEquals('Test Product', $item->name);
        $this->assertEquals($tax->id, $item->tax_id);
        $this->assertNotNull($item->tax);
    }

    /** @test */
    public function it_belongs_to_an_account()
    {
        $account = Account::factory()->create();
        $item = Item::factory()->create(['account_id' => $account->id]);

        $this->assertInstanceOf(Account::class, $item->account);
        $this->assertEquals($account->id, $item->account->id);
    }
}
