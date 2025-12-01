import { Head, router } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, ShoppingCart, Calendar, DollarSign, FileText, Filter, ArrowUpRight } from 'lucide-react';
import debounce from 'lodash/debounce';

export default function Index({ sales, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters.search || '');

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(
                route('sales.index'),
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

    const getStatusStyle = (status) => {
        switch (status) {
            case 'Completed': return 'bg-lime-100 text-lime-700 border-lime-200';
            case 'Pending': return 'bg-amber-100 text-amber-700 border-amber-200';
            case 'Cancelled': return 'bg-red-100 text-red-700 border-red-200';
            default: return 'bg-gray-100 text-gray-700 border-gray-200';
        }
    };

    return (
        <AdminLayout title="Sales">
            <Head title="Sales" />

            <PageHeader
                icon={ShoppingCart}
                title="Sales Management"
                subtitle="Track invoices and transactions"
                actionLabel="New Sale"
                actionIcon={Plus}
            />

            {/* Stats Overview */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="border-l-4 border-l-lime-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Total Revenue</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">$12,450.50</h3>
                            </div>
                            <div className="p-2 bg-lime-100 rounded-lg">
                                <DollarSign className="w-5 h-5 text-lime-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-xs text-lime-600 font-medium">
                            <ArrowUpRight className="w-3 h-3 mr-1" />
                            +15.3% from last month
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-blue-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Invoices</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">145</h3>
                            </div>
                            <div className="p-2 bg-blue-100 rounded-lg">
                                <FileText className="w-5 h-5 text-blue-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-xs text-blue-600 font-medium">
                            <ArrowUpRight className="w-3 h-3 mr-1" />
                            +8.2% new invoices
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-amber-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Pending</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">$2,340.00</h3>
                            </div>
                            <div className="p-2 bg-amber-100 rounded-lg">
                                <Calendar className="w-5 h-5 text-amber-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-xs text-gray-500">
                            5 invoices pending payment
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Search and Filter */}
            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                        type="text"
                        placeholder="Search invoices by ID or customer..."
                        value={searchTerm}
                        onChange={onSearchChange}
                        className="pl-12 h-12 text-base bg-white border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-500/20 transition-all duration-200"
                    />
                </div>
                <Button variant="outline" className="h-12 px-6 border-gray-200 hover:bg-gray-50 text-gray-700">
                    <Filter className="w-4 h-4 mr-2" />
                    Filter Status
                </Button>
            </div>

            {/* Sales List */}
            <div className="space-y-4">
                {/* Desktop Table View */}
                <Card className="hidden md:block border-gray-100 shadow-sm overflow-hidden bg-white">
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm text-left">
                            <thead className="text-xs text-gray-500 uppercase bg-gray-50 border-b border-gray-100">
                                <tr>
                                    <th className="px-6 py-4 font-medium">Invoice ID</th>
                                    <th className="px-6 py-4 font-medium">Customer</th>
                                    <th className="px-6 py-4 font-medium">Date</th>
                                    <th className="px-6 py-4 font-medium">Items</th>
                                    <th className="px-6 py-4 font-medium">Amount</th>
                                    <th className="px-6 py-4 font-medium">Status</th>
                                    <th className="px-6 py-4 font-medium text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {sales.data.length === 0 ? (
                                    <tr>
                                        <td colSpan="7" className="px-6 py-12 text-center text-gray-500">
                                            No sales found.
                                        </td>
                                    </tr>
                                ) : (
                                    sales.data.map((sale) => (
                                        <tr key={sale.id} className="bg-white hover:bg-gray-50 transition-colors group">
                                            <td className="px-6 py-4 font-medium text-gray-900 group-hover:text-lime-600 transition-colors">
                                                {sale.id}
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className="font-medium text-gray-900">{sale.customer}</div>
                                            </td>
                                            <td className="px-6 py-4 text-gray-500">
                                                {sale.date}
                                            </td>
                                            <td className="px-6 py-4 text-gray-500">
                                                {sale.items} items
                                            </td>
                                            <td className="px-6 py-4 font-semibold text-gray-900">
                                                ${sale.amount.toFixed(2)}
                                            </td>
                                            <td className="px-6 py-4">
                                                <Badge variant="outline" className={getStatusStyle(sale.status)}>
                                                    {sale.status}
                                                </Badge>
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <Button variant="ghost" size="sm" className="hover:text-lime-600 hover:bg-lime-50 text-gray-500">
                                                    View Details
                                                </Button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </Card>

                {/* Mobile Card View */}
                <div className="md:hidden space-y-4">
                    {sales.data.length === 0 ? (
                        <div className="text-center py-12 text-gray-500">
                            No sales found.
                        </div>
                    ) : (
                        sales.data.map((sale) => (
                            <Card key={sale.id} className="border-gray-100 shadow-sm bg-white">
                                <CardContent className="p-4">
                                    <div className="flex justify-between items-start mb-3">
                                        <div>
                                            <span className="text-xs font-medium text-gray-500">Invoice ID</span>
                                            <p className="font-bold text-gray-900 text-sm">{sale.id}</p>
                                        </div>
                                        <Badge variant="outline" className={getStatusStyle(sale.status)}>
                                            {sale.status}
                                        </Badge>
                                    </div>
                                    
                                    <div className="flex justify-between items-center mb-3">
                                        <div>
                                            <span className="text-xs font-medium text-gray-500">Customer</span>
                                            <p className="font-medium text-gray-900">{sale.customer}</p>
                                        </div>
                                        <div className="text-right">
                                            <span className="text-xs font-medium text-gray-500">Amount</span>
                                            <p className="font-bold text-gray-900">${sale.amount.toFixed(2)}</p>
                                        </div>
                                    </div>

                                    <div className="flex justify-between items-center pt-3 border-t border-gray-50">
                                        <div className="text-xs text-gray-500">
                                            <span className="mr-3">{sale.date}</span>
                                            <span>{sale.items} items</span>
                                        </div>
                                        <Button variant="ghost" size="sm" className="h-8 hover:text-lime-600 hover:bg-lime-50 text-gray-500">
                                            View Details
                                        </Button>
                                    </div>
                                </CardContent>
                            </Card>
                        ))
                    )}
                </div>
            </div>
        </AdminLayout>
    );
}
