<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('transports', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('gst');
            $table->string('contact')->default('');
            $table->boolean('active')->default(false);
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('transports');
    }
};
