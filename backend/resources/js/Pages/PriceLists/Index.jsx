import { Head, router, useForm } from '@inertiajs/react';
import { useState, useCallback } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Search, Plus, Tags, Edit, Trash2, Eye } from 'lucide-react';
import { debounce } from 'lodash';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/Components/ui/dialog";
import { Label } from "@/Components/ui/label";

export default function Index({ priceLists, filters }) {
    const [searchTerm, setSearchTerm] = useState(filters?.search || '');
    const [modalOpen, setModalOpen] = useState(false);
    const [editingList, setEditingList] = useState(null);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    const { data, setData, post, put, processing, reset, errors } = useForm({
        name: '',
    });

    const handleSearch = useCallback(debounce((value) => {
        router.get(route('price-lists.index'), { search: value }, { preserveState: true, replace: true });
    }, 300), []);

    const onSearchChange = (e) => { setSearchTerm(e.target.value); handleSearch(e.target.value); };

    const openCreateModal = () => {
        setEditingList(null);
        setData({ name: '' });
        setModalOpen(true);
    };

    const openEditModal = (list) => {
        setEditingList(list);
        setData({ name: list.name });
        setModalOpen(true);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const options = { onSuccess: () => { setModalOpen(false); reset(); } };
        if (editingList) {
            put(route('price-lists.update', editingList.id), options);
        } else {
            post(route('price-lists.store'), options);
        }
    };

    const handleDelete = () => {
        router.delete(route('price-lists.destroy', deleteConfirm.id), {
            onSuccess: () => setDeleteConfirm(null),
        });
    };

    return (
        <AdminLayout title="Price Lists">
            <Head title="Price Lists" />
            <PageHeader 
                icon={Tags} 
                title="Price Lists" 
                subtitle="Manage custom price lists" 
                actionLabel="New Price List" 
                actionIcon={Plus} 
                onAction={openCreateModal} 
            />

            <div className="flex flex-col sm:flex-row gap-4 mb-6">
                <div className="relative flex-1">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input type="text" placeholder="Search price lists..." value={searchTerm} onChange={onSearchChange} className="pl-12 h-12 text-base bg-white border-gray-200" />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {priceLists?.data?.length === 0 ? (
                    <div className="col-span-full text-center py-12 text-gray-500">No price lists found.</div>
                ) : priceLists?.data?.map((list) => (
                    <Card key={list.id} className="border-gray-100 shadow-sm bg-white hover:shadow-md transition-shadow">
                        <CardContent className="p-4">
                            <div className="flex justify-between items-start mb-3">
                                <div>
                                    <h3 className="font-bold text-gray-900 text-lg">{list.name}</h3>
                                    <p className="text-sm text-gray-500">{list.items_count} items</p>
                                </div>
                                <div className="p-2 bg-purple-100 rounded-lg"><Tags className="w-5 h-5 text-purple-600" /></div>
                            </div>
                            <div className="flex justify-end items-center pt-3 border-t border-gray-50 gap-2">
                                <Button variant="outline" size="sm" onClick={() => router.get(route('price-lists.show', list.id))}>
                                    <Eye className="w-4 h-4 mr-1" /> Manage Items
                                </Button>
                                <Button variant="ghost" size="sm" onClick={() => openEditModal(list)}><Edit className="w-4 h-4" /></Button>
                                <Button variant="ghost" size="sm" onClick={() => setDeleteConfirm(list)} className="text-red-500"><Trash2 className="w-4 h-4" /></Button>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <Dialog open={modalOpen} onOpenChange={setModalOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editingList ? 'Edit Price List' : 'Create Price List'}</DialogTitle>
                        <DialogDescription>Enter a name for the price list.</DialogDescription>
                    </DialogHeader>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label>Name</Label>
                            <Input value={data.name} onChange={(e) => setData('name', e.target.value)} placeholder="e.g. Wholesale, VIP" />
                        </div>
                        <DialogFooter>
                            <Button type="button" variant="outline" onClick={() => setModalOpen(false)}>Cancel</Button>
                            <Button type="submit" disabled={processing} className="bg-purple-600 hover:bg-purple-700">Save</Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>

            <Dialog open={!!deleteConfirm} onOpenChange={() => setDeleteConfirm(null)}>
                <DialogContent>
                    <DialogHeader><DialogTitle>Delete Price List</DialogTitle><DialogDescription>Are you sure? This cannot be undone.</DialogDescription></DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
                        <Button variant="destructive" onClick={handleDelete}>Delete</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </AdminLayout>
    );
}
