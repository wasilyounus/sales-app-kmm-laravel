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
        // Companies (formerly accounts)
        Schema::create('companies', function (Blueprint $table) {
            $table->id();
            $table->string('name');
            $table->string('name_formatted')->nullable();
            $table->text('desc')->nullable();
            $table->string('taxation_type')->nullable();
            $table->text('address')->nullable();
            $table->string('call')->nullable();
            $table->string('whatsapp')->nullable();
            $table->text('footer_content')->nullable();
            $table->boolean('signature')->default(false);
            $table->integer('log_id')->nullable();
            $table->date('financial_year_start')->nullable();
            $table->string('country')->nullable();
            $table->string('state')->nullable();
            $table->string('tax_number')->nullable();
            $table->foreignId('default_tax_id')->nullable()->constrained('taxes')->onDelete('set null');
            $table->string('tax_application_level')->default('item');
            $table->string('visibility')->default('private');
            $table->boolean('enable_delivery_notes')->default(true);
            $table->boolean('enable_grns')->default(true);
            $table->boolean('allow_negative_stock')->default(false)->comment('Allow sales even when stock is insufficient');
            $table->timestamps();
            $table->softDeletes();
        });

        // User-Company Permissions
        Schema::create('user_company_permissions', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained()->onDelete('cascade');
            $table->foreignId('company_id')->constrained('companies')->onDelete('cascade');
            $table->string('role')->default('user');
            $table->timestamps();

            $table->unique(['user_id', 'company_id']);
        });

        // Add current_company_id to users
        Schema::table('users', function (Blueprint $table) {
            $table->foreignId('current_company_id')->nullable()->after('id')
                ->constrained('companies')->onDelete('set null');
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropForeign(['current_company_id']);
            $table->dropColumn('current_company_id');
        });

        Schema::dropIfExists('user_company_permissions');
        Schema::dropIfExists('companies');
    }
};
