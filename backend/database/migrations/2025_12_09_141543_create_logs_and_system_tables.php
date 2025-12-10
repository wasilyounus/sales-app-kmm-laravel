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
        // Logs (audit trail)
        Schema::create('logs', function (Blueprint $table) {
            $table->id();
            $table->string('action'); // create, update, delete
            $table->string('model'); // Model class name
            $table->unsignedBigInteger('model_id')->nullable(); // ID in model table
            $table->foreignId('user_id')->nullable()->constrained()->onDelete('set null');
            $table->foreignId('company_id')->nullable()->constrained('companies')->onDelete('cascade');
            $table->json('data')->nullable(); // Snapshot of model data
            $table->text('details')->nullable(); // Additional details
            $table->timestamps();
            $table->softDeletes();

            $table->index(['model', 'model_id']);
            $table->index('company_id');
        });

        // Updates (version tracking)
        Schema::create('updates', function (Blueprint $table) {
            $table->id();
            $table->string('version');
            $table->text('changes')->nullable();
            $table->date('release_date');
            $table->timestamps();
        });

        // Crashes (error reporting)
        Schema::create('crashes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->nullable()->constrained()->onDelete('set null');
            $table->text('error_message');
            $table->text('stack_trace')->nullable();
            $table->string('app_version')->nullable();
            $table->foreignId('company_id')->nullable()->constrained('companies')->onDelete('cascade');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('crashes');
        Schema::dropIfExists('updates');
        Schema::dropIfExists('logs');
    }
};
