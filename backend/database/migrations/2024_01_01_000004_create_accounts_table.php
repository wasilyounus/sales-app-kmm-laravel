<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('accounts', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('name_formatted');
            $table->string('desc');
            $table->integer('taxation_type')->default(1);
            $table->integer('default_tax')->default(0);
            $table->string('gst')->nullable();
            $table->text('address')->nullable();
            $table->string('call')->nullable();
            $table->string('whatsapp')->nullable();
            $table->text('footer_content')->nullable();
            $table->boolean('signature')->default(false);
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('accounts');
    }
};
