import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import AccountForm from '../AccountForm';

describe('AccountForm', () => {
    const mockAccount = {
        id: 1,
        name: 'Test Account',
        name_formatted: 'TEST ACCOUNT',
        desc: 'Description',
        taxation_type: 1, // No tax to simplify the test
        tax_country: '',
        country: 'India',
        state: 'Maharashtra',
        tax_number: '27ABCDE1234F1Z5',
        address: '123 Street',
        call: '1234567890',
        whatsapp: '1234567890',
        footer_content: 'Footer',
        signature: true,
        financial_year_start: '2025-04-01 00:00:00',
        default_tax_id: null
    };

    const mockTaxes = [];

    it('renders tax number field', () => {
        render(
            <AccountForm 
                account={mockAccount} 
                taxes={mockTaxes}
                onSuccess={() => {}}
                onCancel={() => {}}
            />
        );
        
        expect(screen.getByLabelText(/Tax Number/i)).toBeInTheDocument();
        expect(screen.getByDisplayValue('27ABCDE1234F1Z5')).toBeInTheDocument();
    });
});
