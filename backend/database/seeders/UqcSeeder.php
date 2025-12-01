<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use App\Models\Uqc;

class UqcSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $data = [
            ['id' => 1,  'quantity' => 'Bag',               'type' => 'Measure', 'uqc' => 'BAG', 'active' => true],
            ['id' => 2,  'quantity' => 'Bundles',           'type' => 'Measure', 'uqc' => 'BDL', 'active' => true],
            ['id' => 3,  'quantity' => 'Bale',              'type' => 'Measure', 'uqc' => 'BAL', 'active' => false],
            ['id' => 4,  'quantity' => 'Buckles',           'type' => 'Measure', 'uqc' => 'BKL', 'active' => false],
            ['id' => 5,  'quantity' => 'Billions Of Units', 'type' => 'Measure', 'uqc' => 'BOU', 'active' => false],
            ['id' => 6,  'quantity' => 'Box',               'type' => 'Measure', 'uqc' => 'BOX', 'active' => true],
            ['id' => 7,  'quantity' => 'Bottles',           'type' => 'Measure', 'uqc' => 'BTL', 'active' => false],
            ['id' => 8,  'quantity' => 'Bunches',           'type' => 'Measure', 'uqc' => 'BUN', 'active' => false],
            ['id' => 9,  'quantity' => 'Cans',              'type' => 'Measure', 'uqc' => 'CAN', 'active' => false],
            ['id' => 10, 'quantity' => 'Cartons',           'type' => 'Measure', 'uqc' => 'CTN', 'active' => false],
            ['id' => 11, 'quantity' => 'Dozen',             'type' => 'Measure', 'uqc' => 'DOZ', 'active' => false],
            ['id' => 12, 'quantity' => 'Drum',              'type' => 'Measure', 'uqc' => 'DRM', 'active' => false],
            ['id' => 13, 'quantity' => 'Great Gross',       'type' => 'Measure', 'uqc' => 'GGR', 'active' => false],
            ['id' => 14, 'quantity' => 'Gross',             'type' => 'Measure', 'uqc' => 'GRS', 'active' => false],
            ['id' => 15, 'quantity' => 'Numbers',           'type' => 'Measure', 'uqc' => 'NOS', 'active' => true],
            ['id' => 16, 'quantity' => 'Packs',             'type' => 'Measure', 'uqc' => 'PAC', 'active' => true],
            ['id' => 17, 'quantity' => 'Pieces',            'type' => 'Measure', 'uqc' => 'PCS', 'active' => true],
            ['id' => 18, 'quantity' => 'Pairs',             'type' => 'Measure', 'uqc' => 'PRS', 'active' => false],
            ['id' => 19, 'quantity' => 'Rolls',             'type' => 'Measure', 'uqc' => 'ROL', 'active' => false],
            ['id' => 20, 'quantity' => 'Sets',              'type' => 'Measure', 'uqc' => 'SET', 'active' => false],
            ['id' => 21, 'quantity' => 'Tablets',           'type' => 'Measure', 'uqc' => 'TBS', 'active' => false],
            ['id' => 22, 'quantity' => 'Ten Gross',         'type' => 'Measure', 'uqc' => 'TGM', 'active' => false],
            ['id' => 23, 'quantity' => 'Thousands',         'type' => 'Measure', 'uqc' => 'THD', 'active' => false],
            ['id' => 24, 'quantity' => 'Tubes',             'type' => 'Measure', 'uqc' => 'TUB', 'active' => false],
            ['id' => 25, 'quantity' => 'Units',             'type' => 'Measure', 'uqc' => 'UNT', 'active' => false],
            ['id' => 26, 'quantity' => 'Cubic Meter',      'type' => 'Volume',  'uqc' => 'CBM', 'active' => false],
            ['id' => 27, 'quantity' => 'Cubic Centimeter', 'type' => 'Volume',  'uqc' => 'CCM', 'active' => false],
            ['id' => 28, 'quantity' => 'Kilo Liter',       'type' => 'Volume',  'uqc' => 'KLR', 'active' => false],
            ['id' => 29, 'quantity' => 'Milliliter',       'type' => 'Volume',  'uqc' => 'MLT', 'active' => false],
            ['id' => 30, 'quantity' => 'US Gallons',       'type' => 'Volume',  'uqc' => 'UGS', 'active' => false],
            ['id' => 31, 'quantity' => 'Square Feet',      'type' => 'Area',    'uqc' => 'SQF', 'active' => false],
            ['id' => 32, 'quantity' => 'Square Meters',    'type' => 'Area',    'uqc' => 'SQM', 'active' => false],
            ['id' => 33, 'quantity' => 'Square Yards',     'type' => 'Area',    'uqc' => 'SQY', 'active' => false],
            ['id' => 34, 'quantity' => 'Gross Yards',      'type' => 'Length',  'uqc' => 'GYD', 'active' => false],
            ['id' => 35, 'quantity' => 'Kilo Meter',       'type' => 'Length',  'uqc' => 'KME', 'active' => false],
            ['id' => 36, 'quantity' => 'Meters',           'type' => 'Length',  'uqc' => 'MTR', 'active' => true],
            ['id' => 37, 'quantity' => 'Yards',            'type' => 'Length',  'uqc' => 'YDS', 'active' => false],
            ['id' => 38, 'quantity' => 'Centimeter',       'type' => 'Length',  'uqc' => 'CMS', 'active' => false],
            ['id' => 39, 'quantity' => 'Tonnes',           'type' => 'Weight',  'uqc' => 'TON', 'active' => false],
            ['id' => 40, 'quantity' => 'Quintal',          'type' => 'Weight',  'uqc' => 'QTL', 'active' => false],
            ['id' => 41, 'quantity' => 'Grams',            'type' => 'Weight',  'uqc' => 'GMS', 'active' => false],
            ['id' => 42, 'quantity' => 'Kilo Grams',       'type' => 'Weight',  'uqc' => 'KGS', 'active' => true],
            ['id' => 43, 'quantity' => 'Others',           'type' => '',        'uqc' => 'OTH', 'active' => true],
        ];

        foreach ($data as $row) {
            Uqc::updateOrCreate(['id' => $row['id']], $row);
        }
    }
}
