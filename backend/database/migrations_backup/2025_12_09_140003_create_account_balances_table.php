<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('account_balances', function (Blueprint $table) {
            $table->id();
            $table->foreignId('coa_id')->constrained('chart_of_accounts')->onDelete('cascade');
            $table->foreignId('account_id')->constrained('accounts')->onDelete('cascade');

            // Period tracking
            $table->integer('period_year')->comment('e.g., 2025');
            $table->integer('period_month')->comment('1-12');

            // Balances for the period
            $table->decimal('opening_balance', 15, 2)->default(0)->comment('Balance at start of period');
            $table->decimal('debit_total', 15, 2)->default(0)->comment('Total debits during period');
            $table->decimal('credit_total', 15, 2)->default(0)->comment('Total credits during period');
            $table->decimal('closing_balance', 15, 2)->default(0)->comment('Balance at end of period');

            $table->timestamps();

            // Unique constraint: one record per account per period
            $table->unique(['coa_id', 'account_id', 'period_year', 'period_month'], 'unique_account_period');

            // Indexes
            $table->index(['account_id', 'period_year', 'period_month']);
            $table->index(['coa_id', 'period_year', 'period_month']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('account_balances');
    }
};
