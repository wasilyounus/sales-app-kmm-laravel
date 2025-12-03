import { Head, router } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PartyFormModal from '@/Components/PartyFormModal';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Edit, Users, Phone, Mail, MapPin } from 'lucide-react';
import debounce from 'lodash/debounce';

export default function Index({ parties, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters.search || '');
    const [showModal, setShowModal] = useState(false);
    const [editingParty, setEditingParty] = useState(null);

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(
                '/admin/parties',
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

    const handleEdit = (party) => {
        setEditingParty(party);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setEditingParty(null);
    };

    return (
        <AdminLayout title="Parties">
            <Head title="Parties" />

            {/* Header */}
            <div className="mb-6">
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold flex items-center gap-3">
                            <Users className="h-8 w-8 text-lime-500" />
                            Party Management
                        </h1>
                        <p className="text-gray-600 mt-1">Manage customers and suppliers</p>
                    </div>
                    <Button onClick={() => setShowModal(true)}>
                        <Plus className="h-4 w-4 mr-2" />
                        Add Party
                    </Button>
                </div>
            </div>

            {/* Search */}
            <div className="mb-6 relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                <Input
                    type="search"
                    placeholder="Search parties..."
                    value={searchTerm}
                    onChange={onSearchChange}
                    className="pl-10"
                />
            </div>

            {/* Parties Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {parties.data && parties.data.length > 0 ? (
                    parties.data.map((party) => (
                        <Card key={party.id} className="hover:shadow-lg transition-shadow">
                            <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3">
                                <div className="space-y-1 flex-1">
                                    <CardTitle className="text-lg font-semibold flex items-center gap-2">
                                        <Users className="h-4 w-4 text-gray-400" />
                                        {party.name}
                                    </CardTitle>
                                    <Badge variant="secondary" className="bg-lime-50 text-lime-700">
                                        {party.type || 'Customer'}
                                    </Badge>
                                </div>
                                <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleEdit(party)}
                                >
                                    <Edit className="h-4 w-4" />
                                </Button>
                            </CardHeader>
                            <CardContent className="space-y-2">
                                {party.phone && (
                                    <div className="flex items-center gap-2 text-sm text-gray-600">
                                        <Phone className="h-3.5 w-3.5" />
                                        {party.phone}
                                    </div>
                                )}
                                {party.email && (
                                    <div className="flex items-center gap-2 text-sm text-gray-600">
                                        <Mail className="h-3.5 w-3.5" />
                                        <span className="truncate">{party.email}</span>
                                    </div>
                                )}
                                {party.tax_number && (
                                    <div className="text-xs text-gray-500">
                                        Tax Number: {party.tax_number}
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    ))
                ) : (
                    <Card className="col-span-full">
                        <CardContent className="p-12 text-center">
                            <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                            <p className="text-gray-500">No parties found</p>
                            <Button onClick={() => setShowModal(true)} className="mt-4">
                                <Plus className="h-4 w-4 mr-2" />
                                Add your first party
                            </Button>
                        </CardContent>
                    </Card>
                )}
            </div>

            {/* Modal */}
            <PartyFormModal
                open={showModal}
                onClose={handleCloseModal}
                party={editingParty}
            />
        </AdminLayout>
    );
}
