import { Head, router, useForm } from '@inertiajs/react';
import { useState } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Search, Plus, ArrowLeft, Save, Trash2 } from 'lucide-react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/Components/ui/select";

export default function Show({ priceList, allItems }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [modalOpen, setModalOpen] = useState(false);
    
    const { data, setData, post, processing, reset } = useForm({
        items: [], // Array of { item_id, price }
    });

    const [newItem, setNewItem] = useState({ item_id: '', price: '' });

    const filteredItems = priceList.items.filter(item => 
        item.item?.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.item?.code?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleAddItem = () => {
        if (!newItem.item_id || !newItem.price) return;
        
        post(route('price-lists.update-items', priceList.id), {
            data: { items: [{ item_id: newItem.item_id, price: newItem.price }] },
            onSuccess: () => {
                setModalOpen(false);
                setNewItem({ item_id: '', price: '' });
            },
        });
    };

    const handleRemoveItem = (itemId) => {
        router.delete(route('price-lists.remove-item', [priceList.id, itemId]));
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(amount);

    return (
        <AdminLayout title={`Price List: ${priceList.name}`}>
            <Head title={`Price List: ${priceList.name}`} />
            
            <div className="mb-6">
                <Button variant="ghost" className="mb-2 pl-0 hover:bg-transparent hover:text-gray-900" onClick={() => router.get(route('price-lists.index'))}>
                    <ArrowLeft className="w-4 h-4 mr-2" /> Back to Price Lists
                </Button>
                <PageHeader 
                    title={priceList.name} 
                    subtitle="Manage items and custom prices" 
                    actionLabel="Add Item" 
                    actionIcon={Plus} 
                    onAction={() => setModalOpen(true)} 
                />
            </div>

            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input type="text" placeholder="Search items in list..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-12 h-12 text-base bg-white border-gray-200" />
                </div>
            </div>

            <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="text-xs text-gray-500 uppercase bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-medium">Item Name</th>
                                <th className="px-6 py-4 font-medium">Code</th>
                                <th className="px-6 py-4 font-medium">Standard Price</th>
                                <th className="px-6 py-4 font-medium">Custom Price</th>
                                <th className="px-6 py-4 font-medium text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {filteredItems.length === 0 ? (
                                <tr><td colSpan="5" className="px-6 py-12 text-center text-gray-500">No items in this price list.</td></tr>
                            ) : filteredItems.map((item) => (
                                <tr key={item.id} className="bg-white hover:bg-gray-50 transition-colors">
                                    <td className="px-6 py-4 font-medium text-gray-900">{item.item?.name}</td>
                                    <td className="px-6 py-4 text-gray-500">{item.item?.code || '-'}</td>
                                    <td className="px-6 py-4 text-gray-500">{formatCurrency(item.item?.price || 0)}</td>
                                    <td className="px-6 py-4 font-bold text-purple-600">{formatCurrency(item.price)}</td>
                                    <td className="px-6 py-4 text-right">
                                        <Button variant="ghost" size="sm" onClick={() => handleRemoveItem(item.item_id)} className="text-red-500 hover:bg-red-50">
                                            <Trash2 className="w-4 h-4" />
                                        </Button>
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
                        <DialogTitle>Add Item to Price List</DialogTitle>
                        <DialogDescription>Select an item and set a custom price.</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="space-y-2">
                            <Label>Item</Label>
                            <Select value={newItem.item_id} onValueChange={(v) => setNewItem({ ...newItem, item_id: v })}>
                                <SelectTrigger><SelectValue placeholder="Select Item" /></SelectTrigger>
                                <SelectContent>
                                    {allItems?.map((item) => (
                                        <SelectItem key={item.id} value={item.id.toString()}>
                                            {item.name} (Std: {item.price})
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="space-y-2">
                            <Label>Custom Price</Label>
                            <Input 
                                type="number" 
                                value={newItem.price} 
                                onChange={(e) => setNewItem({ ...newItem, price: e.target.value })} 
                                placeholder="0.00" 
                                min="0" 
                                step="0.01" 
                            />
                        </div>
                        <DialogFooter>
                            <Button variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button onClick={handleAddItem} disabled={!newItem.item_id || !newItem.price} className="bg-purple-600 hover:bg-purple-700">Add Item</Button>
                        </DialogFooter>
                    </div>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
