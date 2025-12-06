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
        Schema::table('accounts', function (Blueprint $table) {
            // Tax application level: where user selects tax in UI
            // - 'account': Uses account's default_tax_id for all items
            // - 'bill': Tax picker at transaction header, applies to all items
            // - 'item': Tax picker per item row (existing behavior)
            $table->enum('tax_application_level', ['account', 'bill', 'item'])
                ->default('item')
                ->after('default_tax_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('accounts', function (Blueprint $table) {
            $table->dropColumn('tax_application_level');
        });
    }
};
