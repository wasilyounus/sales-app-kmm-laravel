<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Tax>
 */
class TaxFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'scheme_name' => $this->faker->word(),
            'tax1_name' => 'CGST',
            'tax1_val' => 9,
            'tax2_name' => 'SGST',
            'tax2_val' => 9,
            'active' => true,
            'log_id' => 1,
        ];
    }
}
