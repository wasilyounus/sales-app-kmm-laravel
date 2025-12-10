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
            $table->dropColumn('gst');
        });

        Schema::table('parties', function (Blueprint $table) {
            if (!Schema::hasColumn('parties', 'tax_number')) {
                $table->string('tax_number', 255)->nullable();
            }
            $table->dropColumn('gst');
        });

        Schema::table('transports', function (Blueprint $table) {
            if (!Schema::hasColumn('transports', 'tax_number')) {
                $table->string('tax_number', 255)->nullable();
            }
            $table->dropColumn('gst');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('accounts', function (Blueprint $table) {
            $table->string('gst', 255)->nullable();
        });

        Schema::table('parties', function (Blueprint $table) {
            $table->string('gst', 255)->nullable();
            if (Schema::hasColumn('parties', 'tax_number')) {
                $table->dropColumn('tax_number');
            }
        });

        Schema::table('transports', function (Blueprint $table) {
            $table->string('gst', 255)->nullable();
            if (Schema::hasColumn('transports', 'tax_number')) {
                $table->dropColumn('tax_number');
            }
        });
    }
};
