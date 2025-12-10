<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        // Step 1: Rename the accounts table to companies
        Schema::rename('accounts', 'companies');

        // Step 2: Rename account_id to company_id in all tables

        // chart_of_accounts
        Schema::table('chart_of_accounts', function (Blueprint $table) {
            $table->renameColumn('account_id', 'company_id');
        });

        // journal_entries
        Schema::table('journal_entries', function (Blueprint $table) {
            $table->renameColumn('account_id', 'company_id');
        });

        // account_balances
        Schema::table('account_balances', function (Blueprint $table) {
            $table->renameColumn('account_id', 'company_id');
        });

        // user_account_permissions → user_company_permissions
        Schema::rename('user_account_permissions', 'user_company_permissions');
        Schema::table('user_company_permissions', function (Blueprint $table) {
            $table->renameColumn('account_id', 'company_id');
        });

        // All transaction tables
        $tables = [
            'parties',
            'items',
            'taxes',
            'quotes',
            'orders',
            'sales',
            'purchases',
            'stocks',
            'supplies',
            'transports',
            'transactions',
            'logs',
            'crashes',
            'price_lists',
            'delivery_notes',
            'grns',
        ];

        foreach ($tables as $tableName) {
            if (Schema::hasColumn($tableName, 'account_id')) {
                Schema::table($tableName, function (Blueprint $table) {
                    $table->renameColumn('account_id', 'company_id');
                });
            }
        }

        // users table: current_account_id → current_company_id
        if (Schema::hasColumn('users', 'current_account_id')) {
            Schema::table('users', function (Blueprint $table) {
                $table->renameColumn('current_account_id', 'current_company_id');
            });
        }
    }

    public function down(): void
    {
        // Reverse: companies → accounts
        Schema::rename('companies', 'accounts');

        // Reverse all column renames
        Schema::table('chart_of_accounts', function (Blueprint $table) {
            $table->renameColumn('company_id', 'account_id');
        });

        Schema::table('journal_entries', function (Blueprint $table) {
            $table->renameColumn('company_id', 'account_id');
        });

        Schema::table('account_balances', function (Blueprint $table) {
            $table->renameColumn('company_id', 'account_id');
        });

        Schema::rename('user_company_permissions', 'user_account_permissions');
        Schema::table('user_account_permissions', function (Blueprint $table) {
            $table->renameColumn('company_id', 'account_id');
        });

        $tables = [
            'parties',
            'items',
            'taxes',
            'quotes',
            'orders',
            'sales',
            'purchases',
            'stocks',
            'supplies',
            'transports',
            'transactions',
            'logs',
            'crashes',
            'price_lists',
            'delivery_notes',
            'grns',
        ];

        foreach ($tables as $tableName) {
            if (Schema::hasColumn($tableName, 'company_id')) {
                Schema::table($tableName, function (Blueprint $table) {
                    $table->renameColumn('company_id', 'account_id');
                });
            }
        }

        if (Schema::hasColumn('users', 'current_company_id')) {
            Schema::table('users', function (Blueprint $table) {
                $table->renameColumn('current_company_id', 'current_account_id');
            });
        }
    }
};
