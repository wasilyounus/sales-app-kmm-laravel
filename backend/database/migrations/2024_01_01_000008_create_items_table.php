<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('items', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('alt_name')->nullable();
            $table->string('brand')->nullable();
            $table->string('size')->nullable();
            $table->smallInteger('uqc');
            $table->integer('hsn')->nullable();
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->integer('log_id')->nullable();
            $table->timestamps();
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('items');
    }
};
