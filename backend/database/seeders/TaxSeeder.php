<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Tax;

class TaxSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $schemes = [
            [
                'id' => 1,
                'scheme_name' => 'No Tax',
                'tax1_name' => null,
                'tax1_val' => 0,
                'tax2_name' => null,
                'tax2_val' => 0,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            // India GST rates
            [
                'id' => 2,
                'scheme_name' => 'India GST 5%',
                'tax1_name' => 'CGST',
                'tax1_val' => 2.5,
                'tax2_name' => 'SGST',
                'tax2_val' => 2.5,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            [
                'id' => 3,
                'scheme_name' => 'India GST 12%',
                'tax1_name' => 'CGST',
                'tax1_val' => 6,
                'tax2_name' => 'SGST',
                'tax2_val' => 6,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            [
                'id' => 4,
                'scheme_name' => 'India GST 18%',
                'tax1_name' => 'CGST',
                'tax1_val' => 9,
                'tax2_name' => 'SGST',
                'tax2_val' => 9,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            [
                'id' => 5,
                'scheme_name' => 'India GST 28%',
                'tax1_name' => 'CGST',
                'tax1_val' => 14,
                'tax2_name' => 'SGST',
                'tax2_val' => 14,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            // Saudi Arabia VAT
            [
                'id' => 6,
                'scheme_name' => 'Saudi VAT 15%',
                'tax1_name' => 'VAT',
                'tax1_val' => 15,
                'tax2_name' => null,
                'tax2_val' => 0,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            // UAE VAT
            [
                'id' => 7,
                'scheme_name' => 'UAE VAT 5%',
                'tax1_name' => 'VAT',
                'tax1_val' => 5,
                'tax2_name' => null,
                'tax2_val' => 0,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
            // Qatar VAT
            [
                'id' => 8,
                'scheme_name' => 'Qatar VAT 0%',
                'tax1_name' => 'VAT',
                'tax1_val' => 0,
                'tax2_name' => null,
                'tax2_val' => 0,
                'tax3_name' => null,
                'tax3_val' => 0,
                'tax4_name' => null,
                'tax4_val' => 0,
                'active' => true,
                'log_id' => 1,
            ],
        ];

        foreach ($schemes as $scheme) {
            Tax::updateOrCreate(
                ['id' => $scheme['id']],
                $scheme
            );
        }
    }
}
