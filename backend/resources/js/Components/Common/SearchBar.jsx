import { Search } from 'lucide-react';
import { Input } from "@/Components/ui/input";

export default function SearchBar({ searchTerm, setSearchTerm, placeholder = "Search..." }) {
    return (
        <div className="relative flex-1">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
                type="text"
                placeholder={placeholder}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-12 h-12 text-base bg-white border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-500/20 transition-all duration-200"
            />
        </div>
    );
}
