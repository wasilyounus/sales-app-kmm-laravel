import { Head, router } from '@inertiajs/react';
import { useState, useEffect, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import ItemFormModal from '@/Components/ItemFormModal';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Edit, Package, Layers } from 'lucide-react';
import { debounce } from 'lodash';

export default function Index({ items, filters, taxes, uqcs }) {
    const [searchTerm, setSearchTerm] = useState(filters.search || '');
    const [showModal, setShowModal] = useState(false);
    const [editingItem, setEditingItem] = useState(null);

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(
                '/admin/items',
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

    const getStockStatus = (stock) => {
        if (stock <= 0) return { label: 'Out of Stock', color: 'bg-red-100 text-red-700 hover:bg-red-200' };
        if (stock < 10) return { label: 'Low Stock', color: 'bg-amber-100 text-amber-700 hover:bg-amber-200' };
        return { label: 'In Stock', color: 'bg-lime-100 text-lime-700 hover:bg-lime-200' };
    };

    const handleEdit = (item) => {
        setEditingItem(item);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setEditingItem(null);
    };

    return (
        <AdminLayout title="Items">
            <Head title="Items" />

            {/* Header */}
            <div className="mb-6">
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold flex items-center gap-3">
                            <Package className="h-8 w-8 text-lime-500" />
                            Item Management
                        </h1>
                        <p className="text-gray-600 mt-1">Manage your products and services</p>
                    </div>
                    <Button onClick={() => setShowModal(true)}>
                        <Plus className="h-4 w-4 mr-2" />
                        Add Item
                    </Button>
                </div>
            </div>

            {/* Search */}
            <div className="flex gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input
                        type="search"
                        placeholder="Search items..."
                        value={searchTerm}
                        onChange={onSearchChange}
                        className="pl-10"
                    />
                </div>
            </div>

            {/* Items Grid */}
            <div className="grid grid-cols-1 gap-4">
                {items.data && items.data.length > 0 ? (
                    items.data.map((item) => (
                        <Card key={item.id} className="hover:shadow-lg transition-shadow">
                            <CardContent className="p-6">
                                <div className="flex items-center justify-between">
                                    <div className="flex-1">
                                        <div className="flex items-center gap-3 mb-2">
                                            <h3 className="text-lg font-semibold">{item.name}</h3>
                                            {item.brand && (
                                                <Badge variant="outline">{item.brand}</Badge>
                                            )}
                                        </div>
                                        <div className="flex gap-4 text-sm text-gray-600">
                                            {item.size && <span>Size: {item.size}</span>}
                                            {item.hsn && <span>HSN: {item.hsn}</span>}
                                            <span className="flex items-center gap-1">
                                                <Layers className="h-4 w-4" />
                                                Stock: {item.stock_count || 0}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <Badge className={getStockStatus(item.stock_count || 0).color}>
                                            {getStockStatus(item.stock_count || 0).label}
                                        </Badge>
                                        <Button
                                            size="sm"
                                            variant="outline"
                                            onClick={() => handleEdit(item)}
                                        >
                                            <Edit className="h-4 w-4" />
                                        </Button>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    ))
                ) : (
                    <Card>
                        <CardContent className="p-12 text-center">
                            <Package className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                            <p className="text-gray-500">No items found</p>
                            <Button onClick={() => setShowModal(true)} className="mt-4">
                                <Plus className="h-4 w-4 mr-2" />
                                Add your first item
                            </Button>
                        </CardContent>
                    </Card>
                )}
            </div>

            {/* Modal */}
            <ItemFormModal
                open={showModal}
                onClose={handleCloseModal}
                item={editingItem}
                taxes={taxes}
                uqcs={uqcs}
            />
        </AdminLayout>
    );
}
