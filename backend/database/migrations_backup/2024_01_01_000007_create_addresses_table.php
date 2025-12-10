<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('addresses', function (Blueprint $table) {
            $table->id();
            $table->foreignId('party_id')->constrained()->onDelete('cascade');
            $table->string('line1')->nullable();
            $table->string('line2')->nullable();
            $table->string('place')->nullable();
            $table->string('district')->nullable();
            $table->string('state')->nullable();
            $table->integer('pincode')->nullable();
            $table->string('coordinates')->nullable();
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('addresses');
    }
};
