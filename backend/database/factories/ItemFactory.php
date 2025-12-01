<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Item>
 */
class ItemFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'name' => $this->faker->word(),
            'alt_name' => $this->faker->word(),
            'brand' => $this->faker->company(),
            'size' => $this->faker->randomDigit(),
            'uqc' => 1,
            'hsn' => $this->faker->numerify('####'),
            'account_id' => 1,
            'log_id' => 1,
            'tax_id' => null,
        ];
    }
}
