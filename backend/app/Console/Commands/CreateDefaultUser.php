<?php

namespace App\Console\Commands;

use App\Models\User;
use App\Models\Account;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;

class CreateDefaultUser extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'user:create-default';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Create the default test user and account if they do not exist';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $email = 'test@wyco.in';
        $password = 'pass';

        $this->info('Checking for default user...');

        $user = User::where('email', $email)->first();

        if (!$user) {
            $user = User::create([
                'name' => 'Test User',
                'email' => $email,
                'password' => Hash::make($password), // Using Hash::make() which uses bcrypt by default
            ]);
            $this->info("User created: {$email}");
        } else {
            $this->info("User already exists: {$email}");
            // Optional: Update password if needed
            // $user->update(['password' => Hash::make($password)]);
        }

        // Ensure default account exists
        $accountCheck = Account::where('name', 'Demo Company')->first();
        if (!$accountCheck) {
            $account = Account::create([
                'name' => 'Demo Company',
                'name_formatted' => 'DEMO COMPANY',
                'desc' => 'Default demo account for testing',
                'taxation_type' => 2,
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
            $this->info("Default Account created.");

            // Link user to account
            if (!$user->accounts()->where('account_id', $account->id)->exists()) {
                $user->accounts()->attach($account->id, [
                    'role' => 'admin',
                    'created_at' => now(),
                    'updated_at' => now(),
                ]);
                $this->info("User attached to account.");
            }

            $user->update(['current_account_id' => $account->id]);
        } else {
            $this->info("Default Account already exists.");
            if (!$user->current_account_id) {
                $user->update(['current_account_id' => $accountCheck->id]);
                $this->info("User current_account_id updated.");
            }
        }

        $this->info("Done! Credentials:");
        $this->info("Email: {$email}");
        $this->info("Password: {$password}");
    }
}
