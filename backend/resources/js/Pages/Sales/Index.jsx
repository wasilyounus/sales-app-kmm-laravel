import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { 
    Search, Plus, ShoppingCart, Calendar, DollarSign, Edit, Trash2, X
} from 'lucide-react';
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

export default function Index({ sales, parties, items, taxes, stats, filters, taxSettings }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingSale, setEditingSale] = useState(null);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    // Tax application level from account settings
    const taxLevel = taxSettings?.level || 'item';
    const defaultTaxId = taxSettings?.default_tax_id?.toString() || '';

    const { data, setData, post, put, processing, errors, reset } = useForm({
        party_id: '',
        date: new Date().toISOString().split('T')[0],
        invoice_no: '',
        tax_id: '',
        items: [{ item_id: '', price: '', qty: 1, tax_id: '' }],
    });

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(route('sales.index'), { search: value }, { preserveState: true, replace: true });
        }, 300),
        []
    );

    const onSearchChange = (e) => {
        setSearchTerm(e.target.value);
        handleSearch(e.target.value);
    };

    const openCreateModal = () => {
        reset();
        const initialTaxId = taxLevel === 'account' ? defaultTaxId : '';
        setData({
            party_id: '',
            date: new Date().toISOString().split('T')[0],
            invoice_no: '',
            tax_id: taxLevel === 'bill' ? '' : initialTaxId,
            items: [{ item_id: '', price: '', qty: 1, tax_id: taxLevel === 'item' ? '' : initialTaxId }],
        });
        setEditingSale(null);
        setModalOpen(true);
    };

    const openEditModal = (sale) => {
        setEditingSale(sale);
        setData({
            party_id: sale.party_id.toString(),
            date: sale.date,
            invoice_no: sale.invoice_no || '',
            tax_id: sale.tax_id ? sale.tax_id.toString() : '',
            items: sale.items.map(item => ({
                item_id: item.item_id.toString(),
                price: item.price.toString(),
                qty: item.qty,
                tax_id: item.tax_id ? item.tax_id.toString() : '',
            })),
        });
        setModalOpen(true);
    };

    const addItemRow = () => {
        const itemTaxId = taxLevel === 'item' ? '' : (data.tax_id || defaultTaxId);
        setData('items', [...data.items, { item_id: '', price: '', qty: 1, tax_id: itemTaxId }]);
    };

    const removeItemRow = (index) => {
        if (data.items.length > 1) {
            setData('items', data.items.filter((_, i) => i !== index));
        }
    };

    const updateItem = (index, field, value) => {
        const newItems = [...data.items];
        newItems[index][field] = value;
        
        if (field === 'item_id') {
            const selectedItem = items?.find(i => i.id.toString() === value);
            if (selectedItem && selectedItem.tax_id && taxLevel === 'item') {
                newItems[index].tax_id = selectedItem.tax_id.toString();
            }
        }
        
        setData('items', newItems);
    };

    const getTaxRate = (taxId) => {
        if (!taxId) return 0;
        const tax = taxes?.find(t => t.id.toString() === taxId);
        return tax ? tax.rate : 0;
    };

    const calculateSubtotal = () => {
        return data.items.reduce((sum, item) => {
            return sum + ((parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0));
        }, 0);
    };

    const calculateTaxAmount = () => {
        return data.items.reduce((sum, item) => {
            const lineTotal = (parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0);
            return sum + (lineTotal * getTaxRate(item.tax_id) / 100);
        }, 0);
    };

    const calculateTotal = () => calculateSubtotal() + calculateTaxAmount();

    const handleSubmit = (e) => {
        e.preventDefault();
        const options = { onSuccess: () => { setModalOpen(false); reset(); } };
        if (editingSale) {
            put(route('sales.update', editingSale.id), options);
        } else {
            post(route('sales.store'), options);
        }
    };

    const handleDelete = () => {
        router.delete(route('sales.destroy', deleteConfirm.id), {
            onSuccess: () => setDeleteConfirm(null),
        });
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', {
        style: 'currency', currency: 'INR', maximumFractionDigits: 0
    }).format(amount);

    return (
        <AdminLayout title="Sales">
            <Head title="Sales" />

            <PageHeader
                icon={ShoppingCart}
                title="Sales Management"
                subtitle="Track invoices and transactions"
                actionLabel="New Sale"
                actionIcon={Plus}
                onAction={openCreateModal}
            />

            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="border-l-4 border-l-lime-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Total Sales</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.total || 0}</h3>
                            </div>
                            <div className="p-2 bg-lime-100 rounded-lg"><ShoppingCart className="w-5 h-5 text-lime-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-blue-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">This Month</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.this_month || 0}</h3>
                            </div>
                            <div className="p-2 bg-blue-100 rounded-lg"><Calendar className="w-5 h-5 text-blue-600" /></div>
                        </div>
                    </CardContent>
                </Card>
                <Card className="border-l-4 border-l-emerald-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Total Value</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">{formatCurrency(stats?.total_value || 0)}</h3>
                            </div>
                            <div className="p-2 bg-emerald-100 rounded-lg"><DollarSign className="w-5 h-5 text-emerald-600" /></div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Search */}
            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                        type="text"
                        placeholder="Search by invoice number or customer..."
                        value={searchTerm}
                        onChange={onSearchChange}
                        className="pl-12 h-12 text-base bg-white border-gray-200"
                    />
                </div>
            </div>

            {/* Sales Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {sales?.data?.length === 0 ? (
                    <div className="col-span-full text-center py-12 text-gray-500">No sales found.</div>
                ) : sales?.data?.map((sale) => (
                    <Card key={sale.id} className="border-gray-100 shadow-sm bg-white hover:shadow-md transition-shadow">
                        <CardContent className="p-4">
                            <div className="flex justify-between items-start mb-3">
                                <div>
                                    <p className="font-bold text-gray-900">#{sale.invoice_no || sale.id}</p>
                                    <p className="text-sm text-gray-500">{sale.party_name}</p>
                                </div>
                                <Badge variant="outline" className="bg-lime-100 text-lime-700">{sale.items_count} items</Badge>
                            </div>
                            <div className="flex justify-between items-center pt-3 border-t border-gray-50">
                                <div>
                                    <span className="text-xs text-gray-500">{sale.date}</span>
                                    <p className="font-bold text-gray-900">{formatCurrency(sale.total)}</p>
                                </div>
                                <div className="flex gap-2">
                                    <Button variant="ghost" size="sm" onClick={() => openEditModal(sale)}><Edit className="w-4 h-4" /></Button>
                                    <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(sale)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
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
                        <DialogTitle>{editingSale ? 'Edit Sale' : 'Create New Sale'}</DialogTitle>
                        <DialogDescription>{editingSale ? 'Update the sale details.' : 'Fill in the details to create a new sale.'}</DialogDescription>
                    </DialogHeader>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-3 gap-4">
                            <div className="space-y-2">
                                <Label>Party *</Label>
                                <Select value={data.party_id} onValueChange={(value) => setData('party_id', value)}>
                                    <SelectTrigger><SelectValue placeholder="Select a party" /></SelectTrigger>
                                    <SelectContent>
                                        {parties?.map((party) => (
                                            <SelectItem key={party.id} value={party.id.toString()}>{party.name}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Date *</Label>
                                <Input type="date" value={data.date} onChange={(e) => setData('date', e.target.value)} />
                            </div>
                            <div className="space-y-2">
                                <Label>Invoice No</Label>
                                <Input type="text" value={data.invoice_no} onChange={(e) => setData('invoice_no', e.target.value)} placeholder="Auto-generated" />
                            </div>
                        </div>

                        {/* Bill-level Tax */}
                        {taxLevel === 'bill' && (
                            <div className="p-4 bg-lime-50 rounded-lg border border-lime-200">
                                <div className="space-y-2">
                                    <Label>Tax for all items</Label>
                                    <Select 
                                        value={data.tax_id || "none"} 
                                        onValueChange={(value) => {
                                            const newTaxId = value === 'none' ? '' : value;
                                            setData('tax_id', newTaxId);
                                            setData('items', data.items.map(item => ({ ...item, tax_id: newTaxId })));
                                        }}
                                    >
                                        <SelectTrigger><SelectValue placeholder="Select tax" /></SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="none">No Tax</SelectItem>
                                            {taxes?.map((t) => (
                                                <SelectItem key={t.id} value={t.id.toString()}>{t.name} ({t.rate}%)</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                            </div>
                        )}

                        {/* Account-level Tax info */}
                        {taxLevel === 'account' && defaultTaxId && (
                            <div className="p-3 bg-blue-50 rounded-lg border border-blue-200 text-sm">
                                <span className="text-blue-700">
                                    Using account default tax: <strong>{taxes?.find(t => t.id.toString() === defaultTaxId)?.name || 'Unknown'}</strong>
                                </span>
                            </div>
                        )}

                        {/* Items */}
                        <div className="space-y-4">
                            <div className="flex justify-between items-center">
                                <Label className="text-base font-semibold">Items</Label>
                                <Button type="button" variant="outline" size="sm" onClick={addItemRow}>
                                    <Plus className="w-4 h-4 mr-1" /> Add Item
                                </Button>
                            </div>

                            <div className="space-y-3 max-h-60 overflow-y-auto">
                                {data.items.map((item, index) => (
                                    <div key={index} className={`grid gap-2 items-start p-3 bg-gray-50 rounded-lg ${taxLevel === 'item' ? 'grid-cols-12' : 'grid-cols-10'}`}>
                                        <div className={taxLevel === 'item' ? 'col-span-3' : 'col-span-4'}>
                                            <Select value={item.item_id} onValueChange={(value) => updateItem(index, 'item_id', value)}>
                                                <SelectTrigger><SelectValue placeholder="Item" /></SelectTrigger>
                                                <SelectContent>
                                                    {items?.map((i) => (
                                                        <SelectItem key={i.id} value={i.id.toString()}>{i.name}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                        <div className="col-span-2">
                                            <Input type="number" placeholder="Price" value={item.price} onChange={(e) => updateItem(index, 'price', e.target.value)} min="0" step="0.01" />
                                        </div>
                                        <div className="col-span-2">
                                            <Input type="number" placeholder="Qty" value={item.qty} onChange={(e) => updateItem(index, 'qty', e.target.value)} min="0.01" step="0.01" />
                                        </div>
                                        {taxLevel === 'item' && (
                                            <div className="col-span-2">
                                                <Select value={item.tax_id || "none"} onValueChange={(value) => updateItem(index, 'tax_id', value === 'none' ? '' : value)}>
                                                    <SelectTrigger><SelectValue placeholder="Tax" /></SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="none">No Tax</SelectItem>
                                                        {taxes?.map((t) => (
                                                            <SelectItem key={t.id} value={t.id.toString()}>{t.name} ({t.rate}%)</SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        )}
                                        <div className="col-span-2 text-right font-medium pt-2 text-sm">
                                            {formatCurrency((parseFloat(item.price) || 0) * (parseFloat(item.qty) || 0))}
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

                        {/* Summary */}
                        <div className="border-t pt-4 space-y-2">
                            <div className="flex justify-between text-sm">
                                <span className="text-gray-500">Subtotal</span>
                                <span>{formatCurrency(calculateSubtotal())}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-gray-500">Tax</span>
                                <span>{formatCurrency(calculateTaxAmount())}</span>
                            </div>
                            <div className="flex justify-between text-lg font-bold pt-2 border-t">
                                <span>Total</span>
                                <span>{formatCurrency(calculateTotal())}</span>
                            </div>
                        </div>

                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-lime-600 hover:bg-lime-700">
                                {processing ? 'Saving...' : (editingSale ? 'Update Sale' : 'Create Sale')}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>

            {/* Delete Confirmation */}
            <Dialog open={!!deleteConfirm} onOpenChange={() => setDeleteConfirm(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Delete Sale</DialogTitle>
                        <DialogDescription>Are you sure you want to delete this sale? This action cannot be undone.</DialogDescription>
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
