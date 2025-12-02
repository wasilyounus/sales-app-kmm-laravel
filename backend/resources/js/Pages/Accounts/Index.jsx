import { Head, usePage } from '@inertiajs/react';
import { useState, useEffect } from 'react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import Modal from '@/Components/Modal';
import AccountForm from '@/Components/AccountForm';
import Toast from '@/Components/Toast';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Badge } from "@/Components/ui/badge";
import { Search, Plus, Edit, FileText, Calculator, Building2 } from 'lucide-react';

export default function Index({ accounts, taxes }) {
    const { flash } = usePage().props;
    const [searchTerm, setSearchTerm] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingAccount, setEditingAccount] = useState(null);
    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState('');
    const [toastType, setToastType] = useState('success');

    useEffect(() => {
        if (flash?.success) {
            setToastMessage(flash.success);
            setToastType('success');
            setShowToast(true);
        } else if (flash?.error) {
            setToastMessage(flash.error);
            setToastType('error');
            setShowToast(true);
        }
    }, [flash]);

    const filteredAccounts = accounts.filter(account =>
        account.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        account.gst?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getTaxTypeName = (type) => {
        switch (type) {
            case 1: return 'No Tax';
            case 2: return 'Inclusive';
            case 3: return 'Exclusive';
            default: return 'Unknown';
        }
    };

    const getTaxTypeBorderColor = (type) => {
        switch (type) {
            case 1: return 'border-t-gray-400';
            case 2: return 'border-t-lime-500';
            case 3: return 'border-t-purple-500';
            default: return 'border-t-gray-400';
        }
    };

    const getTaxTypeBadgeStyle = (type) => {
        switch (type) {
            case 1: return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
            case 2: return 'bg-lime-100 text-lime-700 hover:bg-lime-200 border-lime-200';
            case 3: return 'bg-purple-100 text-purple-700 hover:bg-purple-200 border-purple-200';
            default: return 'bg-gray-100 text-gray-700 hover:bg-gray-200 border-gray-200';
        }
    };

    const openCreateModal = () => {
        setEditingAccount(null);
        setIsModalOpen(true);
    };

    const openEditModal = (account) => {
        setEditingAccount(account);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingAccount(null);
    };

    return (
        <AdminLayout title="Accounts">
            <Head title="Accounts" />

            <PageHeader
                icon={Building2}
                title="Account Management"
                subtitle="Manage your business accounts and settings"
                actionLabel="Create Account"
                actionIcon={Plus}
                onAction={openCreateModal}
            />

            {/* Search Bar */}
            <div className="mb-8 relative">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                <Input
                    type="text"
                    placeholder="Search accounts by name or GST..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-12 h-12 text-base bg-white border-gray-200 focus:border-lime-500 focus:ring-2 focus:ring-lime-500/20 transition-all duration-200"
                />
            </div>

            {/* Accounts Grid */}
            {filteredAccounts.length === 0 ? (
                <div className="text-center py-20">
                    <div className="inline-flex items-center justify-center w-20 h-20 rounded-2xl bg-gray-50 mb-4">
                        <Search className="w-10 h-10 text-gray-400" />
                    </div>
                    <h3 className="text-xl font-semibold text-gray-900 mb-2">No accounts found</h3>
                    <p className="text-gray-500 mb-6">Get started by creating your first account</p>
                    <Button onClick={openCreateModal} className="bg-black hover:bg-gray-800 text-white">
                        <Plus className="w-4 h-4 mr-2" />
                        Create Account
                    </Button>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {filteredAccounts.map((account) => (
                        <Card
                            key={account.id}
                            className={`group hover:shadow-xl hover:shadow-lime-500/5 transition-all duration-300 hover:-translate-y-1 border-gray-100 border-t-4 ${getTaxTypeBorderColor(account.taxation_type)} overflow-hidden bg-white`}
                        >
                            <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3 bg-gradient-to-br from-transparent to-lime-500/5">
                                <div className="space-y-1 flex-1">
                                    <CardTitle className="text-lg font-semibold text-gray-900 group-hover:text-lime-600 transition-colors flex items-center gap-2">
                                        <Building2 className="h-4 w-4 text-gray-400" />
                                        {account.name}
                                    </CardTitle>
                                    <p className="text-sm text-gray-500">{account.name_formatted}</p>
                                </div>
                                <Badge variant="secondary" className={`shrink-0 ${getTaxTypeBadgeStyle(account.taxation_type)}`}>
                                    {getTaxTypeName(account.taxation_type)}
                                </Badge>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                {account.desc && (
                                    <p className="text-sm text-gray-500 line-clamp-2 leading-relaxed">{account.desc}</p>
                                )}

                                <div className="space-y-2.5">
                                    {account.gst && (
                                        <div className="flex items-center text-sm p-2 rounded-lg bg-gray-50 group-hover:bg-lime-50/50 transition-colors">
                                            <FileText className="w-4 h-4 mr-2.5 text-gray-400 shrink-0" />
                                            <span className="text-gray-500 w-12 shrink-0">GST:</span>
                                            <span className="font-medium text-gray-900 truncate">{account.gst}</span>
                                        </div>
                                    )}
                                </div>

                                <div className="pt-3 border-t border-gray-100">
                                    <Button
                                        variant="secondary"
                                        className="w-full hover:bg-lime-50 hover:text-lime-600 hover:border-lime-200 transition-all duration-200"
                                        onClick={() => openEditModal(account)}
                                    >
                                        <Edit className="w-4 h-4 mr-2" />
                                        Edit Account
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}

            <Modal
                show={isModalOpen}
                onClose={closeModal}
                title={editingAccount ? `Edit ${editingAccount.name}` : 'Create New Account'}
            >
                <AccountForm
                    account={editingAccount}
                    taxes={taxes}
                    onSuccess={closeModal}
                    onCancel={closeModal}
                />
            </Modal>

            {showToast && (
                <Toast
                    message={toastMessage}
                    type={toastType}
                    onClose={() => setShowToast(false)}
                />
            )}
        </AdminLayout>
    );
}
