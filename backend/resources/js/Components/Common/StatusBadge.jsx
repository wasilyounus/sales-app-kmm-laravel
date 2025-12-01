import { Badge } from "@/Components/ui/badge";

/**
 * Get status badge styling
 * @param {string} status - Status value
 * @param {string} type - Type of status ('stock', 'payment', 'general')
 * @returns {string} Tailwind classes for the badge
 */
export function getStatusColor(status, type = 'general') {
    const statusNormalized = status?.toLowerCase();
    
    // Stock statuses
    if (type === 'stock') {
        switch (statusNormalized) {
            case 'in stock': return 'bg-lime-100 text-lime-700 hover:bg-lime-200 border-lime-200';
            case 'low stock': return 'bg-amber-100 text-amber-700 hover:bg-amber-200 border-amber-200';
            case 'critical': return 'bg-orange-100 text-orange-700 hover:bg-orange-200 border-orange-200';
            case 'out of stock': return 'bg-red-100 text-red-700 hover:bg-red-200 border-red-200';
            default: return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
        }
    }
    
    // Payment/Transaction statuses
    if (type === 'payment') {
        switch (statusNormalized) {
            case 'completed':
            case 'paid': return 'bg-lime-100 text-lime-700 hover:bg-lime-200 border-lime-200';
            case 'pending': return 'bg-amber-100 text-amber-700 hover:bg-amber-200 border-amber-200';
            case 'cancelled':
            case 'failed': return 'bg-red-100 text-red-700 hover:bg-red-200 border-red-200';
            case 'refunded': return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
            default: return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
        }
    }
    
    // General statuses
    switch (statusNormalized) {
        case 'active': return 'bg-lime-100 text-lime-700 hover:bg-lime-200 border-lime-200';
        case 'inactive': return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
        case 'pending': return 'bg-amber-100 text-amber-700 hover:bg-amber-200 border-amber-200';
        default: return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
    }
}

/**
 * StatusBadge component
 */
export default function StatusBadge({ status, type = 'general', className = '' }) {
    return (
        <Badge 
            variant="secondary" 
            className={`${getStatusColor(status, type)} ${className}`}
        >
            {status}
        </Badge>
    );
}
