<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        // Add timestamps to all transaction tables and master data tables
        $tables = [
            'sales',
            'sale_items',
            'quotes',
            'quote_items',
            'orders',
            'order_items',
            'purchases',
            'purchase_items',
            'taxes',
            'uqcs',
            'items',
            'parties',
            'addresses'
        ];

        foreach ($tables as $table) {
            Schema::table($table, function (Blueprint $table) {
                if (!Schema::hasColumn($table->getTable(), 'created_at')) {
                    $table->timestamps();
                }
            });
        }
    }

    public function down(): void
    {
        $tables = [
            'sales',
            'sale_items',
            'quotes',
            'quote_items',
            'orders',
            'order_items',
            'purchases',
            'purchase_items',
            'taxes',
            'uqcs',
            'items',
            'parties',
            'addresses'
        ];

        foreach ($tables as $table) {
            Schema::table($table, function (Blueprint $table) {
                $table->dropTimestamps();
            });
        }
    }
};
