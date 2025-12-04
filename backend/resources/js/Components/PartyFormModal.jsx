import { useState, useEffect } from 'react';
import { useForm } from '@inertiajs/react';
import { Button } from '@/Components/ui/button';
import { Input } from '@/Components/ui/input';
import { Label } from '@/Components/ui/label';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/Components/ui/dialog';

export default function PartyFormModal({ open, onClose, party = null }) {
    const { data, setData, post, put, processing, errors, reset } = useForm({
        name: party?.name || '',
        phone: party?.phone || '',
        email: party?.email || '',
        tax_number: party?.tax_number || '',
        addresses: party?.addresses?.length > 0 ? party.addresses : [{
            line1: '',
            line2: '',
            city: '',
            state: '',
            pincode: '',
            country: ''
        }],
    });

    useEffect(() => {
        if (party) {
            setData({
                name: party.name || '',
                phone: party.phone || '',
                email: party.email || '',
                tax_number: party.tax_number || '',
                addresses: party.addresses?.length > 0 ? party.addresses : [{
                    line1: '',
                    line2: '',
                    city: '',
                    state: '',
                    pincode: '',
                    country: ''
                }],
            });
        } else {
            reset();
        }
    }, [party]);

    const handleAddressChange = (field, value) => {
        const newAddresses = [...data.addresses];
        newAddresses[0] = { ...newAddresses[0], [field]: value };
        setData('addresses', newAddresses);
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (party) {
            put(`/admin/parties/${party.id}`, {
                onSuccess: () => {
                    reset();
                    onClose();
                },
            });
        } else {
            post('/admin/parties', {
                onSuccess: () => {
                    reset();
                    onClose();
                },
            });
        }
    };

    const handleClose = () => {
        reset();
        onClose();
    };

    return (
        <Dialog open={open} onOpenChange={handleClose}>
            <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>{party ? 'Edit Party' : 'Create New Party'}</DialogTitle>
                    <DialogDescription>
                        {party ? 'Update party/customer details.' : 'Add a new party or customer.'}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        {/* Name */}
                        <div className="col-span-2 space-y-2">
                            <Label htmlFor="name">
                                Party Name <span className="text-red-500">*</span>
                            </Label>
                            <Input
                                id="name"
                                value={data.name}
                                onChange={(e) => setData('name', e.target.value)}
                                className={errors.name ? 'border-red-500' : ''}
                                placeholder="Company or Person Name"
                            />
                            {errors.name && (
                                <p className="text-sm text-red-500">{errors.name}</p>
                            )}
                        </div>

                        {/* Phone */}
                        <div className="space-y-2">
                            <Label htmlFor="phone">Phone Number</Label>
                            <Input
                                id="phone"
                                type="tel"
                                value={data.phone}
                                onChange={(e) => setData('phone', e.target.value)}
                                placeholder="+91 9876543210"
                            />
                        </div>

                        {/* Email */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                value={data.email}
                                onChange={(e) => setData('email', e.target.value)}
                                placeholder="contact@example.com"
                            />
                        </div>

                        {/* Tax Number */}
                        <div className="col-span-2 space-y-2">
                            <Label htmlFor="tax_number">Tax Number</Label>
                            <Input
                                id="tax_number"
                                value={data.tax_number}
                                onChange={(e) => setData('tax_number', e.target.value)}
                                placeholder="GSTIN / VAT Number"
                            />
                        </div>

                        {/* Address Section */}
                        <div className="col-span-2 border-t pt-4 mt-2">
                            <h3 className="text-sm font-medium mb-3">Address Details</h3>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="col-span-2 space-y-2">
                                    <Label htmlFor="line1">Address Line 1 <span className="text-red-500">*</span></Label>
                                    <Input
                                        id="line1"
                                        value={data.addresses[0].line1}
                                        onChange={(e) => handleAddressChange('line1', e.target.value)}
                                        placeholder="Street address, P.O. box"
                                    />
                                    {errors['addresses.0.line1'] && (
                                        <p className="text-sm text-red-500">{errors['addresses.0.line1']}</p>
                                    )}
                                </div>

                                <div className="col-span-2 space-y-2">
                                    <Label htmlFor="line2">Address Line 2</Label>
                                    <Input
                                        id="line2"
                                        value={data.addresses[0].line2}
                                        onChange={(e) => handleAddressChange('line2', e.target.value)}
                                        placeholder="Apartment, suite, unit, etc."
                                    />
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="city">City <span className="text-red-500">*</span></Label>
                                    <Input
                                        id="city"
                                        value={data.addresses[0].city}
                                        onChange={(e) => handleAddressChange('city', e.target.value)}
                                        placeholder="City"
                                    />
                                    {errors['addresses.0.city'] && (
                                        <p className="text-sm text-red-500">{errors['addresses.0.city']}</p>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="state">State <span className="text-red-500">*</span></Label>
                                    <Input
                                        id="state"
                                        value={data.addresses[0].state}
                                        onChange={(e) => handleAddressChange('state', e.target.value)}
                                        placeholder="State"
                                    />
                                    {errors['addresses.0.state'] && (
                                        <p className="text-sm text-red-500">{errors['addresses.0.state']}</p>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="pincode">Pincode <span className="text-red-500">*</span></Label>
                                    <Input
                                        id="pincode"
                                        value={data.addresses[0].pincode}
                                        onChange={(e) => handleAddressChange('pincode', e.target.value)}
                                        placeholder="ZIP / Postal Code"
                                    />
                                    {errors['addresses.0.pincode'] && (
                                        <p className="text-sm text-red-500">{errors['addresses.0.pincode']}</p>
                                    )}
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="country">Country <span className="text-red-500">*</span></Label>
                                    <Input
                                        id="country"
                                        value={data.addresses[0].country}
                                        onChange={(e) => handleAddressChange('country', e.target.value)}
                                        placeholder="Country"
                                    />
                                    {errors['addresses.0.country'] && (
                                        <p className="text-sm text-red-500">{errors['addresses.0.country']}</p>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            onClick={handleClose}
                            disabled={processing}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={processing}>
                            {processing ? 'Saving...' : (party ? 'Update Party' : 'Create Party')}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
