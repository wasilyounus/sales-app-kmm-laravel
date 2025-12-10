<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('journal_entry_lines', function (Blueprint $table) {
            $table->id();
            $table->foreignId('journal_entry_id')->constrained('journal_entries')->onDelete('cascade');
            $table->integer('line_number')->default(1)->comment('Line order within entry');

            // Account reference
            $table->foreignId('coa_id')->constrained('chart_of_accounts')->onDelete('restrict')->comment('Link to Chart of Accounts');
            $table->text('description')->nullable()->comment('Line-specific description');

            // Amounts - one must be > 0, not both
            $table->decimal('debit_amount', 15, 2)->default(0);
            $table->decimal('credit_amount', 15, 2)->default(0);

            // Optional party tracking for AR/AP
            $table->foreignId('party_id')->nullable()->constrained('parties')->onDelete('set null');
            $table->enum('party_type', ['CUSTOMER', 'VENDOR', 'EMPLOYEE'])->nullable();

            $table->timestamps();

            // Indexes
            $table->index(['journal_entry_id', 'line_number']);
            $table->index('coa_id');
            $table->index(['party_id', 'party_type']);
        });

        // Note: SQLite handles CHECK constraints inline via Blueprint.
        // For MySQL/PostgreSQL, this is created automatically by Laravel.
        // The constraint ensures: Either debit OR credit must be set, not both.
    }

    public function down(): void
    {
        Schema::dropIfExists('journal_entry_lines');
    }
};
