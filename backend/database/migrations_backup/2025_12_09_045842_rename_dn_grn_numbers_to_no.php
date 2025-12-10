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
        Schema::table('delivery_notes', function (Blueprint $table) {
            $table->renameColumn('dn_number', 'dn_no');
        });

        Schema::table('grns', function (Blueprint $table) {
            $table->renameColumn('grn_number', 'grn_no');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('delivery_notes', function (Blueprint $table) {
            $table->renameColumn('dn_no', 'dn_number');
        });

        Schema::table('grns', function (Blueprint $table) {
            $table->renameColumn('grn_no', 'grn_number');
        });
    }
};
