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
        // Stocks
        Schema::create('stocks', function (Blueprint $table) {
            $table->id();
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->integer('quantity');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();

            // Unique stock record per item per location
            $table->unique(['item_id', 'location_id'], 'unique_item_location_stock');
        });

        // Supplies (Stock movements from purchases)
        Schema::create('supplies', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sale_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->integer('quantity');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        // Transports (Delivery tracking)
        Schema::create('transports', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sale_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('purchase_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('from_location_id')->nullable()->constrained('locations')->onDelete('set null');
            $table->foreignId('to_location_id')->nullable()->constrained('locations')->onDelete('set null');
            $table->string('vehicle_number')->nullable();
            $table->string('driver_name')->nullable();
            $table->date('dispatch_date')->nullable();
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        // Delivery Notes
        Schema::create('delivery_notes', function (Blueprint $table) {
            $table->id();
            $table->string('dn_no')->nullable();
            $table->foreignId('sale_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->date('date');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('delivery_note_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('delivery_note_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->timestamps();
        });

        // Goods Receipt Notes (GRNs)
        Schema::create('grns', function (Blueprint $table) {
            $table->id();
            $table->string('grn_no')->nullable();
            $table->foreignId('purchase_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('location_id')->constrained('locations')->onDelete('cascade');
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->date('date');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });

        Schema::create('grn_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('grn_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->integer('quantity');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('grn_items');
        Schema::dropIfExists('grns');
        Schema::dropIfExists('delivery_note_items');
        Schema::dropIfExists('delivery_notes');
        Schema::dropIfExists('transports');
        Schema::dropIfExists('supplies');
        Schema::dropIfExists('stocks');
    }
};
