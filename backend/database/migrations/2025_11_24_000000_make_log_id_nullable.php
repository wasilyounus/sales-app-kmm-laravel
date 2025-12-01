<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        $tables = [
            'accounts', 'parties', 'addresses', 'uqcs', 'items', 'taxes',
            'quotes', 'quote_items', 'orders', 'order_items', 'sales', 'sale_items',
            'purchases', 'purchase_items', 'stocks', 'supplies', 'transports', 'transactions'
        ];

        foreach ($tables as $table) {
            if (Schema::hasTable($table)) {
                Schema::table($table, function (Blueprint $table) {
                    $table->integer('log_id')->nullable()->change();
                });
            }
        }
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        $tables = [
            'accounts', 'parties', 'addresses', 'uqcs', 'items', 'taxes',
            'quotes', 'quote_items', 'orders', 'order_items', 'sales', 'sale_items',
            'purchases', 'purchase_items', 'stocks', 'supplies', 'transports', 'transactions'
        ];

        foreach ($tables as $table) {
            if (Schema::hasTable($table)) {
                Schema::table($table, function (Blueprint $table) {
                    // We cannot easily revert to not null without ensuring data validity
                    // But we can try
                    $table->integer('log_id')->nullable(false)->change();
                });
            }
        }
    }
};
