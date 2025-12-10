<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Tax;

class TaxSeeder extends Seeder
{
    /**
     * Run the database seeds.
     * 
     * Taxes are UNIVERSAL (system-level), not company-specific.
     * Government tax rates apply to all companies in that country.
     * 
     * Supports: India (with GST combinations), UAE, Saudi Arabia, Qatar, Oman, Bahrain
     */
    public function run(): void
    {
        $taxes = [
            // Global/No Tax
            [
                'name' => 'No Tax',
                'rate' => 0.00,
                'country' => null,
                'is_default' => false,
            ],

            // ========================================
            // INDIA GST - Proper Combinations
            // ========================================

            // GST 5% - Intrastate (CGST 2.5% + SGST 2.5%)
            [
                'name' => 'GST 5% (CGST 2.5% + SGST 2.5%)',
                'rate' => 5.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 5% - Interstate (IGST 5%)
            [
                'name' => 'GST 5% (IGST 5%)',
                'rate' => 5.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 5% - Union Territory (CGST 2.5% + UTGST 2.5%)
            [
                'name' => 'GST 5% (CGST 2.5% + UTGST 2.5%)',
                'rate' => 5.00,
                'country' => 'India',
                'is_default' => false,
            ],

            // GST 12% - Intrastate (CGST 6% + SGST 6%)
            [
                'name' => 'GST 12% (CGST 6% + SGST 6%)',
                'rate' => 12.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 12% - Interstate (IGST 12%)
            [
                'name' => 'GST 12% (IGST 12%)',
                'rate' => 12.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 12% - Union Territory (CGST 6% + UTGST 6%)
            [
                'name' => 'GST 12% (CGST 6% + UTGST 6%)',
                'rate' => 12.00,
                'country' => 'India',
                'is_default' => false,
            ],

            // GST 18% - Intrastate (CGST 9% + SGST 9%) - MOST COMMON
            [
                'name' => 'GST 18% (CGST 9% + SGST 9%)',
                'rate' => 18.00,
                'country' => 'India',
                'is_default' => true, // Most common in India
            ],
            // GST 18% - Interstate (IGST 18%)
            [
                'name' => 'GST 18% (IGST 18%)',
                'rate' => 18.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 18% - Union Territory (CGST 9% + UTGST 9%)
            [
                'name' => 'GST 18% (CGST 9% + UTGST 9%)',
                'rate' => 18.00,
                'country' => 'India',
                'is_default' => false,
            ],

            // GST 28% - Intrastate (CGST 14% + SGST 14%)
            [
                'name' => 'GST 28% (CGST 14% + SGST 14%)',
                'rate' => 28.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 28% - Interstate (IGST 28%)
            [
                'name' => 'GST 28% (IGST 28%)',
                'rate' => 28.00,
                'country' => 'India',
                'is_default' => false,
            ],
            // GST 28% - Union Territory (CGST 14% + UTGST 14%)
            [
                'name' => 'GST 28% (CGST 14% + UTGST 14%)',
                'rate' => 28.00,
                'country' => 'India',
                'is_default' => false,
            ],

            // ========================================
            // UAE VAT
            // ========================================
            [
                'name' => 'VAT 5%',
                'rate' => 5.00,
                'country' => 'UAE',
                'is_default' => true,
            ],
            [
                'name' => 'VAT 0% (Zero-rated)',
                'rate' => 0.00,
                'country' => 'UAE',
                'is_default' => false,
            ],

            // ========================================
            // SAUDI ARABIA VAT
            // ========================================
            [
                'name' => 'VAT 15%',
                'rate' => 15.00,
                'country' => 'Saudi Arabia',
                'is_default' => true,
            ],
            [
                'name' => 'VAT 0% (Zero-rated)',
                'rate' => 0.00,
                'country' => 'Saudi Arabia',
                'is_default' => false,
            ],

            // ========================================
            // QATAR VAT
            // ========================================
            [
                'name' => 'VAT 0%',
                'rate' => 0.00,
                'country' => 'Qatar',
                'is_default' => true,
            ],

            // ========================================
            // OMAN VAT
            // ========================================
            [
                'name' => 'VAT 5%',
                'rate' => 5.00,
                'country' => 'Oman',
                'is_default' => true,
            ],
            [
                'name' => 'VAT 0% (Zero-rated)',
                'rate' => 0.00,
                'country' => 'Oman',
                'is_default' => false,
            ],

            // ========================================
            // BAHRAIN VAT
            // ========================================
            [
                'name' => 'VAT 10%',
                'rate' => 10.00,
                'country' => 'Bahrain',
                'is_default' => true,
            ],
            [
                'name' => 'VAT 0% (Zero-rated)',
                'rate' => 0.00,
                'country' => 'Bahrain',
                'is_default' => false,
            ],
        ];

        foreach ($taxes as $tax) {
            Tax::firstOrCreate(
                [
                    'name' => $tax['name'],
                    'country' => $tax['country'],
                ],
                $tax
            );
        }
    }
}
