<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('grns', function (Blueprint $table) {
            $table->id();
            $table->foreignId('purchase_id')->constrained()->onDelete('cascade');
            $table->string('grn_number')->nullable();
            $table->date('date');
            $table->string('vehicle_no')->nullable();
            $table->string('invoice_no')->nullable();
            $table->text('notes')->nullable();
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->foreignId('log_id')->nullable()->constrained('logs');
            $table->timestamps();
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('grns');
    }
};
