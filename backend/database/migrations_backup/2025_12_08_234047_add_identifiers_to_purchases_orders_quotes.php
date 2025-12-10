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
        Schema::table('purchases', function (Blueprint $table) {
            $table->string('invoice_no')->nullable()->after('date');
        });

        Schema::table('orders', function (Blueprint $table) {
            $table->string('order_no')->nullable()->after('date');
        });

        Schema::table('quotes', function (Blueprint $table) {
            $table->string('quote_no')->nullable()->after('date');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('purchases', function (Blueprint $table) {
            $table->dropColumn('invoice_no');
        });

        Schema::table('orders', function (Blueprint $table) {
            $table->dropColumn('order_no');
        });

        Schema::table('quotes', function (Blueprint $table) {
            $table->dropColumn('quote_no');
        });
    }
};
