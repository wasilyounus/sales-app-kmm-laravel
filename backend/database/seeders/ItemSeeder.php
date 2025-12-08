<?php

namespace Database\Seeders;

use App\Models\Item;
use App\Models\Tax;
use Illuminate\Database\Seeder;

class ItemSeeder extends Seeder
{
    public function run(): void
    {
        // Get first active tax for default
        $defaultTax = Tax::where('active', true)->first();
        $taxId = $defaultTax?->id;

        // Get Demo Account or default to 1
        $account = \App\Models\Account::where('name', 'Demo Company')->first();
        $accountId = $account ? $account->id : 1;
        
        // Create UQC lookup map
        $uqcs = \App\Models\Uqc::pluck('id', 'uqc')->toArray();

        $items = [
            // Electronics
            [
                'name' => 'LED Monitor 24"',
                'alt_name' => 'Dell Monitor',
                'brand' => 'Dell',
                'size' => '24 inch',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8528',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Wireless Keyboard',
                'alt_name' => 'Logitech K380',
                'brand' => 'Logitech',
                'size' => 'Standard',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8471',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'USB Mouse',
                'alt_name' => 'HP Mouse',
                'brand' => 'HP',
                'size' => 'Standard',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8471',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            // Office Supplies
            [
                'name' => 'A4 Paper Ream',
                'alt_name' => 'JK Paper 500 sheets',
                'brand' => 'JK Paper',
                'size' => '500 sheets',
                'uqc' => $uqcs['PAC'] ?? 16,
                'hsn' => '4802',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Ballpoint Pen Box',
                'alt_name' => 'Reynolds Blue',
                'brand' => 'Reynolds',
                'size' => '10 pens',
                'uqc' => $uqcs['BOX'] ?? 6,
                'hsn' => '9608',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Stapler',
                'alt_name' => 'Kangaro Stapler',
                'brand' => 'Kangaro',
                'size' => 'Medium',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8305',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            // Hardware
            [
                'name' => 'Steel Pipe 1"',
                'alt_name' => 'GI Pipe',
                'brand' => 'Tata Steel',
                'size' => '1 inch x 6 ft',
                'uqc' => $uqcs['MTR'] ?? 36,
                'hsn' => '7306',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Cement Bag',
                'alt_name' => 'UltraTech 50kg',
                'brand' => 'UltraTech',
                'size' => '50 kg',
                'uqc' => $uqcs['BAG'] ?? 1,
                'hsn' => '2523',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Paint Bucket',
                'alt_name' => 'Asian Paints White',
                'brand' => 'Asian Paints',
                'size' => '20 Ltr',
                'uqc' => $uqcs['LTR'] ?? 44, // Assuming LTR might map to Others or specific if exists, fallback? UqcSeeder didn't have LTR explicitly in the snippet I saw unless I missed it. Wait, UqcSeeder had KLR, MLT. LTR is often mapped to Others or MLT*1000. Let's check UqcSeeder again. 
                // UqcSeeder content:
                // ... 'uqc' => 'KLR' ... 'MLT' ...
                // It does NOT have 'LTR'. It has 'KLR' (Kilo Liter) and 'MLT' (Milliliter). 
                // Use 'OTH' (43) or create a new one? The user's previous error showed 'LTR' in the code.
                // The error was "Unexpected symbol 'L' in numeric literal" when 'LTR' was passed? No, the error was "Unexpected symbol 'N' in numeric literal" from "NOS".
                // I will use 'OTH' (43) for 'LTR' if it's not in the map, to be safe.
                'hsn' => '3208',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'PVC Wire 1.5mm',
                'alt_name' => 'Havells Wire Red',
                'brand' => 'Havells',
                'size' => '90 mtr coil',
                'uqc' => $uqcs['MTR'] ?? 36,
                'hsn' => '8544',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            // General
            [
                'name' => 'LED Bulb 9W',
                'alt_name' => 'Philips LED',
                'brand' => 'Philips',
                'size' => '9 Watt',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8539',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Extension Cord',
                'alt_name' => 'Anchor Extension 4 socket',
                'brand' => 'Anchor',
                'size' => '5 mtr',
                'uqc' => $uqcs['NOS'] ?? 15,
                'hsn' => '8536',
                'account_id' => $accountId,
                'tax_id' => $taxId,
            ],
        ];

        foreach ($items as $item) {
            Item::firstOrCreate(
                ['name' => $item['name'], 'account_id' => $item['account_id']],
                $item
            );
        }
    }
}
