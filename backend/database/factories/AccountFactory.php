<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Account>
 */
class AccountFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        $name = fake()->company();
        return [
            'name' => $name,
            'name_formatted' => $name, // Use same value for formatted name
            'desc' => $this->faker->sentence(),
            'taxation_type' => 1,
            'address' => $this->faker->address(),
            'call' => $this->faker->phoneNumber(),
            'whatsapp' => $this->faker->phoneNumber(),
            'footer_content' => $this->faker->text(),
            'signature' => false,
            'log_id' => 1,
            'financial_year_start' => now()->format('Y-m-d H:i:s'),
            'country' => $this->faker->country(),
            'state' => $this->faker->state(),
            'tax_number' => $this->faker->bothify('??#####'),
            'visibility' => 'private',
        ];
    }
}
