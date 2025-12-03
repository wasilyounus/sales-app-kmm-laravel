import { useState } from 'react';
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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/Components/ui/select';

export default function ItemFormModal({ open, onClose, item = null, taxes, uqcs }) {
    const { data, setData, post, put, processing, errors, reset } = useForm({
        name: item?.name || '',
        alt_name: item?.alt_name || '',
        brand: item?.brand || '',
        size: item?.size || '',
        uqc: item?.uqc || (uqcs && uqcs.length > 0 ? uqcs[0].id : ''),
        hsn: item?.hsn || '',
        tax_id: item?.tax_id || null,
        opening_stock: '',
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        
        if (item) {
            put(`/admin/items/${item.id}`, {
                onSuccess: () => {
                    reset();
                    onClose();
                },
            });
        } else {
            post('/admin/items', {
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
                    <DialogTitle>{item ? 'Edit Item' : 'Create New Item'}</DialogTitle>
                    <DialogDescription>
                        {item ? 'Update item details below.' : 'Add a new item to your inventory.'}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        {/* Name */}
                        <div className="col-span-2 space-y-2">
                            <Label htmlFor="name">
                                Item Name <span className="text-red-500">*</span>
                            </Label>
                            <Input
                                id="name"
                                value={data.name}
                                onChange={(e) => setData('name', e.target.value)}
                                className={errors.name ? 'border-red-500' : ''}
                                placeholder="e.g., Laptop"
                            />
                            {errors.name && (
                                <p className="text-sm text-red-500">{errors.name}</p>
                            )}
                        </div>

                        {/* Brand */}
                        <div className="space-y-2">
                            <Label htmlFor="brand">Brand</Label>
                            <Input
                                id="brand"
                                value={data.brand}
                                onChange={(e) => setData('brand', e.target.value)}
                                placeholder="e.g., Dell"
                            />
                        </div>

                        {/* Size */}
                        <div className="space-y-2">
                            <Label htmlFor="size">Size / Variant</Label>
                            <Input
                                id="size"
                                value={data.size}
                                onChange={(e) => setData('size', e.target.value)}
                                placeholder="e.g., 15 inch"
                            />
                        </div>

                        {/* UQC */}
                        <div className="space-y-2">
                            <Label htmlFor="uqc">
                                Unit (UQC) <span className="text-red-500">*</span>
                            </Label>
                            <Select
                                value={data.uqc?.toString()}
                                onValueChange={(value) => setData('uqc', parseInt(value))}
                            >
                                <SelectTrigger className={errors.uqc ? 'border-red-500' : ''}>
                                    <SelectValue placeholder="Select unit" />
                                </SelectTrigger>
                                <SelectContent>
                                    {uqcs && uqcs.map((uqc) => (
                                        <SelectItem key={uqc.id} value={uqc.id.toString()}>
                                            {uqc.uqc}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                            {errors.uqc && (
                                <p className="text-sm text-red-500">{errors.uqc}</p>
                            )}
                        </div>

                        {/* HSN Code */}
                        <div className="space-y-2">
                            <Label htmlFor="hsn">HSN Code</Label>
                            <Input
                                id="hsn"
                                type="number"
                                value={data.hsn}
                                onChange={(e) => setData('hsn', e.target.value)}
                                placeholder="e.g., 8471"
                            />
                        </div>

                        {/* Tax */}
                        <div className="space-y-2">
                            <Label htmlFor="tax_id">Tax Scheme</Label>
                            <Select
                                value={data.tax_id?.toString() || 'none'}
                                onValueChange={(value) => setData('tax_id', value === 'none' ? null : parseInt(value))}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="No tax" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="none">None</SelectItem>
                                    {taxes && taxes.map((tax) => (
                                        <SelectItem key={tax.id} value={tax.id.toString()}>
                                            {tax.scheme_name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        {/* Opening Stock (only for new items) */}
                        {!item && (
                            <div className="space-y-2">
                                <Label htmlFor="opening_stock">Opening Stock</Label>
                                <Input
                                    id="opening_stock"
                                    type="number"
                                    step="0.01"
                                    value={data.opening_stock}
                                    onChange={(e) => setData('opening_stock', e.target.value)}
                                    placeholder="0"
                                />
                            </div>
                        )}
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
                            {processing ? 'Saving...' : (item ? 'Update Item' : 'Create Item')}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
