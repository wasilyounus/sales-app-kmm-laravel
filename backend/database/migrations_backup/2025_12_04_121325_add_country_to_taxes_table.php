<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('taxes', function (Blueprint $table) {
            $table->string('country')->nullable()->after('active');
        });

        // Populate country for existing tax records based on scheme name
        // India GST schemes
        DB::table('taxes')
            ->where('scheme_name', 'LIKE', 'India%')
            ->update(['country' => 'India']);

        // Saudi VAT schemes
        DB::table('taxes')
            ->where('scheme_name', 'LIKE', 'Saudi%')
            ->update(['country' => 'Saudi Arabia']);

        // UAE VAT schemes
        DB::table('taxes')
            ->where('scheme_name', 'LIKE', 'UAE%')
            ->update(['country' => 'UAE']);

        // Qatar VAT schemes
        DB::table('taxes')
            ->where('scheme_name', 'LIKE', 'Qatar%')
            ->update(['country' => 'Qatar']);

        // "No Tax" scheme stays null (will show for all countries)
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('taxes', function (Blueprint $table) {
            $table->dropColumn('country');
        });
    }
};
