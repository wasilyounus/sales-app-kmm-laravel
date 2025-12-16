import { usePage, router } from '@inertiajs/react';
import { Check, ChevronDown, Building2, Plus, Settings } from 'lucide-react';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/Components/ui/dropdown-menu";
import { Button } from "@/Components/ui/button";
import axios from 'axios';

export default function CompanySwitcher({ currentCompany }) {
    const { auth } = usePage().props;
    const companies = auth.availableCompanies || [];

    const switchCompany = async (companyId) => {
        try {
            await axios.post('/admin/select-company', { company_id: companyId });
            // Reload page to apply new company context
            router.reload();
        } catch (error) {
            console.error('Failed to switch company:', error);
        }
    };

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button
                    variant="outline"
                    className="w-full justify-between h-12 px-3 border-border hover:bg-muted/50 transition-all duration-200 group"
                >
                    <div className="flex items-center gap-3">
                        <div className="w-6 h-6 rounded bg-primary/10 flex items-center justify-center text-primary">
                            <Building2 className="w-3.5 h-3.5" />
                        </div>
                        <span className="font-bold text-sm truncate">
                            {currentCompany ? currentCompany.name : 'Select Company'}
                        </span>
                    </div>
                    <ChevronDown className="w-4 h-4 text-muted-foreground group-hover:text-foreground" />
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-72">
                <DropdownMenuLabel className="text-xs text-gray-500 uppercase font-semibold">
                    Switch Company
                </DropdownMenuLabel>
                <DropdownMenuSeparator />

                {companies.length === 0 ? (
                    <div className="py-3 px-3 text-sm text-gray-500 text-center italic">
                        No companies found
                    </div>
                ) : (
                    companies.map((company) => (
                        <DropdownMenuItem
                            key={company.id}
                            onClick={() => switchCompany(company.id)}
                            className="cursor-pointer py-3 px-3 focus:bg-lime-50 focus:text-lime-700"
                        >
                            <div className="flex items-center justify-between w-full">
                                <div className="flex items-center gap-3">
                                    <div className="w-8 h-8 rounded-lg bg-lime-100 flex items-center justify-center">
                                        <Building2 className="w-4 h-4 text-lime-600" />
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="font-medium text-sm">{company.name}</span>
                                        <span className="text-xs text-gray-500">{company.name_formatted}</span>
                                    </div>
                                </div>
                                {currentCompany?.id === company.id && (
                                    <Check className="w-4 h-4 text-lime-600" />
                                )}
                            </div>
                        </DropdownMenuItem>
                    ))
                )}

                <DropdownMenuSeparator />
                <DropdownMenuItem
                    onClick={() => router.visit('/admin/companies')}
                    className="cursor-pointer py-2.5 px-3 text-gray-600 focus:text-lime-700 focus:bg-lime-50"
                >
                    <div className="flex items-center gap-2 w-full">
                        <div className="w-8 h-8 rounded-lg border border-dashed border-gray-300 flex items-center justify-center">
                            <Settings className="w-4 h-4 text-gray-500" />
                        </div>
                        <span className="font-medium text-sm">Manage Companies</span>
                    </div>
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
