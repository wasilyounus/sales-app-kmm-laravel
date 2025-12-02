import { useEffect, useState } from 'react';
import { CheckCircle, XCircle, X } from 'lucide-react';

export default function Toast({ message, type = 'success', onClose }) {
    const [isVisible, setIsVisible] = useState(true);

    useEffect(() => {
        const timer = setTimeout(() => {
            setIsVisible(false);
            setTimeout(onClose, 300); // Wait for fade out animation
        }, 3000);

        return () => clearTimeout(timer);
    }, [onClose]);

    const handleClose = () => {
        setIsVisible(false);
        setTimeout(onClose, 300);
    };

    const bgColor = type === 'success' ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200';
    const iconColor = type === 'success' ? 'text-green-600' : 'text-red-600';
    const textColor = type === 'success' ? 'text-green-800' : 'text-red-800';
    const Icon = type === 'success' ? CheckCircle : XCircle;

    return (
        <div
            className={`fixed top-4 right-4 z-50 flex items-center gap-3 px-4 py-3 rounded-lg border shadow-lg transition-all duration-300 ${bgColor} ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-2'
                }`}
        >
            <Icon className={`w-5 h-5 ${iconColor}`} />
            <p className={`text-sm font-medium ${textColor}`}>{message}</p>
            <button
                onClick={handleClose}
                className={`ml-2 ${iconColor} hover:opacity-70 transition-opacity`}
            >
                <X className="w-4 h-4" />
            </button>
        </div>
    );
}
