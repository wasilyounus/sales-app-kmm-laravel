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
        // Chart of Accounts
        Schema::create('chart_of_accounts', function (Blueprint $table) {
            $table->id();
            $table->string('account_code', 20)->unique();
            $table->string('account_name', 100);
            $table->enum('account_type', ['ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE']);
            $table->enum('normal_balance', ['DEBIT', 'CREDIT']);
            $table->foreignId('parent_account_id')->nullable()->constrained('chart_of_accounts')->onDelete('restrict');
            $table->boolean('is_active')->default(true);
            $table->boolean('is_system')->default(false);
            $table->text('description')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->timestamps();
            $table->softDeletes();

            $table->index(['company_id', 'account_type']);
            $table->index('account_code');
            $table->index('is_active');
        });

        // Journal Entries
        Schema::create('journal_entries', function (Blueprint $table) {
            $table->id();
            $table->string('entry_number', 50)->unique();
            $table->date('entry_date');
            $table->date('posting_date')->nullable();
            $table->string('reference', 100)->nullable();
            $table->text('description')->nullable();
            $table->boolean('is_posted')->default(false);
            $table->boolean('is_reversed')->default(false);
            $table->foreignId('reversed_by_id')->nullable()->constrained('journal_entries')->onDelete('set null');
            $table->string('source_type', 50)->nullable();
            $table->unsignedBigInteger('source_id')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->foreignId('created_by')->nullable()->constrained('users')->onDelete('set null');
            $table->foreignId('posted_by')->nullable()->constrained('users')->onDelete('set null');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->index(['company_id', 'entry_date']);
            $table->index(['source_type', 'source_id']);
            $table->index('is_posted');
        });

        // Journal Entry Lines
        Schema::create('journal_entry_lines', function (Blueprint $table) {
            $table->id();
            $table->foreignId('journal_entry_id')->constrained('journal_entries')->onDelete('cascade');
            $table->integer('line_number')->default(1);
            $table->foreignId('coa_id')->constrained('chart_of_accounts')->onDelete('restrict');
            $table->text('description')->nullable();
            $table->decimal('debit_amount', 15, 2)->default(0);
            $table->decimal('credit_amount', 15, 2)->default(0);
            $table->foreignId('party_id')->nullable()->constrained('parties')->onDelete('set null');
            $table->enum('party_type', ['CUSTOMER', 'VENDOR', 'EMPLOYEE'])->nullable();
            $table->timestamps();

            $table->index(['journal_entry_id', 'line_number']);
            $table->index('coa_id');
            $table->index(['party_id', 'party_type']);
        });

        // Account Balances (for reporting performance)
        Schema::create('account_balances', function (Blueprint $table) {
            $table->id();
            $table->foreignId('coa_id')->constrained('chart_of_accounts')->onDelete('cascade');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('period_year');
            $table->integer('period_month');
            $table->decimal('opening_balance', 15, 2)->default(0);
            $table->decimal('debit_total', 15, 2)->default(0);
            $table->decimal('credit_total', 15, 2)->default(0);
            $table->decimal('closing_balance', 15, 2)->default(0);
            $table->timestamps();

            $table->unique(['coa_id', 'company_id', 'period_year', 'period_month'], 'unique_account_period');
            $table->index(['company_id', 'period_year', 'period_month']);
        });

        // Transactions (legacy payment tracking)
        Schema::create('transactions', function (Blueprint $table) {
            $table->id();
            $table->integer('credit_code');
            $table->integer('debit_code');
            $table->tinyInteger('type');
            $table->decimal('amount', 10, 2);
            $table->date('date');
            $table->string('comment');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('transactions');
        Schema::dropIfExists('account_balances');
        Schema::dropIfExists('journal_entry_lines');
        Schema::dropIfExists('journal_entries');
        Schema::dropIfExists('chart_of_accounts');
    }
};
