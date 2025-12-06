import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, ArrowUpRight, ArrowDownRight, Wallet, CreditCard } from 'lucide-react';
import { debounce } from 'lodash';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/Components/ui/select";
import { Textarea } from "@/Components/ui/textarea";

export default function Index({ transactions, parties, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);

    const { data, setData, post, processing, reset, errors } = useForm({
        party_id: '',
        amount: '',
        date: new Date().toISOString().split('T')[0],
        type: 'received', // received or paid
        method: 'cash', // cash, cheque, upi, neft
        comment: '',
    });

    const handleSearch = useCallback(debounce((value) => {
        router.get(route('payments.index'), { search: value }, { preserveState: true, replace: true });
    }, 300), []);

    const onSearchChange = (e) => { setSearchTerm(e.target.value); handleSearch(e.target.value); };

    const openCreateModal = () => {
        reset();
        setModalOpen(true);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        post(route('payments.store'), {
            onSuccess: () => { setModalOpen(false); reset(); },
        });
    };

    const formatCurrency = (amount) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(amount);

    return (
        <AdminLayout title="Payments">
            <Head title="Payments" />
            <PageHeader 
                icon={Wallet} 
                title="Payments" 
                subtitle="Record and track payments" 
                actionLabel="New Payment" 
                actionIcon={Plus} 
                onAction={openCreateModal} 
            />

            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input type="text" placeholder="Search payments..." value={searchTerm} onChange={onSearchChange} className="pl-12 h-12 text-base bg-white border-gray-200" />
                </div>
            </div>

            <Card className="border-gray-100 shadow-sm overflow-hidden bg-white">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="text-xs text-gray-500 uppercase bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-medium">Date</th>
                                <th className="px-6 py-4 font-medium">Party</th>
                                <th className="px-6 py-4 font-medium">Type</th>
                                <th className="px-6 py-4 font-medium">Amount</th>
                                <th className="px-6 py-4 font-medium">Comment</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {transactions?.data?.length === 0 ? (
                                <tr><td colSpan="5" className="px-6 py-12 text-center text-gray-500">No transactions found.</td></tr>
                            ) : transactions?.data?.map((t) => (
                                <tr key={t.id} className="bg-white hover:bg-gray-50 transition-colors">
                                    <td className="px-6 py-4 text-gray-500">{t.date}</td>
                                    <td className="px-6 py-4 font-medium text-gray-900">{t.party_name}</td>
                                    <td className="px-6 py-4">
                                        <Badge variant="outline" className={t.is_received ? 'bg-green-100 text-green-700 border-green-200' : 'bg-red-100 text-red-700 border-red-200'}>
                                            {t.is_received ? <ArrowDownRight className="w-3 h-3 mr-1" /> : <ArrowUpRight className="w-3 h-3 mr-1" />}
                                            {t.type_label}
                                        </Badge>
                                    </td>
                                    <td className={`px-6 py-4 font-bold ${t.is_received ? 'text-green-600' : 'text-red-600'}`}>
                                        {t.is_received ? '+' : '-'}{formatCurrency(t.amount)}
                                    </td>
                                    <td className="px-6 py-4 text-gray-500 truncate max-w-xs">{t.comment}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </Card>

            <Dialog open={modalOpen} onOpenChange={setModalOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Record Payment</DialogTitle>
                        <DialogDescription>Record a payment received or paid.</DialogDescription>
                    </DialogHeader>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Type</Label>
                                <Select value={data.type} onValueChange={(v) => setData('type', v)}>
                                    <SelectTrigger><SelectValue /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="received">Received (In)</SelectItem>
                                        <SelectItem value="paid">Paid (Out)</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label>Method</Label>
                                <Select value={data.method} onValueChange={(v) => setData('method', v)}>
                                    <SelectTrigger><SelectValue /></SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="cash">Cash</SelectItem>
                                        <SelectItem value="cheque">Cheque</SelectItem>
                                        <SelectItem value="upi">UPI</SelectItem>
                                        <SelectItem value="neft">NEFT</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label>Party</Label>
                            <Select value={data.party_id} onValueChange={(v) => setData('party_id', v)}>
                                <SelectTrigger><SelectValue placeholder="Select Party" /></SelectTrigger>
                                <SelectContent>
                                    {parties?.map((p) => <SelectItem key={p.id} value={p.id.toString()}>{p.name}</SelectItem>)}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Amount</Label>
                                <Input type="number" value={data.amount} onChange={(e) => setData('amount', e.target.value)} min="0" step="0.01" />
                            </div>
                            <div className="space-y-2">
                                <Label>Date</Label>
                                <Input type="date" value={data.date} onChange={(e) => setData('date', e.target.value)} />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label>Comment (Optional)</Label>
                            <Textarea value={data.comment} onChange={(e) => setData('comment', e.target.value)} />
                        </div>

                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-blue-600 hover:bg-blue-700">Save Payment</Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
