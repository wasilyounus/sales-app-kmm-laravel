<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Sale>
 */
class SaleFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'party_id' => \App\Models\Party::factory(),
            'date' => $this->faker->date(),
            'invoice_no' => $this->faker->unique()->numerify('INV-####'),
            'account_id' => \App\Models\Account::factory(),
            'log_id' => $this->faker->randomNumber(),
        ];
    }
}
