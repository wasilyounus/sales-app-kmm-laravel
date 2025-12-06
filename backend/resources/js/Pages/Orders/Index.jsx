import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, ClipboardList, Calendar, DollarSign, Edit, Trash2, X } from 'lucide-react';
import { debounce } from 'lodash';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/Components/ui/select";

export default function Index({ orders, parties, items, taxes, stats, filters, taxSettings }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingOrder, setEditingOrder] = useState(null);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    const taxLevel = taxSettings?.level || 'item';
    const defaultTaxId = taxSettings?.default_tax_id?.toString() || '';

    const { data, setData, post, put, processing, reset } = useForm({
        party_id: '', date: new Date().toISOString().split('T')[0], tax_id: '',
        items: [{ item_id: '', price: '', qty: 1, tax_id: '' }],
    });

    const handleSearch = useCallback(debounce((value) => {
        router.get(route('orders.index'), { search: value }, { preserveState: true, replace: true });
    }, 300), []);

    const onSearchChange = (e) => { setSearchTerm(e.target.value); handleSearch(e.target.value); };

    const openCreateModal = () => {
        reset();
        const initialTaxId = taxLevel === 'account' ? defaultTaxId : '';
        setData({
            party_id: '', date: new Date().toISOString().split('T')[0],
            tax_id: taxLevel === 'bill' ? '' : initialTaxId,
            items: [{ item_id: '', price: '', qty: 1, tax_id: taxLevel === 'item' ? '' : initialTaxId }],
        });
        setEditingOrder(null); setModalOpen(true);
    };

    const openEditModal = (order) => {
        setEditingOrder(order);
        setData({
            party_id: order.party_id.toString(), date: order.date,
            tax_id: order.tax_id?.toString() || '',
            items: order.items.map(item => ({
                item_id: item.item_id.toString(), price: item.price.toString(),
                qty: item.qty, tax_id: item.tax_id?.toString() || '',
            })),
        });
        setModalOpen(true);
    };

    const addItemRow = () => {
        const itemTaxId = taxLevel === 'item' ? '' : (data.tax_id || defaultTaxId);
        setData('items', [...data.items, { item_id: '', price: '', qty: 1, tax_id: itemTaxId }]);
    };

    const removeItemRow = (index) => { if (data.items.length > 1) setData('items', data.items.filter((_, i) => i !== index)); };

    const updateItem = (index, field, value) => {
        const newItems = [...data.items];
        newItems[index][field] = value;
        if (field === 'item_id' && taxLevel === 'item') {
            const selectedItem = items?.find(i => i.id.toString() === value);
            if (selectedItem?.tax_id) newItems[index].tax_id = selectedItem.tax_id.toString();
        }
        setData('items', newItems);
    };

    const getTaxRate = (taxId) => taxes?.find(t => t.id.toString() === taxId)?.rate || 0;
    const calculateSubtotal = () => data.items.reduce((sum, item) => sum + ((parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0)), 0);
    const calculateTaxAmount = () => data.items.reduce((sum, item) => {
        const lineTotal = (parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0);
        return sum + (lineTotal * getTaxRate(item.tax_id) / 100);
    }, 0);
    const calculateTotal = () => calculateSubtotal() + calculateTaxAmount();

    const handleSubmit = (e) => {
        e.preventDefault();
        const options = { onSuccess: () => { setModalOpen(false); reset(); } };
        editingOrder ? put(route('orders.update', editingOrder.id), options) : post(route('orders.store'), options);
    };

    const handleDelete = () => { router.delete(route('orders.destroy', deleteConfirm.id), { onSuccess: () => setDeleteConfirm(null) }); };
    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount);

    return (
        <AdminLayout title="Orders">
            <Head title="Orders" />
            <PageHeader icon={ClipboardList} title="Order Management" subtitle="Track customer orders and fulfillment" actionLabel="New Order" actionIcon={Plus} onAction={openCreateModal} />

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="border-l-4 border-l-indigo-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">Total Orders</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.total || 0}</h3></div>
                            <div className="p-2 bg-indigo-100 rounded-lg"><ClipboardList className="w-5 h-5 text-indigo-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-blue-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">This Month</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.this_month || 0}</h3></div>
                            <div className="p-2 bg-blue-100 rounded-lg"><Calendar className="w-5 h-5 text-blue-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-teal-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div><p className="text-sm font-medium text-gray-500">Total Value</p><h3 className="text-2xl font-bold mt-2 text-gray-900">{formatCurrency(stats?.total_value || 0)}</h3></div>
                            <div className="p-2 bg-teal-100 rounded-lg"><DollarSign className="w-5 h-5 text-teal-600" /></div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input type="text" placeholder="Search orders..." value={searchTerm} onChange={onSearchChange} className="pl-12 h-12 text-base bg-white border-gray-200" />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {orders?.data?.length === 0 ? (
                    <div className="col-span-full text-center py-12 text-gray-500">No orders found.</div>
                ) : orders?.data?.map((order) => (
                    <Card key={order.id} className="border-gray-100 shadow-sm bg-white hover:shadow-md transition-shadow">
                        <CardContent className="p-4">
                            <div className="flex justify-between items-start mb-3">
                                <div><p className="font-bold text-gray-900">#{order.id}</p><p className="text-sm text-gray-500">{order.party_name}</p></div>
                                <Badge variant="outline" className="bg-indigo-100 text-indigo-700">{order.items_count} items</Badge>
                            </div>
                            <div className="flex justify-between items-center pt-3 border-t border-gray-50">
                                <div><span className="text-xs text-gray-500">{order.date}</span><p className="font-bold text-gray-900">{formatCurrency(order.total)}</p></div>
                                <div className="flex gap-2">
                                    <Button variant="ghost" size="sm" onClick={() => openEditModal(order)}><Edit className="w-4 h-4" /></Button>
                                    <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(order)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <Dialog open={modalOpen} onOpenChange={setModalOpen}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>{editingOrder ? 'Edit Order' : 'Create New Order'}</DialogTitle>
                        <DialogDescription>{editingOrder ? 'Update order details.' : 'Fill in order details.'}</DialogDescription>
                    </DialogHeader>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Customer *</Label>
                                <Select value={data.party_id} onValueChange={(v) => setData('party_id', v)}>
                                    <SelectTrigger><SelectValue placeholder="Select customer" /></SelectTrigger>
                                    <SelectContent>{parties?.map((p) => <SelectItem key={p.id} value={p.id.toString()}>{p.name}</SelectItem>)}</SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Date *</Label>
                                <Input type="date" value={data.date} onChange={(e) => setData('date', e.target.value)} />
                            </div>
                        </div>

                        {taxLevel === 'bill' && (
                            <div className="p-4 bg-indigo-50 rounded-lg border border-indigo-200">
                                <Label>Tax for all items</Label>
                                <Select value={data.tax_id || "none"} onValueChange={(v) => {
                                    const newTaxId = v === 'none' ? '' : v;
                                    setData('tax_id', newTaxId);
                                    setData('items', data.items.map(item => ({ ...item, tax_id: newTaxId })));
                                }}>
                                    <SelectTrigger><SelectValue placeholder="Select tax" /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="none">No Tax</SelectItem>
                                        {taxes?.map((t) => <SelectItem key={t.id} value={t.id.toString()}>{t.name} ({t.rate}%)</SelectItem>)}
                                    </SelectContent>
                                </Select>
                            </div>
                        )}

                        {taxLevel === 'account' && defaultTaxId && (
                            <div className="p-3 bg-blue-50 rounded-lg border border-blue-200 text-sm">
                                <span className="text-blue-700">Using account default tax: <strong>{taxes?.find(t => t.id.toString() === defaultTaxId)?.name}</strong></span>
                            </div>
                        )}

                        <div className="space-y-4">
                            <div className="flex justify-between items-center">
                                <Label className="text-base font-semibold">Items</Label>
                                <Button type="button" variant="outline" size="sm" onClick={addItemRow}><Plus className="w-4 h-4 mr-1" /> Add Item</Button>
                            </div>
                            <div className="space-y-3 max-h-60 overflow-y-auto">
                                {data.items.map((item, index) => (
                                    <div key={index} className={`grid gap-2 items-start p-3 bg-gray-50 rounded-lg ${taxLevel === 'item' ? 'grid-cols-12' : 'grid-cols-10'}`}>
                                        <div className={taxLevel === 'item' ? 'col-span-3' : 'col-span-4'}>
                                            <Select value={item.item_id} onValueChange={(v) => updateItem(index, 'item_id', v)}>
                                                <SelectTrigger><SelectValue placeholder="Item" /></SelectTrigger>
                                                <SelectContent>{items?.map((i) => <SelectItem key={i.id} value={i.id.toString()}>{i.name}</SelectItem>)}</SelectContent>
                                            </Select>
                                        </div>
                                        <div className="col-span-2"><Input type="number" placeholder="Price" value={item.price} onChange={(e) => updateItem(index, 'price', e.target.value)} min="0" step="0.01" /></div>
                                        <div className="col-span-2"><Input type="number" placeholder="Qty" value={item.qty} onChange={(e) => updateItem(index, 'qty', e.target.value)} min="0.01" step="0.01" /></div>
                                        {taxLevel === 'item' && (
                                            <div className="col-span-2">
                                                <Select value={item.tax_id || "none"} onValueChange={(v) => updateItem(index, 'tax_id', v === 'none' ? '' : v)}>
                                                    <SelectTrigger><SelectValue placeholder="Tax" /></SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="none">No Tax</SelectItem>
                                                        {taxes?.map((t) => <SelectItem key={t.id} value={t.id.toString()}>{t.name} ({t.rate}%)</SelectItem>)}
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        )}
                                        <div className="col-span-2 text-right font-medium pt-2 text-sm">{formatCurrency((parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0))}</div>
                                        <div className="col-span-1 text-right">
                                            {data.items.length > 1 && <Button type="button" variant="ghost" size="sm" onClick={() => removeItemRow(index)} className="text-red-500 p-1"><X className="w-4 h-4" /></Button>}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="border-t pt-4 space-y-2">
                            <div className="flex justify-between text-sm"><span className="text-gray-500">Subtotal</span><span>{formatCurrency(calculateSubtotal())}</span></div>
                            <div className="flex justify-between text-sm"><span className="text-gray-500">Tax</span><span>{formatCurrency(calculateTaxAmount())}</span></div>
                            <div className="flex justify-between text-lg font-bold pt-2 border-t"><span>Total</span><span>{formatCurrency(calculateTotal())}</span></div>
                        </div>

                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-indigo-600 hover:bg-indigo-700">
                                {processing ? 'Saving...' : (editingOrder ? 'Update' : 'Create')}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>

            <Dialog open={!!deleteConfirm} onOpenChange={() => setDeleteConfirm(null)}>
                <DialogContent>
                    <DialogHeader><DialogTitle>Delete Order</DialogTitle><DialogDescription>Are you sure? This cannot be undone.</DialogDescription></DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
                        <Button variant="destructive" onClick={handleDelete}>Delete</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
