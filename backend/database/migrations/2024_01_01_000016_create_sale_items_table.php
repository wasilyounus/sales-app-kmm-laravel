<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('sale_items', function (Blueprint $table) {
            $table->id();
            $table->foreignId('sale_id')->constrained()->onDelete('cascade');
            $table->foreignId('item_id')->constrained()->onDelete('cascade');
            $table->decimal('price', 10, 2);
            $table->decimal('qty', 10, 3);
            $table->foreignId('tax_id')->nullable()->constrained('taxes')->onDelete('set null');
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('sale_items');
    }
};
