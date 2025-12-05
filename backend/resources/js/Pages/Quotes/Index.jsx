import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { 
    Search, Plus, FileText, Calendar, DollarSign, Edit, Trash2, X
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

export default function Index({ quotes, parties, items, taxes, stats, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingQuote, setEditingQuote] = useState(null);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    const { data, setData, post, put, processing, errors, reset } = useForm({
        party_id: '',
        date: new Date().toISOString().split('T')[0],
        items: [{ item_id: '', price: '', qty: 1, tax_id: '' }],
    });

    const handleSearch = useCallback(
        debounce((value) => {
            router.get(route('quotes.index'), { search: value }, { preserveState: true, replace: true });
        }, 300),
        []
    );

    const onSearchChange = (e) => {
        setSearchTerm(e.target.value);
        handleSearch(e.target.value);
    };

    const openCreateModal = () => {
        reset();
        setData({
            party_id: '',
            date: new Date().toISOString().split('T')[0],
            items: [{ item_id: '', price: '', qty: 1, tax_id: '' }],
        });
        setEditingQuote(null);
        setModalOpen(true);
    };

    const openEditModal = (quote) => {
        setEditingQuote(quote);
        setData({
            party_id: quote.party_id.toString(),
            date: quote.date,
            items: quote.items.map(item => ({
                item_id: item.item_id.toString(),
                price: item.price.toString(),
                qty: item.qty,
                tax_id: item.tax_id ? item.tax_id.toString() : '',
            })),
        });
        setModalOpen(true);
    };

    const addItemRow = () => {
        setData('items', [...data.items, { item_id: '', price: '', qty: 1, tax_id: '' }]);
    };

    const removeItemRow = (index) => {
        if (data.items.length > 1) {
            setData('items', data.items.filter((_, i) => i !== index));
        }
    };

    const updateItem = (index, field, value) => {
        const newItems = [...data.items];
        newItems[index][field] = value;
        
        // Auto-fill tax when item is selected
        if (field === 'item_id') {
            const selectedItem = items.find(i => i.id.toString() === value);
            if (selectedItem && selectedItem.tax_id) {
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
        if (editingQuote) {
            put(route('quotes.update', editingQuote.id), options);
        } else {
            post(route('quotes.store'), options);
        }
    };

    const handleDelete = () => {
        router.delete(route('quotes.destroy', deleteConfirm.id), {
            onSuccess: () => setDeleteConfirm(null),
        });
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', {
        style: 'currency', currency: 'INR', maximumFractionDigits: 0
    }).format(amount);

    return (
        <AdminLayout title="Quotes">
            <Head title="Quotes" />

            <PageHeader
                icon={FileText}
                title="Quote Preparation"
                subtitle="Create and manage quotations for customers"
                actionLabel="New Quote"
                actionIcon={Plus}
                onAction={openCreateModal}
            />

            {/* Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="border-l-4 border-l-violet-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Total Quotes</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">{stats?.total || 0}</h3>
                            </div>
                            <div className="p-2 bg-violet-100 rounded-lg"><FileText className="w-5 h-5 text-violet-600" /></div>
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
                <Card className="border-l-4 border-l-lime-500 shadow-sm bg-white">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Total Value</p>
                                <h3 className="text-2xl font-bold mt-2 text-gray-900">{formatCurrency(stats?.total_value || 0)}</h3>
                            </div>
                            <div className="p-2 bg-lime-100 rounded-lg"><DollarSign className="w-5 h-5 text-lime-600" /></div>
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
                        placeholder="Search quotes..."
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
                                <th className="px-6 py-4">Quote #</th>
                                <th className="px-6 py-4">Party</th>
                                <th className="px-6 py-4">Date</th>
                                <th className="px-6 py-4">Items</th>
                                <th className="px-6 py-4">Subtotal</th>
                                <th className="px-6 py-4">Tax</th>
                                <th className="px-6 py-4">Total</th>
                                <th className="px-6 py-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {quotes.data.length === 0 ? (
                                <tr><td colSpan="8" className="px-6 py-12 text-center text-gray-500">No quotes found.</td></tr>
                            ) : (
                                quotes.data.map((quote) => (
                                    <tr key={quote.id} className="bg-white hover:bg-gray-50 group">
                                        <td className="px-6 py-4 font-medium text-gray-900">#{quote.id}</td>
                                        <td className="px-6 py-4 text-gray-900">{quote.party_name}</td>
                                        <td className="px-6 py-4 text-gray-500">{quote.date}</td>
                                        <td className="px-6 py-4 text-gray-500">{quote.items_count} items</td>
                                        <td className="px-6 py-4 text-gray-700">{formatCurrency(quote.subtotal)}</td>
                                        <td className="px-6 py-4 text-gray-500">{formatCurrency(quote.tax_amount)}</td>
                                        <td className="px-6 py-4 font-semibold text-gray-900">{formatCurrency(quote.total)}</td>
                                        <td className="px-6 py-4 text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button variant="ghost" size="sm" onClick={() => openEditModal(quote)}><Edit className="w-4 h-4" /></Button>
                                                <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(quote)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
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
                {quotes.data.map((quote) => (
                    <Card key={quote.id} className="border-gray-100 shadow-sm bg-white">
                        <CardContent className="p-4">
                            <div className="flex justify-between items-start mb-3">
                                <div>
                                    <p className="font-bold text-gray-900">#{quote.id}</p>
                                    <p className="text-sm text-gray-500">{quote.party_name}</p>
                                </div>
                                <Badge variant="outline" className="bg-violet-100 text-violet-700">{quote.items_count} items</Badge>
                            </div>
                            <div className="flex justify-between items-center pt-3 border-t border-gray-50">
                                <div>
                                    <span className="text-xs text-gray-500">{quote.date}</span>
                                    <p className="font-bold text-gray-900">{formatCurrency(quote.total)}</p>
                                </div>
                                <div className="flex gap-2">
                                    <Button variant="ghost" size="sm" onClick={() => openEditModal(quote)}><Edit className="w-4 h-4" /></Button>
                                    <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(quote)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
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
                        <DialogTitle>{editingQuote ? 'Edit Quote' : 'Create New Quote'}</DialogTitle>
                        <DialogDescription>{editingQuote ? 'Update the quote details.' : 'Fill in the details to create a new quotation.'}</DialogDescription>
                    </DialogHeader>

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Party *</Label>
                                <Select value={data.party_id} onValueChange={(value) => setData('party_id', value)}>
                                    <SelectTrigger><SelectValue placeholder="Select a party" /></SelectTrigger>
                                    <SelectContent>
                                        {parties.map((party) => (
                                            <SelectItem key={party.id} value={party.id.toString()}>{party.name}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Date *</Label>
                                <Input type="date" value={data.date} onChange={(e) => setData('date', e.target.value)} />
                            </div>
                        </div>

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
                                    <div key={index} className="grid grid-cols-12 gap-2 items-start p-3 bg-gray-50 rounded-lg">
                                        <div className="col-span-3">
                                            <Select value={item.item_id} onValueChange={(value) => updateItem(index, 'item_id', value)}>
                                                <SelectTrigger><SelectValue placeholder="Item" /></SelectTrigger>
                                                <SelectContent>
                                                    {items.map((i) => (
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
                            <Button type="submit" disabled={processing} className="bg-violet-600 hover:bg-violet-700">
                                {processing ? 'Saving...' : (editingQuote ? 'Update Quote' : 'Create Quote')}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>

            {/* Delete Confirmation */}
            <Dialog open={!!deleteConfirm} onOpenChange={() => setDeleteConfirm(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Delete Quote</DialogTitle>
                        <DialogDescription>Are you sure you want to delete Quote #{deleteConfirm?.id}?</DialogDescription>
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
