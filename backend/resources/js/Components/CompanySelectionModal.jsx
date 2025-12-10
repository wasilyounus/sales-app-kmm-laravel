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

export default function CompanySelectionModal({ open, onCompanySelected }) {
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selecting, setSelecting] = useState(false);

    useEffect(() => {
        if (open) {
            fetchCompanies();
        }
    }, [open]);

    const fetchCompanies = async () => {
        try {
            setLoading(true);
            const response = await axios.get('/api/companies');
            const companiesData = response.data.data || response.data; // Handle both formats
            setCompanies(companiesData);

            // Auto-select if only one company
            if (companiesData.length === 1) {
                await selectCompany(companiesData[0].id);
            }
        } catch (err) {
            console.error('Failed to load companies:', err);
            setError('Failed to load companies');
        } finally {
            setLoading(false);
        }
    };

    const selectCompany = async (companyId) => {
        try {
            setSelecting(true);
            await axios.post('/admin/select-company', { company_id: companyId });

            // Try to save to localStorage (optional - backend session is source of truth)
            try {
                localStorage.setItem('current_company_id', companyId.toString());
            } catch (storageError) {
                // Ignore localStorage errors - browser may block storage in certain contexts
                console.warn('Unable to save to localStorage:', storageError);
            }

            // Notify parent
            if (onCompanySelected) {
                onCompanySelected(companyId);
            }
        } catch (err) {
            setError('Failed to select company');
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
                            <p className="text-gray-500">Loading companies...</p>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        );
    }

    if (companies.length === 0) {
        return (
            <Dialog open={open}>
                <DialogContent className="sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="text-red-600">No Companies Found</DialogTitle>
                        <DialogDescription>
                            You don't have access to any companies. Please contact your administrator.
                        </DialogDescription>
                    </DialogHeader>
                </DialogContent>
            </Dialog>
        );
    }

    return (
        <Dialog open={open} onOpenChange={() => { }}>
            <DialogContent
                className="sm:max-w-2xl"
                onPointerDownOutside={(e) => e.preventDefault()}
                onEscapeKeyDown={(e) => e.preventDefault()}
            >
                <DialogHeader>
                    <DialogTitle>Select Company</DialogTitle>
                    <DialogDescription>
                        Choose the company you want to work with
                    </DialogDescription>
                </DialogHeader>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                    {companies.map((company) => (
                        <Card
                            key={company.id}
                            className="cursor-pointer hover:shadow-lg transition-shadow border-2 hover:border-lime-500 overflow-hidden bg-white"
                            onClick={() => selectCompany(company.id)}
                        >
                            <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3 bg-gradient-to-br from-transparent to-lime-500/5">
                                <div className="space-y-1 flex-1">
                                    <CardTitle className="text-lg font-semibold text-gray-900 hover:text-lime-600 transition-colors flex items-center gap-2">
                                        <Building2 className="h-4 w-4 mr-1 text-gray-400" />
                                        {company.name}
                                    </CardTitle>
                                    <p className="text-sm text-gray-500">{company.name_formatted}</p>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                {company.desc && (
                                    <p className="text-sm text-gray-500 line-clamp-2 leading-relaxed">{company.desc}</p>
                                )}

                                <div className="space-y-2.5">
                                    {company.tax_number && (
                                        <div className="flex items-center text-sm p-2 rounded-lg bg-gray-50">
                                            <FileText className="w-4 h-4 mr-2.5 text-gray-400 shrink-0" />
                                            <span className="text-gray-500 w-12 shrink-0">Tax#:</span>
                                            <span className="font-medium text-gray-900 truncate">{company.tax_number}</span>
                                        </div>
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
                        Selecting company...
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
}
