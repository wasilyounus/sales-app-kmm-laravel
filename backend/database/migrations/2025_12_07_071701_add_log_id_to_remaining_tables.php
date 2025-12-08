<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        $tables = [
            'users',
            'price_lists',
            'price_list_items',
            'user_account_permissions',
            'crashes',
            'updates'
        ];

        foreach ($tables as $table) {
            Schema::table($table, function (Blueprint $table) {
                if (!Schema::hasColumn($table->getTable(), 'log_id')) {
                    $table->integer('log_id')->nullable();
                }
            });
        }
    }

    public function down(): void
    {
        $tables = [
            'users',
            'price_lists',
            'price_list_items',
            'user_account_permissions',
            'crashes',
            'updates'
        ];

        foreach ($tables as $table) {
            if (Schema::hasColumn($table, 'log_id')) {
                Schema::table($table, function (Blueprint $table) {
                    $table->dropColumn('log_id');
                });
            }
        }
    }
};
