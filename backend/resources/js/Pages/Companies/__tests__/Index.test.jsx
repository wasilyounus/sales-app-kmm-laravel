import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Index from '../Index';

describe('Accounts Index Page', () => {
    const mockAccounts = [
        { id: 1, name: 'Account 1', tax_number: 'TAX1', taxation_type: 1 },
        { id: 2, name: 'Account 2', tax_number: 'TAX2', taxation_type: 1 }
    ];

    const mockTaxes = [];

    it('renders page title', () => {
        render(<Index accounts={mockAccounts} taxes={mockTaxes} />);
        
        expect(screen.getByText(/Accounts/i)).toBeInTheDocument();
    });
});
