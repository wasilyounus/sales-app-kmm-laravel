import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import ItemFormModal from '../ItemFormModal';

describe('ItemFormModal', () => {
    const mockItem = {
        id: 1,
        name: 'Test Item',
        price: 150,
        tax_id: null,
        unit: 'pcs'
    };
    
    const mockTaxes = [
        { id: 1, name: 'GST 18%', rate: 18, active: true }
    ];

    it('renders item name field', () => {
        render(
            <ItemFormModal 
                isOpen={true} 
                onClose={() => {}} 
                item={mockItem}
                taxes={mockTaxes}
            />
        );
        
        const nameInput = screen.getByLabelText(/Item Name/i);
        expect(nameInput).toBeInTheDocument();
        expect(nameInput.value).toBe('Test Item');
    });
});
