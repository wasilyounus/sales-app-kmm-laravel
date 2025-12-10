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
        Schema::create('locations', function (Blueprint $table) {
            $table->id();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->string('name');
            $table->enum('type', ['warehouse', 'retail', 'wholesale', 'office'])->default('warehouse');

            // Address
            $table->text('address')->nullable();
            $table->string('city')->nullable();
            $table->string('state')->nullable();
            $table->string('country')->nullable();
            $table->string('zip_code')->nullable();

            // Contact
            $table->string('phone')->nullable();
            $table->string('email')->nullable();

            // Tax Registration (branch-specific)
            $table->string('tax_number')->nullable()->comment('GSTIN/VAT/TRN for this location');

            // Settings
            $table->boolean('is_default')->default(false);
            $table->boolean('is_active')->default(true);
            $table->foreignId('manager_user_id')->nullable()->constrained('users')->onDelete('set null');

            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();

            // Ensure only one default per company
            $table->unique(['company_id', 'is_default'], 'unique_default_location');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('locations');
    }
};
