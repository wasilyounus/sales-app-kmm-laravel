<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Account;
use App\Models\Company; // Changed from Account to Company
use Illuminate\Support\Facades\DB;

class ChartOfAccountsSeeder extends Seeder
{
    public function run(): void
    {
        // Ensure there's at least one company to link to
        $company = Company::first(); // Changed from Account::first() to Company::first()

        if (!$company) { // Changed from $account to $company
            $this->command->warn('No companies found. Please create a company first.'); // Updated message
            $this->command->info('Hint: Ensure you have run migrations and seeders for the companies table.'); // Updated message
            return;
        }

        $companyId = $company->id; // Changed from $accountId = $account->id to $companyId = $company->id

        $accounts = [
            // ========== ASSETS ==========
            [
                'code' => '1000',
                'name' => 'Assets',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => null,
                'system' => true,
                'desc' => 'All asset accounts'
            ],

            // Current Assets
            [
                'code' => '1100',
                'name' => 'Current Assets',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1000',
                'system' => true,
                'desc' => 'Assets expected to be converted to cash within one year'
            ],
            [
                'code' => '1110',
                'name' => 'Cash',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1100',
                'system' => true,
                'desc' => 'Cash on hand'
            ],
            [
                'code' => '1120',
                'name' => 'Bank Accounts',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1100',
                'system' => true,
                'desc' => 'Bank account balances'
            ],
            [
                'code' => '1130',
                'name' => 'Accounts Receivable',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1100',
                'system' => true,
                'desc' => 'Money owed by customers'
            ],
            [
                'code' => '1140',
                'name' => 'Inventory',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1100',
                'system' => true,
                'desc' => 'Goods available for sale'
            ],

            // Fixed Assets
            [
                'code' => '1200',
                'name' => 'Fixed Assets',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1000',
                'system' => true,
                'desc' => 'Long-term tangible assets'
            ],
            [
                'code' => '1210',
                'name' => 'Equipment',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1200',
                'system' => false,
                'desc' => 'Equipment and machinery'
            ],
            [
                'code' => '1220',
                'name' => 'Vehicles',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1200',
                'system' => false,
                'desc' => 'Company vehicles'
            ],
            [
                'code' => '1230',
                'name' => 'Furniture & Fixtures',
                'type' => 'ASSET',
                'balance' => 'DEBIT',
                'parent' => '1200',
                'system' => false,
                'desc' => 'Office furniture and fixtures'
            ],

            // ========== LIABILITIES ==========
            [
                'code' => '2000',
                'name' => 'Liabilities',
                'type' => 'LIABILITY',
                'balance' => 'CREDIT',
                'parent' => null,
                'system' => true,
                'desc' => 'All liability accounts'
            ],

            // Current Liabilities
            [
                'code' => '2100',
                'name' => 'Current Liabilities',
                'type' => 'LIABILITY',
                'balance' => 'CREDIT',
                'parent' => '2000',
                'system' => true,
                'desc' => 'Obligations due within one year'
            ],
            [
                'code' => '2110',
                'name' => 'Accounts Payable',
                'type' => 'LIABILITY',
                'balance' => 'CREDIT',
                'parent' => '2100',
                'system' => true,
                'desc' => 'Money owed to suppliers'
            ],
            [
                'code' => '2120',
                'name' => 'Tax Payable',
                'type' => 'LIABILITY',
                'balance' => 'CREDIT',
                'parent' => '2100',
                'system' => true,
                'desc' => 'GST/VAT/Tax owed to government'
            ],
            [
                'code' => '2130',
                'name' => 'Salaries Payable',
                'type' => 'LIABILITY',
                'balance' => 'CREDIT',
                'parent' => '2100',
                'system' => false,
                'desc' => 'Unpaid employee salaries'
            ],

            // ========== EQUITY ==========
            [
                'code' => '3000',
                'name' => 'Equity',
                'type' => 'EQUITY',
                'balance' => 'CREDIT',
                'parent' => null,
                'system' => true,
                'desc' => 'Owner equity and retained earnings'
            ],
            [
                'code' => '3100',
                'name' => 'Owner\'s Capital',
                'type' => 'EQUITY',
                'balance' => 'CREDIT',
                'parent' => '3000',
                'system' => true,
                'desc' => 'Owner investment in business'
            ],
            [
                'code' => '3200',
                'name' => 'Retained Earnings',
                'type' => 'EQUITY',
                'balance' => 'CREDIT',
                'parent' => '3000',
                'system' => true,
                'desc' => 'Accumulated profits'
            ],
            [
                'code' => '3300',
                'name' => 'Drawings',
                'type' => 'EQUITY',
                'balance' => 'DEBIT',
                'parent' => '3000',
                'system' => false,
                'desc' => 'Owner withdrawals'
            ],

            // ========== REVENUE ==========
            [
                'code' => '4000',
                'name' => 'Revenue',
                'type' => 'REVENUE',
                'balance' => 'CREDIT',
                'parent' => null,
                'system' => true,
                'desc' => 'All revenue accounts'
            ],
            [
                'code' => '4100',
                'name' => 'Sales Revenue',
                'type' => 'REVENUE',
                'balance' => 'CREDIT',
                'parent' => '4000',
                'system' => true,
                'desc' => 'Revenue from sale of goods/services'
            ],
            [
                'code' => '4200',
                'name' => 'Other Income',
                'type' => 'REVENUE',
                'balance' => 'CREDIT',
                'parent' => '4000',
                'system' => false,
                'desc' => 'Non-operating income'
            ],
            [
                'code' => '4210',
                'name' => 'Interest Income',
                'type' => 'REVENUE',
                'balance' => 'CREDIT',
                'parent' => '4200',
                'system' => false,
                'desc' => 'Interest earned'
            ],

            // ========== EXPENSES ==========
            [
                'code' => '5000',
                'name' => 'Expenses',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => null,
                'system' => true,
                'desc' => 'All expense accounts'
            ],

            // Cost of Goods Sold
            [
                'code' => '5100',
                'name' => 'Cost of Goods Sold',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5000',
                'system' => true,
                'desc' => 'Direct costs of producing goods sold'
            ],
            [
                'code' => '5110',
                'name' => 'Purchases',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5100',
                'system' => true,
                'desc' => 'Cost of inventory purchased'
            ],

            // Operating Expenses
            [
                'code' => '5200',
                'name' => 'Operating Expenses',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5000',
                'system' => true,
                'desc' => 'Day-to-day business expenses'
            ],

            // Your requested expense categories
            [
                'code' => '5210',
                'name' => 'Transport Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Transportation and delivery costs'
            ],
            [
                'code' => '5220',
                'name' => 'Petrol Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Fuel costs for vehicles'
            ],
            [
                'code' => '5230',
                'name' => 'Commission Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Sales commissions paid'
            ],
            [
                'code' => '5240',
                'name' => 'Bank Charges',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Bank fees and cheque return charges'
            ],
            [
                'code' => '5250',
                'name' => 'Sundry Expenses',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Miscellaneous small expenses'
            ],
            [
                'code' => '5260',
                'name' => 'Other Expenses',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Other uncategorized expenses'
            ],
            [
                'code' => '5270',
                'name' => 'Rent Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Office/shop rent'
            ],
            [
                'code' => '5280',
                'name' => 'Utilities Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Electricity, water, internet'
            ],
            [
                'code' => '5290',
                'name' => 'Salary Expense',
                'type' => 'EXPENSE',
                'balance' => 'DEBIT',
                'parent' => '5200',
                'system' => false,
                'desc' => 'Employee salaries and wages'
            ],
        ];

        // Insert in order to handle parent references
        $idMap = [];

        foreach ($accounts as $account) {
            $parentId = null;
            if ($account['parent']) {
                $parentId = $idMap[$account['parent']] ?? null;
            }

            $id = DB::table('chart_of_accounts')->insertGetId([
                'account_code' => $account['code'],
                'account_name' => $account['name'],
                'account_type' => $account['type'],
                'normal_balance' => $account['balance'],
                'parent_account_id' => $parentId,
                'is_active' => true,
                'is_system' => $account['system'],
                'description' => $account['desc'],
                'company_id' => $companyId,
                'created_at' => now(),
                'updated_at' => now(),
            ]);

            $idMap[$account['code']] = $id;
        }

        $this->command->info('Chart of Accounts seeded successfully!');
        $this->command->info('Total accounts created: ' . count($accounts));
    }
}
