<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('chart_of_accounts', function (Blueprint $table) {
            $table->id();
            $table->string('account_code', 20)->unique()->comment('e.g., 1000, 1100, 5210');
            $table->string('account_name', 100)->comment('e.g., Assets, Cash, Petrol Expense');
            $table->enum('account_type', ['ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE']);
            $table->enum('normal_balance', ['DEBIT', 'CREDIT'])->comment('Natural balance side');
            $table->foreignId('parent_account_id')->nullable()->constrained('chart_of_accounts')->onDelete('restrict');
            $table->boolean('is_active')->default(true);
            $table->boolean('is_system')->default(false)->comment('System accounts cannot be deleted');
            $table->text('description')->nullable();
            $table->foreignId('account_id')->constrained('accounts')->onDelete('cascade');
            $table->timestamps();
            $table->softDeletes();

            // Indexes
            $table->index(['account_id', 'account_type']);
            $table->index('account_code');
            $table->index('is_active');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('chart_of_accounts');
    }
};
