<?php

namespace Tests\Unit\Models;

use App\Models\{
    Account,
    Address,
    Crash,
    Item,
    Order,
    OrderItem,
    SaleItem,
    Purchase,
    PurchaseItem,
    Quote,
    QuoteItem,
    Sale,
    Stock,
    Supply,
    Tax,
    Transaction,
    Transport,
    Update,
    Uqc,
    User,
    UserAccountPermission,
    Party,
    PriceList,
    PriceListItem,
};
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class TimestampModelTest extends TestCase
{
    use RefreshDatabase;

    /** @test */
    public function all_models_have_timestamps_enabled()
    {
        $models = [
            Account::class,
            Address::class,
            Crash::class,
            Item::class,
            Order::class,
            OrderItem::class,
            Party::class,
            PriceList::class,
            PriceListItem::class,
            Purchase::class,
            PurchaseItem::class,
            Quote::class,
            QuoteItem::class,
            Sale::class,
            SaleItem::class,
            Stock::class,
            Supply::class,
            Tax::class,
            Transaction::class,
            Transport::class,
            Update::class,
            Uqc::class,
            User::class,
            UserAccountPermission::class,
        ];

        foreach ($models as $modelClass) {
            $model = new $modelClass();
            $this->assertTrue(
                $model->usesTimestamps(),
                "Model {$modelClass} should have timestamps available"
            );
        }
    }

    /** @test */
    public function all_item_models_have_tax_id_in_fillable()
    {
        $itemModels = [
            'SaleItem' => new SaleItem(),
            'OrderItem' => new OrderItem(),
            'PurchaseItem' => new PurchaseItem(),
        ];

        foreach ($itemModels as $name => $model) {
            $this->assertContains(
                'tax_id',
                $model->getFillable(),
                "$name should have tax_id in fillable"
            );
        }
    }




}
