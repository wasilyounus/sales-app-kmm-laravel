<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('transactions', function (Blueprint $table) {
            $table->id();
            $table->integer('credit_code')->comment('Credit Code: Gone to | uppa_id=0 | -1=kallai | -2=perumugham | -3=bigbazar | PartyId>=1');
            $table->integer('debit_code')->comment('Debit Code: Came from | uppa_id=0 | -1=kallai | -2=perumugham | -3=bigbazar | PartyId>=1');
            $table->tinyInteger('type')->comment('1-18: Cash/Cheque/UPI/NEFT Received/Paid, Purchase, Sale, Returns, etc.');
            $table->decimal('amount', 10, 2);
            $table->date('date');
            $table->string('comment');
            $table->foreignId('account_id')->constrained()->onDelete('cascade');
            $table->integer('log_id');
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('transactions');
    }
};
