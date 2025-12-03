import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Index from '../Index';

describe('Items Index Page', () => {
    const mockItems = {
        data: [
            { id: 1, name: 'Item 1', price: 100, tax: null },
            { id: 2, name: 'Item 2', price: 200, tax: null }
        ],
        links: [],
        meta: { per_page: 10, total: 2 }
    };
    
    const mockTaxes = [{ id: 1, name: 'GST 18%' }];

    it('renders page title', () => {
        render(<Index items={mockItems} taxes={mockTaxes} />);
        
        expect(screen.getByText(/Items/i)).toBeInTheDocument();
    });
});
