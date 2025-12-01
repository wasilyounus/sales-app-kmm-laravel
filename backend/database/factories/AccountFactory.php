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
        return [
            'name' => $this->faker->company(),
            'name_formatted' => $this->faker->company(),
            'desc' => $this->faker->sentence(),
            'taxation_type' => 1,
            'gst' => null,
            'address' => $this->faker->address(),
            'call' => $this->faker->phoneNumber(),
            'whatsapp' => $this->faker->phoneNumber(),
            'footer_content' => $this->faker->text(),
            'signature' => false,
            'log_id' => 1,
            'financial_year_start' => null,
            'country' => $this->faker->country(),
            'state' => $this->faker->state(),
            'tax_number' => $this->faker->bothify('??#####'),
        ];
    }
}
