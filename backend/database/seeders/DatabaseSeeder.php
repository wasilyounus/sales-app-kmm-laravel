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
        // User::factory(10)->create();

        User::factory()->create([
            'name' => 'Test User',
            'email' => 'test@wyco.in',
            'password' => 'pass'
        ]);

        \App\Models\Account::create([
            'id' => 1,
            'name' => 'Default Account',
            'name_formatted' => 'Default Account',
            'desc' => 'Default Account Description',
            'taxation_type' => 'GST',
            'default_tax' => 0,
            'gst' => 'GST123',
            'address' => 'Default Address',
            'call' => '1234567890',
            'whatsapp' => '1234567890',
            'footer_content' => 'Footer',
            'signature' => false
        ]);

        $this->call([
            UqcSeeder::class,
        ]);
    }
}
