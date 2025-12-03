<?php

namespace Tests\Feature;

use App\Models\Item;
use App\Models\Account;
use App\Models\Tax;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ItemTaxSelectionTest extends TestCase
{
    use RefreshDatabase;
    protected $user;
    protected $account;

    protected function setUp(): void
    {
        parent::setUp();
        
        $this->user = User::factory()->create();
        $this->actingAs($this->user);
        $this->account = Account::factory()->create();
    }

    /** @test */
    public function item_can_be_created_without_tax()
    {
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => null
        ]);

        $this->assertNull($item->tax_id);
        $this->assertNull($item->tax);
    }

    /** @test */
    public function item_can_be_created_with_tax()
    {
        $tax = Tax::factory()->create(['scheme_name' => 'GST 18%']);
        
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);

        $this->assertEquals($tax->id, $item->tax_id);
        $this->assertNotNull($item->tax);
        $this->assertEquals('GST 18%', $item->tax->scheme_name);
    }

    /** @test */
    public function item_tax_relationship_loads_correctly()
    {
        $tax = Tax::factory()->create([
            'scheme_name' => 'VAT 15%',
            'tax1_name' => 'VAT',
            'tax1_val' => 15.0
        ]);
        
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);

        $item->load('tax');
        
        $this->assertInstanceOf(Tax::class, $item->tax);
        $this->assertEquals('VAT 15%', $item->tax->scheme_name);
        $this->assertEquals(15.0, $item->tax->tax1_val);
    }

    /** @test */
    public function item_tax_can_be_updated()
    {
        $tax1 = Tax::factory()->create(['scheme_name' => 'GST 5%']);
        $tax2 = Tax::factory()->create(['scheme_name' => 'GST 18%']);
        
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax1->id
        ]);

        $this->assertEquals($tax1->id, $item->tax_id);

        // Update to different tax
        $item->update(['tax_id' => $tax2->id]);
        
        $this->assertEquals($tax2->id, $item->fresh()->tax_id);
    }

    /** @test */
    public function item_tax_can_be_removed()
    {
        $tax = Tax::factory()->create();
        
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);

        $this->assertNotNull($item->tax_id);

        // Remove tax
        $item->update(['tax_id' => null]);
        
        $this->assertNull($item->fresh()->tax_id);
    }

    /** @test */
    public function multiple_items_can_have_same_tax()
    {
        $tax = Tax::factory()->create(['scheme_name' => 'GST 12%']);
        
        $item1 = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);
        
        $item2 = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);

        $this->assertEquals($tax->id, $item1->tax_id);
        $this->assertEquals($tax->id, $item2->tax_id);
    }

    /** @test */
    public function item_tax_survives_deletion_of_tax()
    {
        $tax = Tax::factory()->create();
        
        $item = Item::factory()->create([
            'account_id' => $this->account->id,
            'tax_id' => $tax->id
        ]);

        $this->assertNotNull($item->tax);

        // Soft delete tax
        $tax->delete();

        // Item should still reference tax_id but relationship returns null
        $this->assertEquals($tax->id, $item->fresh()->tax_id);
    }
}
