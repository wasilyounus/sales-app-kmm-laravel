<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('uqcs', function (Blueprint $table) {
            $table->id();
            $table->string('quantity');
            $table->string('type');
            $table->string('uqc');
            $table->boolean('active')->default(false);
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('uqcs');
    }
};
