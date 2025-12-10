<?php

namespace Database\Seeders;

use App\Models\User;
use App\Models\Company;
use App\Models\Location;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Create test user
        $user = User::create([
            'name' => 'Test User',
            'email' => 'test@wyco.in',
            'password' => Hash::make('pass'),
            'email_verified_at' => now(),
        ]);

        // Create first company
        $company1 = Company::create([
            'name' => 'Demo Company',
            'name_formatted' => 'DEMO COMPANY',
            'desc' => 'Default demo account for testing',
            'taxation_type' => '2',
            'address' => '123 Demo Street, Mumbai, Maharashtra',
            'call' => '+91 9876543210',
            'whatsapp' => '+91 9876543210',
            'footer_content' => 'Thank you for your business!',
            'signature' => true,
            'financial_year_start' => now(),
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5',
            'enable_delivery_notes' => false,
            'enable_grns' => false,
            'allow_negative_stock' => false,
        ]);

        // Create default location for first company
        Location::create([
            'company_id' => $company1->id,
            'name' => 'Main Warehouse',
            'type' => 'warehouse',
            'is_default' => true,
            'is_active' => true,
            'address' => $company1->address,
            'state' => $company1->state,
            'country' => $company1->country,
            'tax_number' => $company1->tax_number,
        ]);

        // Create second company
        $company2 = Company::create([
            'name' => 'Tech Solutions Inc',
            'name_formatted' => 'TECH SOLUTIONS INC',
            'desc' => 'Technology consulting and software development company',
            'taxation_type' => '3',
            'address' => '456 Tech Park, Bangalore, Karnataka',
            'call' => '+91 8765432109',
            'whatsapp' => '+91 8765432109',
            'footer_content' => 'Thank you for choosing Tech Solutions!',
            'signature' => true,
            'financial_year_start' => now(),
            'country' => 'India',
            'state' => 'Karnataka',
            'tax_number' => '29BBBBB0000B2Z6',
            'enable_delivery_notes' => true,
            'enable_grns' => true,
            'allow_negative_stock' => false,
        ]);

        // Create default location for second company
        Location::create([
            'company_id' => $company2->id,
            'name' => 'Main Warehouse',
            'type' => 'warehouse',
            'is_default' => true,
            'is_active' => true,
            'address' => $company2->address,
            'state' => $company2->state,
            'country' => $company2->country,
            'tax_number' => $company2->tax_number,
        ]);

        // Assign user to both companies with admin role
        DB::table('user_company_permissions')->insert([
            [
                'user_id' => $user->id,
                'company_id' => $company1->id,
                'role' => 'admin',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'user_id' => $user->id,
                'company_id' => $company2->id,
                'role' => 'admin',
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);

        // Set first company as current for the user
        $user->update(['current_company_id' => $company1->id]);

        // Always seed taxes and UQCs
        $this->call([
            TaxSeeder::class,
            UqcSeeder::class,
        ]);

        // Only seed sample Party and Item data in development environment
        if (app()->environment('local', 'development') || env('APP_ENV') === 'development') {
            $this->call([
                PartySeeder::class,
                ItemSeeder::class,
            ]);
            $this->command->info('✓ Seeded sample parties and items (dev only)');
        }

        $this->command->info('✓ Created default user: test@wyco.in (password: pass)');
        $this->command->info('✓ Created companies: Demo Company & Tech Solutions Inc');
        $this->command->info('✓ Created default locations for both companies');
        $this->command->info('✓ Assigned both companies to user with admin role');
        $this->command->info('✓ Seeded taxes and UQCs');
    }
}
