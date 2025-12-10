<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('journal_entries', function (Blueprint $table) {
            $table->id();
            $table->string('entry_number', 50)->unique()->comment('e.g., JE-2025-12-001');
            $table->date('entry_date')->comment('Transaction date');
            $table->date('posting_date')->nullable()->comment('When posted to ledger');
            $table->string('reference', 100)->nullable()->comment('Invoice/Payment/Document reference');
            $table->text('description')->nullable();

            // Status flags
            $table->boolean('is_posted')->default(false)->comment('Posted entries are immutable');
            $table->boolean('is_reversed')->default(false);
            $table->foreignId('reversed_by_id')->nullable()->constrained('journal_entries')->onDelete('set null');

            // Source tracking - links to sales, purchases, payments, expenses
            $table->string('source_type', 50)->nullable()->comment('SALE, PURCHASE, PAYMENT, EXPENSE, MANUAL, etc.');
            $table->unsignedBigInteger('source_id')->nullable()->comment('ID in source table');

            // Metadata
            $table->foreignId('account_id')->constrained('accounts')->onDelete('cascade');
            $table->foreignId('created_by')->nullable()->constrained('users')->onDelete('set null');
            $table->foreignId('posted_by')->nullable()->constrained('users')->onDelete('set null');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();

            // Indexes
            $table->index(['account_id', 'entry_date']);
            $table->index(['source_type', 'source_id']);
            $table->index('is_posted');
            $table->index('entry_date');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('journal_entries');
    }
};
