import { Link } from '@inertiajs/react';
import { useState, useEffect } from 'react';
import { Check, ChevronDown, Building2, Plus } from 'lucide-react';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/Components/ui/dropdown-menu";
import { Button } from "@/Components/ui/button";

export default function AccountSwitcher({ accounts = [], currentAccount, onAccountChange }) {
    const [selectedAccount, setSelectedAccount] = useState(currentAccount || (accounts.length > 0 ? accounts[0] : null));

    // Update selected account if accounts change (e.g. after loading) and no selection exists
    useEffect(() => {
        if (!selectedAccount && accounts.length > 0) {
            setSelectedAccount(accounts[0]);
        }
    }, [accounts, selectedAccount]);

    const handleAccountSelect = (account) => {
        setSelectedAccount(account);
        if (onAccountChange) {
            onAccountChange(account);
        }
    };

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button 
                    variant="outline" 
                    className="h-10 px-4 border-gray-200 hover:bg-lime-50 hover:border-lime-300 hover:text-lime-700 transition-all duration-200 gap-2"
                >
                    <Building2 className="w-4 h-4 text-gray-500" />
                    <span className="font-medium text-sm max-w-[150px] truncate">
                        {selectedAccount ? selectedAccount.name : 'Select Account'}
                    </span>
                    <ChevronDown className="w-4 h-4 text-gray-400" />
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-64">
                <DropdownMenuLabel className="text-xs text-gray-500 uppercase font-semibold">
                    Switch Account
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                
                {accounts.length === 0 ? (
                    <div className="py-3 px-3 text-sm text-gray-500 text-center italic">
                        No accounts found
                    </div>
                ) : (
                    accounts.map((account) => (
                        <DropdownMenuItem
                            key={account.id}
                            onClick={() => handleAccountSelect(account)}
                            className="cursor-pointer py-3 px-3 focus:bg-lime-50 focus:text-lime-700"
                        >
                            <div className="flex items-center justify-between w-full">
                                <div className="flex items-center gap-3">
                                    <div className="w-8 h-8 rounded-lg bg-lime-100 flex items-center justify-center">
                                        <Building2 className="w-4 h-4 text-lime-600" />
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="font-medium text-sm">{account.name}</span>
                                        <span className="text-xs text-gray-500">{account.name_formatted}</span>
                                    </div>
                                </div>
                                {selectedAccount?.id === account.id && (
                                    <Check className="w-4 h-4 text-lime-600" />
                                )}
                            </div>
                        </DropdownMenuItem>
                    ))
                )}
                
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                    <Link href="/admin/accounts" className="cursor-pointer py-2.5 px-3 w-full flex items-center gap-2 text-gray-600 focus:text-lime-700 focus:bg-lime-50">
                        <div className="w-8 h-8 rounded-lg border border-dashed border-gray-300 flex items-center justify-center">
                            <Plus className="w-4 h-4 text-gray-500" />
                        </div>
                        <span className="font-medium text-sm">Manage Accounts</span>
                    </Link>
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
