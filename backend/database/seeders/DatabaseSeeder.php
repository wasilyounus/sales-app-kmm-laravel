<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Create default user
        $user = User::factory()->create([
            'name' => 'Test User',
            'email' => 'test@wyco.in',
            'password' => 'pass'
        ]);

        // Create default account
        $account = \App\Models\Account::create([
            'name' => 'Demo Company',
            'name_formatted' => 'DEMO COMPANY',
            'desc' => 'Default demo account for testing',
            'taxation_type' => 2,
            'tax_country' => 'India',
            'country' => 'India',
            'state' => 'Maharashtra',
            'tax_number' => '27AAAAA0000A1Z5',
            'address' => '123 Demo Street, Mumbai, Maharashtra',
            'call' => '+91 9876543210',
            'whatsapp' => '+91 9876543210',
            'footer_content' => 'Thank you for your business!',
            'signature' => true,
            'financial_year_start' => now()->format('Y-m-d H:i:s'),
            'visibility' => 'public',
            'log_id' => 1,
        ]);

        // Assign account to user with admin role
        $user->accounts()->attach($account->id, [
            'role' => 'admin',
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        // Set as current account for user
        $user->update(['current_account_id' => $account->id]);

        // Seed taxes and UQCs
        $this->call([
            TaxSeeder::class,
            UqcSeeder::class,
        ]);

        $this->command->info('✓ Created default user: test@wyco.in (password: pass)');
        $this->command->info('✓ Created default account: Demo Company');
        $this->command->info('✓ Assigned account to user with admin role');
        $this->command->info('✓ Seeded taxes and UQCs');
    }
}
