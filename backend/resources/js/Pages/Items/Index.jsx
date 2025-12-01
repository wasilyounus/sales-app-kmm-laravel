import { Head, router } from '@inertiajs/react';
import { useState, useEffect, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Edit, Package, Tag, Layers, Filter } from 'lucide-react';
import debounce from 'lodash/debounce';

export default function Index({ items, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters.search || '');

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(
                route('items.index'),
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

    return (
        <AdminLayout title="Items">
            <Head title="Items" />

            <PageHeader
                icon={Package}
                title="Item Management"
                subtitle="Manage your products and services"
                actionLabel="Add Item"
                actionIcon={Plus}
            />

            {/* Search and Filter */}
            <div className="flex flex-col sm:flex-row gap-4 mb-8">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                        type="text"
                        placeholder="Search items by name or category..."
                        value={searchTerm}
                        onChange={onSearchChange}
                        className="pl-12 h-12 text-base bg-white border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-500/20 transition-all duration-200"
                    />
                </div>
                <Button variant="outline" className="h-12 px-6 border-gray-200 hover:bg-gray-50 text-gray-700">
                    <Filter className="w-4 h-4 mr-2" />
                    Filters
                </Button>
            </div>

            {/* Items Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {items.data.length === 0 ? (
                    <div className="col-span-full text-center py-12 text-gray-500">
                        No items found.
                    </div>
                ) : (
                    items.data.map((item) => {
                        const stockCount = item.stock_count || 0;
                        const status = getStockStatus(stockCount);
                        
                        return (
                            <Card 
                                key={item.id} 
                                className="group hover:shadow-xl hover:shadow-lime-500/5 transition-all duration-300 hover:-translate-y-1 border-gray-100 overflow-hidden bg-white"
                            >
                                <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3 bg-gradient-to-br from-transparent to-lime-500/5">
                                    <div className="space-y-1 flex-1">
                                        <CardTitle className="text-lg font-semibold text-gray-900 group-hover:text-lime-600 transition-colors flex items-center gap-2">
                                            <Package className="h-4 w-4 text-gray-400" />
                                            {item.name}
                                        </CardTitle>
                                        <div className="flex items-center gap-2">
                                            <Badge variant="secondary" className="bg-lime-50 text-lime-700 hover:bg-lime-100 border-lime-100">
                                                {item.brand || 'General'}
                                            </Badge>
                                        </div>
                                    </div>
                                    <div className="p-2 rounded-lg bg-gray-50 group-hover:bg-lime-100 transition-colors">
                                        <Tag className="w-4 h-4 text-gray-400 group-hover:text-lime-600" />
                                    </div>
                                </CardHeader>
                                <CardContent className="space-y-4 pt-4">
                                    <div className="flex items-center justify-between">
                                        <div className="space-y-0.5">
                                            <p className="text-sm text-gray-500">Price</p>
                                            <p className="text-xl font-bold text-gray-900">--</p>
                                        </div>
                                        <div className="space-y-0.5 text-right">
                                            <p className="text-sm text-gray-500">Stock</p>
                                            <p className="text-lg font-semibold text-gray-900">{stockCount}</p>
                                        </div>
                                    </div>

                                    <div className="pt-3 border-t border-gray-100 flex items-center justify-between gap-3">
                                        <Badge className={`${status.color} border-0`}>
                                            {status.label}
                                        </Badge>
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
                        );
                    })
                )}
            </div>
            
            {/* Pagination could be added here */}
        </AdminLayout>
    );
}
