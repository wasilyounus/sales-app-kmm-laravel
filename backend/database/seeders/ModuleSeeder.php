<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class ModuleSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $modules = [
            // Inventory Management
            [
                'name' => 'Items',
                'slug' => 'items',
                'description' => 'Manage your product catalog',
                'icon' => 'Inventory',
                'bg_color' => '#6750A4', // Purple
                'color' => '#FFFFFF',
                'sort_order' => 10,
            ],
            [
                'name' => 'Inventory',
                'slug' => 'inventory',
                'description' => 'Track stock levels',
                'icon' => 'Warehouse',
                'bg_color' => '#8E6FCC', // Light Violet
                'color' => '#FFFFFF',
                'sort_order' => 20,
            ],
            [
                'name' => 'Prices',
                'slug' => 'prices',
                'description' => 'Manage pricing',
                'icon' => 'AttachMoney',
                'bg_color' => '#B39DDB', // Pale Violet
                'color' => '#FFFFFF',
                'sort_order' => 30,
            ],

            // Customer Relationship
            [
                'name' => 'Parties',
                'slug' => 'parties',
                'description' => 'Manage customers and suppliers',
                'icon' => 'People',
                'bg_color' => '#006A6A', // Teal
                'color' => '#FFFFFF',
                'sort_order' => 40,
            ],
            [
                'name' => 'Payments',
                'slug' => 'payments',
                'description' => 'Track payments',
                'icon' => 'Payment',
                'bg_color' => '#00897B', // Teal Green
                'color' => '#FFFFFF',
                'sort_order' => 50,
            ],

            // Transactions
            [
                'name' => 'Quotes',
                'slug' => 'quotes',
                'description' => 'Create and manage quotes',
                'icon' => 'RequestQuote',
                'bg_color' => '#8E4585', // Plum
                'color' => '#FFFFFF',
                'sort_order' => 60,
            ],
            [
                'name' => 'Orders',
                'slug' => 'orders',
                'description' => 'Manage orders',
                'icon' => 'ShoppingCart',
                'bg_color' => '#AB47BC', // Purple/Magenta
                'color' => '#FFFFFF',
                'sort_order' => 70,
            ],
            [
                'name' => 'Sales',
                'slug' => 'sales',
                'description' => 'Record sales',
                'icon' => 'TrendingUp',
                'bg_color' => '#C1689B', // Rose
                'color' => '#FFFFFF',
                'sort_order' => 80,
            ],
            [
                'name' => 'Purchases',
                'slug' => 'purchases',
                'description' => 'Track purchases',
                'icon' => 'ShoppingBag',
                'bg_color' => '#AA5A98', // Orchid
                'color' => '#FFFFFF',
                'sort_order' => 90,
            ],
            [
                'name' => 'Transfers',
                'slug' => 'transfers',
                'description' => 'Stock transfers',
                'icon' => 'MoveDown',
                'bg_color' => '#9C4699', // Violet
                'color' => '#FFFFFF',
                'sort_order' => 100,
            ],
            [
                'name' => 'Delivery Notes',
                'slug' => 'delivery-notes',
                'description' => 'Manage delivery notes',
                'icon' => 'LocalShipping',
                'bg_color' => '#EF6C00', // Orange
                'color' => '#FFFFFF',
                'sort_order' => 110,
            ],
            [
                'name' => 'GRNs',
                'slug' => 'grns',
                'description' => 'Goods Received Notes',
                'icon' => 'Inventory2',
                'bg_color' => '#2E7D32', // Dark Green
                'color' => '#FFFFFF',
                'sort_order' => 120,
            ],
            [
                'name' => 'Sync',
                'slug' => 'sync',
                'description' => 'Synchronize with server',
                'icon' => 'Sync',
                'bg_color' => '#5F6368', // Grey
                'color' => '#FFFFFF',
                'sort_order' => 130,
            ],
        ];

        foreach ($modules as $module) {
            \App\Models\Module::updateOrCreate(
                ['slug' => $module['slug']],
                $module
            );
        }
    }
}
