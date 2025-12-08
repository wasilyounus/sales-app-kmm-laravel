<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Database\Seeders\ItemSeeder;
use Database\Seeders\PartySeeder;
use Database\Seeders\TaxSeeder;
use Database\Seeders\UqcSeeder;

class SeedSampleData extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'sample:create';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Seed sample items and parties';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $this->info('Seeding Taxes and UQCs...');
        $this->call(TaxSeeder::class);
        $this->call(UqcSeeder::class);

        $this->info('Seeding Sample Items...');
        $this->call(ItemSeeder::class);

        $this->info('Seeding Sample Parties...');
        $this->call(PartySeeder::class);

        $this->info('✓ Sample data created successfully.');
    }
}
