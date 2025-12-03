import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Index from '../Index';

describe('Parties Index Page', () => {
    const mockParties = {
        data: [
            { id: 1, name: 'Party 1', tax_number: 'PTAX1', city: 'City 1' },
            { id: 2, name: 'Party 2', tax_number: 'PTAX2', city: 'City 2' }
        ],
        links: [],
        meta: { per_page: 10, total: 2 }
    };

    it('renders page title', () => {
        render(<Index parties={mockParties} />);
        
        expect(screen.getByText(/Parties/i)).toBeInTheDocument();
    });
});
