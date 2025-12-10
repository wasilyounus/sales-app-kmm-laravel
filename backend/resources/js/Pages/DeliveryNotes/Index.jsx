import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback, useEffect } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Truck, Calendar, Edit, Trash2, X, FileText } from 'lucide-react';
import { debounce } from 'lodash';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/Components/ui/select";
import { Textarea } from "@/Components/ui/textarea";

export default function Index({ deliveryNotes, sales, items, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingDn, setEditingDn] = useState(null);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    const { data, setData, post, put, processing, reset, errors } = useForm({
        sale_id: '',
        date: new Date().toISOString().split('T')[0],
        vehicle_no: '',
        lr_no: '',
        notes: '',
        items: [{ item_id: '', quantity: '' }],
    });

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(route('delivery-notes.index'), { search: value }, { preserveState: true, replace: true });
        }, 300),
        []
    );

    const onSearchChange = (e) => {
        setSearchTerm(e.target.value);
        handleSearch(e.target.value);
    };

    const openCreateModal = () => {
        reset();
        setEditingDn(null);
        setModalOpen(true);
    };

    const openEditModal = (dn) => {
        setEditingDn(dn);
        setData({
            sale_id: dn.sale_id.toString(),
            date: dn.date,
            vehicle_no: dn.vehicle_no || '',
            lr_no: dn.lr_no || '',
            notes: dn.notes || '',
            items: dn.items.map(item => ({
                item_id: item.item_id.toString(),
                quantity: item.quantity.toString(),
            })),
        });
        setModalOpen(true);
    };

    const handleSaleChange = (value) => {
        setData('sale_id', value);
        // Auto-populate items from selected sale
        const selectedSale = sales.find(s => s.id.toString() === value);
        if (selectedSale && selectedSale.items) {
            const newItems = selectedSale.items.map(i => ({
                item_id: i.item_id.toString(),
                quantity: i.qty.toString()
            }));
            if (newItems.length > 0) {
                setData(data => ({ ...data, sale_id: value, items: newItems }));
            }
        }
    };

    const addItemRow = () => {
        setData('items', [...data.items, { item_id: '', quantity: '' }]);
    };

    const removeItemRow = (index) => {
        if (data.items.length > 1) {
            setData('items', data.items.filter((_, i) => i !== index));
        }
    };

    const updateItem = (index, field, value) => {
        const newItems = [...data.items];
        newItems[index][field] = value;
        setData('items', newItems);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const options = { onSuccess: () => { setModalOpen(false); reset(); } };
        if (editingDn) {
            put(route('delivery-notes.update', editingDn.id), options);
        } else {
            post(route('delivery-notes.store'), options);
        }
    };

    const handleDelete = () => {
        router.delete(route('delivery-notes.destroy', deleteConfirm.id), {
            onSuccess: () => setDeleteConfirm(null),
        });
    };

    return (
        <AdminLayout title="Delivery Notes">
            <Head title="Delivery Notes" />

            <PageHeader
                icon={Truck}
                title="Delivery Notes"
                subtitle="Manage dispatch and delivery documentation"
                actionLabel="New Delivery Note"
                actionIcon={Plus}
                onAction={openCreateModal}
            />

            {/* Search */}
            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                        type="text"
                        placeholder="Search delivery notes..."
                        value={searchTerm}
                        onChange={onSearchChange}
                        className="pl-12 h-12 bg-white border-gray-200 focus:border-violet-500"
                    />
                </div>
            </div>

            {/* Table */}
            <Card className="hidden md:block border-gray-100 shadow-sm overflow-hidden bg-white">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="text-xs text-gray-500 uppercase bg-gray-50 border-b">
                            <tr>
                                <th className="px-6 py-4">DN #</th>
                                <th className="px-6 py-4">Date</th>
                                <th className="px-6 py-4">Sale / Party</th>
                                <th className="px-6 py-4">Vehicle / LR</th>
                                <th className="px-6 py-4">Items</th>
                                <th className="px-6 py-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {deliveryNotes.data.length === 0 ? (
                                <tr><td colSpan="6" className="px-6 py-12 text-center text-gray-500">No delivery notes found.</td></tr>
                            ) : (
                                deliveryNotes.data.map((dn) => (
                                    <tr key={dn.id} className="bg-white hover:bg-gray-50 group">
                                        <td className="px-6 py-4 font-medium text-gray-900">
                                            {dn.dn_no || `#${dn.id}`}
                                        </td>
                                        <td className="px-6 py-4 text-gray-500">{dn.date}</td>
                                        <td className="px-6 py-4">
                                            <div className="font-medium text-gray-900">{dn.party_name}</div>
                                            <div className="text-xs text-gray-500">Sale #{dn.sale_id}</div>
                                        </td>
                                        <td className="px-6 py-4 text-gray-500">
                                            {dn.vehicle_no && <div>{dn.vehicle_no}</div>}
                                            {dn.lr_no && <div className="text-xs">{dn.lr_no}</div>}
                                            {!dn.vehicle_no && !dn.lr_no && <span className="text-gray-400">-</span>}
                                        </td>
                                        <td className="px-6 py-4 text-gray-500">{dn.items_count} items</td>
                                        <td className="px-6 py-4 text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button variant="ghost" size="sm" onClick={() => openEditModal(dn)}><Edit className="w-4 h-4" /></Button>
                                                <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(dn)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </Card>

            {/* Mobile Cards */}
            <div className="md:hidden space-y-4">
                {deliveryNotes.data.map((dn) => (
                    <Card key={dn.id} className="border-gray-100 shadow-sm bg-white">
                        <CardContent className="p-4">
                            <div className="flex justify-between items-start mb-3">
                                <div>
                                    <p className="font-bold text-gray-900">{dn.dn_no || `#${dn.id}`}</p>
                                    <p className="text-sm text-gray-500">{dn.party_name}</p>
                                </div>
                                <Badge variant="outline" className="bg-violet-100 text-violet-700">{dn.items_count} items</Badge>
                            </div>
                            <div className="flex justify-between items-center pt-3 border-t border-gray-50">
                                <div>
                                    <span className="text-xs text-gray-500">{dn.date}</span>
                                    {dn.vehicle_no && <p className="text-xs text-gray-600 mt-1">Veh: {dn.vehicle_no}</p>}
                                </div>
                                <div className="flex gap-2">
                                    <Button variant="ghost" size="sm" onClick={() => openEditModal(dn)}><Edit className="w-4 h-4" /></Button>
                                    <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(dn)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Create/Edit Modal */}
            <Dialog open={modalOpen} onOpenChange={setModalOpen}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>{editingDn ? 'Edit Delivery Note' : 'Create Delivery Note'}</DialogTitle>
                        <DialogDescription>{editingDn ? 'Update delivery details.' : 'Create a new delivery note from a sale.'}</DialogDescription>
                    </DialogHeader>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Sale *</Label>
                                <Select value={data.sale_id} onValueChange={handleSaleChange} disabled={!!editingDn}>
                                    <SelectTrigger><SelectValue placeholder="Select a sale" /></SelectTrigger>
                                    <SelectContent>
                                        {sales.map((sale) => (
                                            <SelectItem key={sale.id} value={sale.id.toString()}>{sale.label}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Date *</Label>
                                <Input type="date" value={data.date} onChange={(e) => setData('date', e.target.value)} />
                            </div>
                            <div className="space-y-2">
                                <Label>Vehicle No</Label>
                                <Input type="text" placeholder="e.g. MH-02-AB-1234" value={data.vehicle_no} onChange={(e) => setData('vehicle_no', e.target.value)} />
                            </div>
                            <div className="space-y-2">
                                <Label>LR No</Label>
                                <Input type="text" placeholder="e.g. LR-98765" value={data.lr_no} onChange={(e) => setData('lr_no', e.target.value)} />
                            </div>
                            <div className="col-span-2 space-y-2">
                                <Label>Notes</Label>
                                <Textarea placeholder="Additional notes..." value={data.notes} onChange={(e) => setData('notes', e.target.value)} />
                            </div>
                        </div>

                        {/* Items */}
                        <div className="space-y-4">
                            <div className="flex justify-between items-center">
                                <Label className="text-base font-semibold">Dispatch Items</Label>
                                <Button type="button" variant="outline" size="sm" onClick={addItemRow}>
                                    <Plus className="w-4 h-4 mr-1" /> Add Item
                                </Button>
                            </div>

                            <div className="space-y-3 max-h-60 overflow-y-auto">
                                {data.items.map((item, index) => (
                                    <div key={index} className="grid grid-cols-12 gap-2 items-start p-3 bg-gray-50 rounded-lg">
                                        <div className="col-span-8">
                                            <Select value={item.item_id} onValueChange={(value) => updateItem(index, 'item_id', value)}>
                                                <SelectTrigger><SelectValue placeholder="Select Item" /></SelectTrigger>
                                                <SelectContent>
                                                    {items.map((i) => (
                                                        <SelectItem key={i.id} value={i.id.toString()}>{i.name}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className="col-span-3">
                                            <Input type="number" placeholder="Qty" value={item.quantity} onChange={(e) => updateItem(index, 'quantity', e.target.value)} min="0.001" step="0.001" />
                                        </div>
                                        <div className="col-span-1 text-right">
                                            {data.items.length > 1 && (
                                                <Button type="button" variant="ghost" size="sm" onClick={() => removeItemRow(index)} className="text-red-500 p-1">
                                                    <X className="w-4 h-4" />
                                                </Button>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-violet-600 hover:bg-violet-700">
                                {processing ? 'Saving...' : (editingDn ? 'Update Delivery' : 'Create Delivery')}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>

            {/* Delete Confirmation */}
            <Dialog open={!!deleteConfirm} onOpenChange={() => setDeleteConfirm(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Delete Delivery Note</DialogTitle>
                        <DialogDescription>Are you sure? This will reverse the stock deduction.</DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
                        <Button variant="destructive" onClick={handleDelete}>Delete</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
