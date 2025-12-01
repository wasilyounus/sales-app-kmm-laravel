<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('taxes', function (Blueprint $table) {
            $table->id();
            $table->string('scheme_name');
            $table->string('tax1_name')->nullable();
            $table->integer('tax1_val')->nullable();
            $table->string('tax2_name')->nullable();
            $table->integer('tax2_val')->nullable();
            $table->string('tax3_name')->nullable();
            $table->integer('tax3_val')->nullable();
            $table->string('tax4_name')->nullable();
            $table->integer('tax4_val')->nullable();
            $table->boolean('active')->default(false);
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('taxes');
    }
};
