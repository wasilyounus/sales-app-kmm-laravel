<?php

use Illuminate\Support\Facades\DB;

// Update all items for account_id = 1 to have current timestamp
$count = DB::table('items')
    ->where('account_id', 1)
    ->update(['updated_at' => now()]);

echo "Updated {$count} items with new timestamp\n";

// Show the items
$items = DB::table('items')
    ->where('account_id', 1)
    ->select('id', 'name', 'updated_at')
    ->get();

echo "\nItems in database:\n";
foreach ($items as $item) {
    echo "ID: {$item->id}, Name: {$item->name}, Updated: {$item->updated_at}\n";
}
