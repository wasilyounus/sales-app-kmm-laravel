<?php

namespace Database\Seeders;

use App\Models\Party;
use Illuminate\Database\Seeder;

class PartySeeder extends Seeder
{
    public function run(): void
    {
        // Get Demo Account or default to 1
        $account = \App\Models\Account::where('name', 'Demo Company')->first();
        $accountId = $account ? $account->id : 1;

        $parties = [
            [
                'name' => 'Acme Corporation',
                'tax_number' => '27AABCU9603R1ZM',
                'phone' => '9876543210',
                'email' => 'contact@acme.com',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Tech Solutions Pvt Ltd',
                'tax_number' => '29AABCT1234D1ZK',
                'phone' => '9988776655',
                'email' => 'info@techsolutions.in',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Global Traders',
                'tax_number' => '24AABCG5678H1ZJ',
                'phone' => '8877665544',
                'email' => 'sales@globaltraders.com',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Metro Supplies',
                'tax_number' => '33AABCM9012L1ZI',
                'phone' => '7766554433',
                'email' => 'orders@metrosupplies.in',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Premium Industries',
                'tax_number' => '06AABCP3456N1ZH',
                'phone' => '6655443322',
                'email' => 'hello@premiumindustries.com',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Star Enterprises',
                'tax_number' => '19AABCS7890Q1ZG',
                'phone' => '5544332211',
                'email' => 'star@enterprises.in',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Royal Distributors',
                'tax_number' => '07AABCR2345T1ZF',
                'phone' => '4433221100',
                'email' => 'contact@royaldist.com',
                'account_id' => $accountId,
            ],
            [
                'name' => 'Quick Mart',
                'tax_number' => '32AABCQ6789W1ZE',
                'phone' => '3322110099',
                'email' => 'quickmart@gmail.com',
                'account_id' => $accountId,
            ],
        ];

        foreach ($parties as $partyData) {
            $party = Party::firstOrCreate(
                ['name' => $partyData['name'], 'account_id' => $partyData['account_id']],
                $partyData
            );

            // Add Address if not exists
            if ($party->addresses()->count() == 0) {
                $party->addresses()->create([
                    'line1' => 'Suite #' . rand(100, 999) . ', Business Park',
                    'place' => 'Mumbai',
                    'district' => 'Mumbai City',
                    'state' => 'Maharashtra',
                    'country' => 'India',
                    'pincode' => '4000' . rand(10, 99),
                    'account_id' => $partyData['account_id']
                ]);
            }
        }
    }
}
