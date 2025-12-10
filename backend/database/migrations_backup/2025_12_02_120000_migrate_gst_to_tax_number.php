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
        // Copy existing GST data to tax_number for Indian accounts where tax_number is null
        DB::table('accounts')
            ->whereNotNull('gst')
            ->whereNull('tax_number')
            ->where(function($query) {
                $query->where('country', 'India')
                      ->orWhere('country', 'IN')
                      ->orWhereNull('country');
            })
            ->update(['tax_number' => DB::raw('gst')]);

        // Note: We're NOT dropping the gst column yet for backward compatibility
        // It can be dropped in a future migration after ensuring no code references it
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Copy tax_number back to gst if reverting
        DB::table('accounts')
            ->whereNotNull('tax_number')
            ->whereNull('gst')
            ->where(function($query) {
                $query->where('country', 'India')
                      ->orWhere('country', 'IN');
            })
            ->update(['gst' => DB::raw('tax_number')]);
    }
};
