<?php

namespace Tests\Feature;

use Tests\TestCase;
use App\Models\Company;
use App\Models\User;
use App\Models\Party;
use App\Models\Item;
use App\Models\Tax;
use App\Models\Sale;
use App\Models\Purchase;
use App\Models\ChartOfAccount;
use App\Models\JournalEntry;
use Illuminate\Foundation\Testing\RefreshDatabase;

class AccountingSystemTest extends TestCase
{
    use RefreshDatabase;

    protected $company;
    protected $user;

    protected function setUp(): void
    {
        parent::setUp();

        // Create test company
        $this->company = Company::create([
            'name' => 'Test Company Ltd',
            'country' => 'IN',
            'tax_number' => 'TEST123456',
        ]);

        // Create test user
        $this->user = User::factory()->create();
        $this->user->companies()->attach($this->company->id, ['role' => 'admin']);
        $this->actingAs($this->user);
    }

    /** @test */
    public function it_creates_companies_successfully()
    {
        $this->assertDatabaseHas('companies', [
            'name' => 'Test Company Ltd',
        ]);
    }

    /** @test */
    public function it_creates_parties_with_company_id()
    {
        $party = Party::create([
            'name' => 'Test Customer',
            'phone' => '1234567890',
            'company_id' => $this->company->id,
        ]);

        $this->assertDatabaseHas('parties', [
            'name' => 'Test Customer',
            'company_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function it_creates_items_with_company_id()
    {
        $item = Item::create([
            'name' => 'Test Product',
            'price' => 100.00,
            'company_id' => $this->company->id,
        ]);

        $this->assertDatabaseHas('items', [
            'name' => 'Test Product',
            'company_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function it_creates_chart_of_accounts()
    {
        $coa = ChartOfAccount::create([
            'account_code' => '1000',
            'account_name' => 'Assets',
            'account_type' => 'ASSET',
            'normal_balance' => 'DEBIT',
            'is_system' => true,
            'company_id' => $this->company->id,
        ]);

        $this->assertDatabaseHas('chart_of_accounts', [
            'account_code' => '1000',
            'account_name' => 'Assets',
            'company_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function it_creates_journal_entries()
    {
        $entry = JournalEntry::create([
            'entry_number' => 'JE-TEST-001',
            'entry_date' => now(),
            'description' => 'Test Entry',
            'company_id' => $this->company->id,
            'created_by' => $this->user->id,
        ]);

        $this->assertDatabaseHas('journal_entries', [
            'entry_number' => 'JE-TEST-001',
            'company_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function sales_use_company_id_not_account_id()
    {
        $party = Party::create([
            'name' => 'Customer',
            'company_id' => $this->company->id,
        ]);

        $sale = Sale::create([
            'party_id' => $party->id,
            'date' => now(),
            'company_id' => $this->company->id,
        ]);

        $this->assertDatabaseHas('sales', [
            'party_id' => $party->id,
            'company_id' => $this->company->id,
        ]);

        // Ensure no account_id column exists
        $this->assertDatabaseMissing('sales', [
            'account_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function purchases_use_company_id_not_account_id()
    {
        $party = Party::create([
            'name' => 'Vendor',
            'company_id' => $this->company->id,
        ]);

        $purchase = Purchase::create([
            'party_id' => $party->id,
            'date' => now(),
            'company_id' => $this->company->id,
        ]);

        $this->assertDatabaseHas('purchases', [
            'party_id' => $party->id,
            'company_id' => $this->company->id,
        ]);
    }

    /** @test */
    public function user_company_permissions_table_exists()
    {
        $this->assertTrue(
            \Schema::hasTable('user_company_permissions')
        );

        // Ensure old table doesn't exist
        $this->assertFalse(
            \Schema::hasTable('user_account_permissions')
        );
    }
}
