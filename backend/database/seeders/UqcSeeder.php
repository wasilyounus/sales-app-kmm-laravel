<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Uqc;

class UqcSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $uqcs = [
            ['code' => 'BAG', 'name' => 'Bags'],
            ['code' => 'BDL', 'name' => 'Bundles'],
            ['code' => 'BOX', 'name' => 'Box'],
            ['code' => 'NOS', 'name' => 'Numbers'],
            ['code' => 'PAC', 'name' => 'Packs'],
            ['code' => 'PCS', 'name' => 'Pieces'],
            ['code' => 'MTR', 'name' => 'Meters'],
            ['code' => 'KGS', 'name' => 'Kilograms'],
            ['code' => 'OTH', 'name' => 'Others'],
            ['code' => 'LTR', 'name' => 'Liters'],
            ['code' => 'DOZ', 'name' => 'Dozen'],
            ['code' => 'GMS', 'name' => 'Grams'],
            ['code' => 'SET', 'name' => 'Sets'],
        ];

        foreach ($uqcs as $uqc) {
            Uqc::firstOrCreate(
                ['code' => $uqc['code']],
                $uqc
            );
        }
    }
}
