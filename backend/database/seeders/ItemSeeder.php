<?php

namespace Database\Seeders;

use App\Models\Item;
use Illuminate\Database\Seeder;

class ItemSeeder extends Seeder
{
    public function run(): void
    {
        // Get Demo Company
        $company = \App\Models\Company::where('name', 'Demo Company')->first();
        $companyId = $company ? $company->id : 1;

        // Get UQCs by code
        $uqcs = \App\Models\Uqc::pluck('id', 'code');

        $items = [
            ['name' => 'LED Monitor 24"', 'hsn' => '8528', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 15000],
            ['name' => 'Wireless Keyboard', 'hsn' => '8471', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 1200],
            ['name' => 'USB Mouse', 'hsn' => '8471', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 500],
            ['name' => 'A4 Paper Ream', 'hsn' => '4802', 'uqc' => $uqcs['PAC'] ?? 1, 'price' => 250],
            ['name' => 'Ballpoint Pen Box', 'hsn' => '9608', 'uqc' => $uqcs['BOX'] ?? 1, 'price' => 100],
            ['name' => 'Stapler', 'hsn' => '8305', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 150],
            ['name' => 'Steel Pipe 1"', 'hsn' => '7306', 'uqc' => $uqcs['MTR'] ?? 1, 'price' => 200],
            ['name' => 'Cement Bag 50kg', 'hsn' => '2523', 'uqc' => $uqcs['BAG'] ?? 1, 'price' => 350],
            ['name' => 'Paint Bucket 20L', 'hsn' => '3208', 'uqc' => $uqcs['OTH'] ?? 1, 'price' => 2500],
            ['name' => 'PVC Wire 1.5mm', 'hsn' => '8544', 'uqc' => $uqcs['MTR'] ?? 1, 'price' => 15],
            ['name' => 'LED Bulb 9W', 'hsn' => '8539', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 80],
            ['name' => 'Extension Cord 5m', 'hsn' => '8536', 'uqc' => $uqcs['NOS'] ?? 1, 'price' => 300],
        ];

        foreach ($items as $itemData) {
            Item::firstOrCreate(
                ['name' => $itemData['name'], 'company_id' => $companyId],
                array_merge($itemData, ['company_id' => $companyId])
            );
        }
    }
}
