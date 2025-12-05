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

        $items = [
            // Electronics
            [
                'name' => 'LED Monitor 24"',
                'alt_name' => 'Dell Monitor',
                'brand' => 'Dell',
                'size' => '24 inch',
                'uqc' => 'NOS',
                'hsn' => '8528',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Wireless Keyboard',
                'alt_name' => 'Logitech K380',
                'brand' => 'Logitech',
                'size' => 'Standard',
                'uqc' => 'NOS',
                'hsn' => '8471',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'USB Mouse',
                'alt_name' => 'HP Mouse',
                'brand' => 'HP',
                'size' => 'Standard',
                'uqc' => 'NOS',
                'hsn' => '8471',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            // Office Supplies
            [
                'name' => 'A4 Paper Ream',
                'alt_name' => 'JK Paper 500 sheets',
                'brand' => 'JK Paper',
                'size' => '500 sheets',
                'uqc' => 'PAC',
                'hsn' => '4802',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Ballpoint Pen Box',
                'alt_name' => 'Reynolds Blue',
                'brand' => 'Reynolds',
                'size' => '10 pens',
                'uqc' => 'BOX',
                'hsn' => '9608',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Stapler',
                'alt_name' => 'Kangaro Stapler',
                'brand' => 'Kangaro',
                'size' => 'Medium',
                'uqc' => 'NOS',
                'hsn' => '8305',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            // Hardware
            [
                'name' => 'Steel Pipe 1"',
                'alt_name' => 'GI Pipe',
                'brand' => 'Tata Steel',
                'size' => '1 inch x 6 ft',
                'uqc' => 'MTR',
                'hsn' => '7306',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Cement Bag',
                'alt_name' => 'UltraTech 50kg',
                'brand' => 'UltraTech',
                'size' => '50 kg',
                'uqc' => 'BAG',
                'hsn' => '2523',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Paint Bucket',
                'alt_name' => 'Asian Paints White',
                'brand' => 'Asian Paints',
                'size' => '20 Ltr',
                'uqc' => 'LTR',
                'hsn' => '3208',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'PVC Wire 1.5mm',
                'alt_name' => 'Havells Wire Red',
                'brand' => 'Havells',
                'size' => '90 mtr coil',
                'uqc' => 'MTR',
                'hsn' => '8544',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            // General
            [
                'name' => 'LED Bulb 9W',
                'alt_name' => 'Philips LED',
                'brand' => 'Philips',
                'size' => '9 Watt',
                'uqc' => 'NOS',
                'hsn' => '8539',
                'account_id' => 1,
                'tax_id' => $taxId,
            ],
            [
                'name' => 'Extension Cord',
                'alt_name' => 'Anchor Extension 4 socket',
                'brand' => 'Anchor',
                'size' => '5 mtr',
                'uqc' => 'NOS',
                'hsn' => '8536',
                'account_id' => 1,
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
