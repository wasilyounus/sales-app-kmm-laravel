import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import PartyFormModal from '../PartyFormModal';

describe('PartyFormModal', () => {
    const mockParty = {
        id: 1,
        name: 'Test Party',
        tax_number: 'PARTYTAX123',
        address: 'Party Address',
        city: 'City',
        pincode: '123456',
        phone: '9876543210',
        email: 'party@example.com',
        opening_balance: 0,
        type: 'customer'
    };

    it('renders tax number field when editing', () => {
        render(
            <PartyFormModal 
                isOpen={true} 
                onClose={() => {}} 
                party={mockParty}
            />
        );
        
        const taxNumberInput = screen.getByLabelText(/Tax Number/i);
        expect(taxNumberInput).toBeInTheDocument();
        expect(taxNumberInput.value).toBe('PARTYTAX123');
    });
});
