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
        // Quotes
        Schema::create('quotes', function (Blueprint $table) {
            $table->id();
            $table->string('quote_no')->nullable();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->date('date');
            $table->string('invoice_no')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('quote_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('quote_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->decimal('price', 10, 2);
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->timestamps();
        });

        // Orders
        Schema::create('orders', function (Blueprint $table) {
            $table->id();
            $table->string('order_no')->nullable();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->date('date');
            $table->string('invoice_no')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('order_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('order_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->decimal('price', 10, 2);
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->timestamps();
        });

        // Sales
        Schema::create('sales', function (Blueprint $table) {
            $table->id();
            $table->string('sale_no')->nullable();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->date('date');
            $table->string('invoice_no')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('sale_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sale_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->decimal('price', 10, 2);
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->timestamps();
        });

        // Purchases
        Schema::create('purchases', function (Blueprint $table) {
            $table->id();
            $table->string('purchase_no')->nullable();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->date('date');
            $table->string('invoice_no')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('purchase_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('purchase_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->decimal('price', 10, 2);
            $table->foreignId('tax_id')->nullable()->constrained()->onDelete('set null');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('purchase_items');
        Schema::dropIfExists('purchases');
        Schema::dropIfExists('sale_items');
        Schema::dropIfExists('sales');
        Schema::dropIfExists('order_items');
        Schema::dropIfExists('orders');
        Schema::dropIfExists('quote_items');
        Schema::dropIfExists('quotes');
    }
};
