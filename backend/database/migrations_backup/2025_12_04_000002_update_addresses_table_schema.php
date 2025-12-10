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
        Schema::table('addresses', function (Blueprint $table) {
            // Rename place to city
            $table->renameColumn('place', 'city');

            // Add country column
            $table->string('country')->after('pincode');

            // Replace coordinates with latitude and longitude
            $table->dropColumn('coordinates');
            $table->double('latitude')->nullable()->after('country');
            $table->double('longitude')->nullable()->after('latitude');

            // Update nullability constraints (SQLite doesn't support modifying columns easily, 
            // so we'll leave them nullable for now to avoid complexity with data migration,
            // but we'll enforce it in the application layer)
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('addresses', function (Blueprint $table) {
            $table->renameColumn('city', 'place');
            $table->dropColumn('country');
            $table->dropColumn('latitude');
            $table->dropColumn('longitude');
            $table->string('coordinates')->nullable();
        });
    }
};
