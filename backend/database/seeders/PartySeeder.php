<?php

namespace Database\Seeders;

use App\Models\Party;
use Illuminate\Database\Seeder;

class PartySeeder extends Seeder
{
    public function run(): void
    {
        // Get Demo Account or default to 1
        $company = \App\Models\Company::where('name', 'Demo Company')->first();
        $companyId = $company ? $company->id : 1;

        $parties = [
            [
                'name' => 'Acme Corporation',
                'tax_number' => '27AABCU9603R1ZM',
                'phone' => '9876543210',
                'email' => 'contact@acme.com',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Tech Solutions Pvt Ltd',
                'tax_number' => '29AABCT1234D1ZK',
                'phone' => '9988776655',
                'email' => 'info@techsolutions.in',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Global Traders',
                'tax_number' => '24AABCG5678H1ZJ',
                'phone' => '8877665544',
                'email' => 'sales@globaltraders.com',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Metro Supplies',
                'tax_number' => '33AABCM9012L1ZI',
                'phone' => '7766554433',
                'email' => 'orders@metrosupplies.in',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Premium Industries',
                'tax_number' => '06AABCP3456N1ZH',
                'phone' => '6655443322',
                'email' => 'hello@premiumindustries.com',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Star Enterprises',
                'tax_number' => '19AABCS7890Q1ZG',
                'phone' => '5544332211',
                'email' => 'star@enterprises.in',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Royal Distributors',
                'tax_number' => '07AABCR2345T1ZF',
                'phone' => '4433221100',
                'email' => 'contact@royaldist.com',
                'company_id' => $companyId,
            ],
            [
                'name' => 'Quick Mart',
                'tax_number' => '32AABCQ6789W1ZE',
                'phone' => '3322110099',
                'email' => 'quickmart@gmail.com',
                'company_id' => $companyId,
            ],
        ];

        foreach ($parties as $partyData) {
            $party = Party::firstOrCreate(
                ['name' => $partyData['name'], 'company_id' => $partyData['company_id']],
                $partyData
            );

            // Add Address if not exists
            if ($party->addresses()->count() == 0) {
                $party->addresses()->create([
                    'address_line_1' => 'Suite #' . rand(100, 999),
                    'address_line_2' => 'Business Park',
                    'city' => 'Mumbai',
                    'state' => 'Maharashtra',
                    'country' => 'India',
                    'zip_code' => '4000' . rand(10, 99),
                ]);
            }
        }
    }
}
