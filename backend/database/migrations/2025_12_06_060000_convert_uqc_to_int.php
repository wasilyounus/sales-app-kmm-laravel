<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // 1. Convert existing string data to IDs matches
        $items = DB::table('items')->get();
        $uqcs = DB::table('uqcs')->pluck('id', 'uqc'); // Map 'NOS' => 1

        foreach ($items as $item) {
            // Check if uqc is already numeric (ID) or string (Code)
            if (!is_numeric($item->uqc)) {
                $uqcCode = $item->uqc;
                $uqcId = $uqcs[$uqcCode] ?? 0; // Default to 0 if not found
                
                DB::table('items')
                    ->where('id', $item->id)
                    ->update(['uqc' => $uqcId]);
            }
        }

        // 2. Change column type to unsignedInteger
        Schema::table('items', function (Blueprint $table) {
            // Change column to integer. 
            // Note: 'change()' requires dbal/doctrine usually, but we can try raw statement if needed.
            // But Laravel typically supports modifying columns.
            $table->unsignedBigInteger('uqc')->change();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('items', function (Blueprint $table) {
            $table->string('uqc')->change();
        });
    }
};
