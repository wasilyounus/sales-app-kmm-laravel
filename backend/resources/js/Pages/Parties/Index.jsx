import { Head, router } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Edit, Users, Phone, MapPin, Mail } from 'lucide-react';
import debounce from 'lodash/debounce';

export default function Index({ parties, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters.search || '');

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(
                route('parties.index'),
                { search: value },
                { preserveState: true, replace: true }
            );
        }, 300),
        []
    );

    const onSearchChange = (e) => {
        setSearchTerm(e.target.value);
        handleSearch(e.target.value);
    };

    const getBalanceColor = (balance, type) => {
        if (balance === 0) return 'text-gray-400';
        if (type === 'Customer') return balance > 0 ? 'text-lime-600' : 'text-red-600';
        return balance < 0 ? 'text-red-600' : 'text-lime-600';
    };

    return (
        <AdminLayout title="Parties">
            <Head title="Parties" />

            <PageHeader
                icon={Users}
                title="Party Management"
                subtitle="Manage customers and suppliers"
                actionLabel="Add Party"
                actionIcon={Plus}
            />

            {/* Search Bar */}
            <div className="mb-8 relative">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                <Input
                    type="text"
                    placeholder="Search parties by name or email..."
                    value={searchTerm}
                    onChange={onSearchChange}
                    className="pl-12 h-12 text-base bg-white border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-500/20 transition-all duration-200"
                />
            </div>

            {/* Parties Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {parties.data.length === 0 ? (
                    <div className="col-span-full text-center py-12 text-gray-500">
                        No parties found.
                    </div>
                ) : (
                    parties.data.map((party) => (
                        <Card 
                            key={party.id} 
                            className="group hover:shadow-xl hover:shadow-lime-500/5 transition-all duration-300 hover:-translate-y-1 border-gray-100 overflow-hidden bg-white"
                        >
                            <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3 bg-gradient-to-br from-transparent to-lime-500/5">
                                <div className="space-y-1 flex-1">
                                    <CardTitle className="text-lg font-semibold text-gray-900 group-hover:text-lime-600 transition-colors flex items-center gap-2">
                                        <Users className="h-4 w-4 text-gray-400" />
                                        {party.name}
                                    </CardTitle>
                                    <div className="flex items-center gap-2">
                                        <Badge variant="secondary" className="bg-lime-50 text-lime-700 hover:bg-lime-100 border-lime-100">
                                            {party.type}
                                        </Badge>
                                        <span className={`text-xs px-2 py-0.5 rounded-full ${
                                            party.status === 'Active' ? 'bg-lime-100 text-lime-700' : 
                                            party.status === 'Inactive' ? 'bg-gray-100 text-gray-700' : 'bg-amber-100 text-amber-700'
                                        }`}>
                                            {party.status}
                                        </span>
                                    </div>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-4 pt-4">
                                <div className="space-y-2">
                                    <div className="flex items-center text-sm text-gray-500">
                                        <Phone className="w-4 h-4 mr-2 text-lime-500" />
                                        {party.phone || 'N/A'}
                                    </div>
                                    <div className="flex items-center text-sm text-gray-500">
                                        <Mail className="w-4 h-4 mr-2 text-lime-500" />
                                        {party.email || 'N/A'}
                                    </div>
                                </div>

                                <div className="pt-3 border-t border-gray-100 flex items-center justify-between">
                                    <div className="space-y-0.5">
                                        <p className="text-xs text-gray-500">Balance</p>
                                        <p className={`text-lg font-bold ${getBalanceColor(party.balance, party.type)}`}>
                                            ${Math.abs(party.balance).toFixed(2)}
                                            <span className="text-xs font-normal text-gray-400 ml-1">
                                                {party.balance > 0 ? 'Dr' : party.balance < 0 ? 'Cr' : ''}
                                            </span>
                                        </p>
                                    </div>
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        className="hover:text-lime-600 hover:bg-lime-50 text-gray-500"
                                    >
                                        <Edit className="w-4 h-4 mr-2" />
                                        Edit
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))
                )}
            </div>
        </AdminLayout>
    );
}
