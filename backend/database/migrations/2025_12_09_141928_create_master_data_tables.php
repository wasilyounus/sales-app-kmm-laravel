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
        // UQCs (Units of Measurement)
        Schema::create('uqcs', function (Blueprint $table) {
            $table->id();
            $table->string('code')->unique();
            $table->string('name');
            $table->timestamps();
            $table->softDeletes();
        });

        // Taxes (Universal - not company-specific)
        Schema::create('taxes', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->decimal('rate', 5, 2);
            $table->string('country')->nullable();
            $table->boolean('is_default')->default(false);
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        // Parties (Customers & Vendors)
        Schema::create('parties', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('phone')->nullable();
            $table->string('email')->nullable();
            $table->string('tax_number')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        // Addresses
        Schema::create('addresses', function (Blueprint $table) {
            $table->id();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->string('attention')->nullable();
            $table->string('address_line_1')->nullable();
            $table->string('address_line_2')->nullable();
            $table->string('city')->nullable();
            $table->string('state')->nullable();
            $table->string('country')->nullable();
            $table->string('zip_code')->nullable();
            $table->string('phone')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        // Items (Products)
        Schema::create('items', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('hsn')->nullable();
            $table->integer('uqc')->nullable();
            $table->decimal('price', 10, 2)->default(0);
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();

            $table->foreign('uqc')->references('id')->on('uqcs')->onDelete('set null');
        });

        // Price Lists
        Schema::create('price_lists', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->text('description')->nullable();
            $table->date('effective_from')->nullable();
            $table->date('effective_to')->nullable();
            $table->boolean('is_active')->default(true);
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('price_list_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('price_list_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->decimal('price', 10, 2);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('price_list_items');
        Schema::dropIfExists('price_lists');
        Schema::dropIfExists('items');
        Schema::dropIfExists('addresses');
        Schema::dropIfExists('parties');
        Schema::dropIfExists('taxes');
        Schema::dropIfExists('uqcs');
    }
};
