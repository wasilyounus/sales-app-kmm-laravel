<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Add tax_id to parent transaction tables for bill-level tax selection.
     * Safely checks if column already exists before adding.
     */
    public function up(): void
    {
        // Add tax_id to quotes table if not exists
        if (!Schema::hasColumn('quotes', 'tax_id')) {
            Schema::table('quotes', function (Blueprint $table) {
                $table->foreignId('tax_id')->nullable()->after('party_id')->constrained('taxes')->nullOnDelete();
            });
        }

        // Add tax_id to sales table if not exists
        if (!Schema::hasColumn('sales', 'tax_id')) {
            Schema::table('sales', function (Blueprint $table) {
                $table->foreignId('tax_id')->nullable()->after('party_id')->constrained('taxes')->nullOnDelete();
            });
        }

        // Add tax_id to purchases table if not exists
        if (!Schema::hasColumn('purchases', 'tax_id')) {
            Schema::table('purchases', function (Blueprint $table) {
                $table->foreignId('tax_id')->nullable()->after('party_id')->constrained('taxes')->nullOnDelete();
            });
        }

        // Add tax_id to orders table if not exists
        if (!Schema::hasColumn('orders', 'tax_id')) {
            Schema::table('orders', function (Blueprint $table) {
                $table->foreignId('tax_id')->nullable()->after('party_id')->constrained('taxes')->nullOnDelete();
            });
        }
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        if (Schema::hasColumn('quotes', 'tax_id')) {
            Schema::table('quotes', function (Blueprint $table) {
                $table->dropForeign(['tax_id']);
                $table->dropColumn('tax_id');
            });
        }

        if (Schema::hasColumn('sales', 'tax_id')) {
            Schema::table('sales', function (Blueprint $table) {
                $table->dropForeign(['tax_id']);
                $table->dropColumn('tax_id');
            });
        }

        if (Schema::hasColumn('purchases', 'tax_id')) {
            Schema::table('purchases', function (Blueprint $table) {
                $table->dropForeign(['tax_id']);
                $table->dropColumn('tax_id');
            });
        }

        if (Schema::hasColumn('orders', 'tax_id')) {
            Schema::table('orders', function (Blueprint $table) {
                $table->dropForeign(['tax_id']);
                $table->dropColumn('tax_id');
            });
        }
    }
};
