import { useState, useEffect } from 'react';
import axios from 'axios';
import { Button } from '@/Components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/Components/ui/card';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/Components/ui/dialog';
import { Building2, Check } from 'lucide-react';

export default function AccountSelectionModal({ open, onAccountSelected }) {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selecting, setSelecting] = useState(false);

    useEffect(() => {
        if (open) {
            fetchAccounts();
        }
    }, [open]);

    const fetchAccounts = async () => {
        try {
            setLoading(true);
            const response = await axios.get('/admin/select-account');
            setAccounts(response.data.accounts);
            
            // Auto-select if only one account
            if (response.data.accounts.length === 1) {
                await selectAccount(response.data.accounts[0].id);
            }
        } catch (err) {
            setError('Failed to load accounts');
        } finally {
            setLoading(false);
        }
    };

    const selectAccount = async (accountId) => {
        try {
            setSelecting(true);
            await axios.post('/admin/select-account', { account_id: accountId });
            
            // Save to localStorage
            localStorage.setItem('current_account_id', accountId.toString());
            
            // Notify parent
            if (onAccountSelected) {
                onAccountSelected(accountId);
            }
        } catch (err) {
            setError('Failed to select account');
            setSelecting(false);
        }
    };

    if (loading) {
        return (
            <Dialog open={open}>
                <DialogContent className="sm:max-w-md">
                    <div className="flex items-center justify-center p-12">
                        <div className="text-center">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-lime-500 mx-auto mb-4"></div>
                            <p className="text-gray-500">Loading accounts...</p>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        );
    }

    if (accounts.length === 0) {
        return (
            <Dialog open={open}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="text-red-600">No Accounts Assigned</DialogTitle>
                        <DialogDescription>
                            You don't have access to any accounts. Please contact your administrator.
                        </DialogDescription>
                    </DialogHeader>
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Dialog open={open}>
            <DialogContent className="sm:max-w-2xl">
                <DialogHeader>
                    <DialogTitle>Select Account</DialogTitle>
                    <DialogDescription>
                        Choose the account you want to work with
                    </DialogDescription>
                </DialogHeader>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                    {accounts.map((account) => (
                        <Card
                            key={account.id}
                            className="cursor-pointer hover:shadow-lg transition-shadow border-2 hover:border-lime-500"
                            onClick={() => selectAccount(account.id)}
                        >
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2">
                                    <Building2 className="h-5 w-5 text-lime-500" />
                                    {account.name}
                                </CardTitle>
                                <CardDescription>{account.name_formatted}</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="flex items-center justify-between">
                                    <span className="text-sm text-gray-600">
                                        Role: <span className="font-medium">{account.role}</span>
                                    </span>
                                    {account.visibility === 'private' && (
                                        <span className="text-xs text-gray-400">Private</span>
                                    )}
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>

                {error && (
                    <p className="text-sm text-red-500 mt-4">{error}</p>
                )}

                {selecting && (
                    <div className="text-center text-sm text-gray-500 mt-4">
                        Selecting account...
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
}
