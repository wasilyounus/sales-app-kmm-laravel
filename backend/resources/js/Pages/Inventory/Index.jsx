import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Package, ArrowUpRight, ArrowDownRight, AlertTriangle, DollarSign } from 'lucide-react';
import { debounce } from 'lodash';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";

export default function Index({ items, stats, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null);

    const { data, setData, post, processing, reset, errors } = useForm({
        item_id: '',
        adjustment: '',
        reason: '',
    });

    const handleSearch = useCallback(debounce((value) => {
        router.get(route('inventory.index'), { search: value }, { preserveState: true, replace: true });
    }, 300), []);

    const onSearchChange = (e) => { setSearchTerm(e.target.value); handleSearch(e.target.value); };

    const openAdjustModal = (item) => {
        setSelectedItem(item);
        setData({ item_id: item.id, adjustment: '', reason: '' });
        setModalOpen(true);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        post(route('inventory.adjust'), {
            onSuccess: () => { setModalOpen(false); reset(); },
        });
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount);

    return (
        <AdminLayout title="Inventory">
            <Head title="Inventory" />
            <PageHeader icon={Package} title="Inventory Management" subtitle="Track stock levels and adjustments" />

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="border-l-4 border-l-blue-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">Total Items</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.total_items || 0}</h3></div>
                            <div className="p-2 bg-blue-100 rounded-lg"><Package className="w-5 h-5 text-blue-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-amber-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">Low Stock Items</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.low_stock || 0}</h3></div>
                            <div className="p-2 bg-amber-100 rounded-lg"><AlertTriangle className="w-5 h-5 text-amber-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-emerald-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">Total Value</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{formatCurrency(stats?.total_value || 0)}</h3></div>
                            <div className="p-2 bg-emerald-100 rounded-lg"><DollarSign className="w-5 h-5 text-emerald-600" /></div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input type="text" placeholder="Search items..." value={searchTerm} onChange={onSearchChange} className="pl-12 h-12 text-base bg-white border-gray-200" />
                </div>
            </div>

            <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="text-xs text-gray-500 uppercase bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-medium">Item Name</th>
                                <th className="px-6 py-4 font-medium">Code</th>
                                <th className="px-6 py-4 font-medium">Category</th>
                                <th className="px-6 py-4 font-medium text-right">Current Stock</th>
                                <th className="px-6 py-4 font-medium text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {items?.data?.length === 0 ? (
                                <tr><td colSpan="5" className="px-6 py-12 text-center text-gray-500">No items found.</td></tr>
                            ) : items?.data?.map((item) => (
                                <tr key={item.id} className="bg-white hover:bg-gray-50 transition-colors">
                                    <td className="px-6 py-4 font-medium text-gray-900">{item.name}</td>
                                    <td className="px-6 py-4 text-gray-500">{item.code || '-'}</td>
                                    <td className="px-6 py-4 text-gray-500">{item.category || '-'}</td>
                                    <td className="px-6 py-4 text-right font-bold text-gray-900">
                                        <span className={item.current_stock < 10 ? 'text-amber-600' : ''}>{item.current_stock} {item.unit}</span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <Button variant="outline" size="sm" onClick={() => openAdjustModal(item)}>Adjust Stock</Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </Card>

            <Dialog open={modalOpen} onOpenChange={setModalOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Adjust Stock: {selectedItem?.name}</DialogTitle>
                        <DialogDescription>Add or subtract stock quantity.</DialogDescription>
                    </DialogHeader>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label>Adjustment Quantity</Label>
                            <Input 
                                type="number" 
                                placeholder="e.g. 10 or -5" 
                                value={data.adjustment} 
                                onChange={(e) => setData('adjustment', e.target.value)} 
                                step="any"
                            />
                            <p className="text-xs text-gray-500">Use positive number to add, negative to subtract.</p>
                        </div>
                        <div className="space-y-2">
                            <Label>Reason (Optional)</Label>
                            <Input 
                                type="text" 
                                placeholder="e.g. Restock, Damage, Correction" 
                                value={data.reason} 
                                onChange={(e) => setData('reason', e.target.value)} 
                            />
                        </div>
                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-blue-600 hover:bg-blue-700">Save Adjustment</Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
