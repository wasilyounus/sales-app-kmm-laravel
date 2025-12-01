<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Account;
use App\Models\Log;

class AccountSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create a log entry first
        $log = Log::create([
            'user_id' => 1,
            'model' => 'Account',
            'model_id' => 1,
            'action' => 'created',
            'account_id' => null,
            'data' => json_encode(['name' => 'Default Account'])
        ]);

        // Create default account
        Account::create([
            'id' => 1,
            'name' => 'Default Account',
            'name_formatted' => 'Default Account',
            'desc' => 'Default account for testing',
            'taxation_type' => 1, // No Tax
            'gst' => null,
            'address' => null,
            'call' => null,
            'whatsapp' => null,
            'footer_content' => null,
            'signature' => false,
            'log_id' => $log->id,
            'financial_year_start' => null
        ]);
    }
}
