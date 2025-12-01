import { useForm } from '@inertiajs/react';
import { useEffect } from 'react';
import { Input } from "@/Components/ui/input"
import { Label } from "@/Components/ui/label"
import { Button } from "@/Components/ui/button"
import { Textarea } from "@/Components/ui/textarea"
import { Checkbox } from "@/Components/ui/checkbox"

export default function AccountForm({ account = null, onSuccess, onCancel }) {
    const isEditing = !!account;
    
    const { data, setData, post, put, processing, errors, reset } = useForm({
        name: account?.name || '',
        name_formatted: account?.name_formatted || '',
        desc: account?.desc || '',
        taxation_type: account?.taxation_type || 1,
        tax_rate: account?.tax_rate || 0,
        gst: account?.gst || '',
        address: account?.address || '',
        call: account?.call || '',
        whatsapp: account?.whatsapp || '',
        footer_content: account?.footer_content || '',
        signature: account?.signature || false,
        financial_year_start: account?.financial_year_start || '',
    });

    useEffect(() => {
        if (account) {
            setData({
                name: account.name || '',
                name_formatted: account.name_formatted || '',
                desc: account.desc || '',
                taxation_type: account.taxation_type || 1,
                tax_rate: account.tax_rate || 0,
                gst: account.gst || '',
                address: account.address || '',
                call: account.call || '',
                whatsapp: account.whatsapp || '',
                footer_content: account.footer_content || '',
                signature: account.signature || false,
                financial_year_start: account.financial_year_start || '',
            });
        } else {
            reset();
        }
    }, [account]);

    const handleSubmit = (e) => {
        e.preventDefault();
        
        if (isEditing) {
            put(`/admin/accounts/${account.id}`, {
                onSuccess: () => {
                    reset();
                    onSuccess();
                }
            });
        } else {
            post('/admin/accounts', {
                onSuccess: () => {
                    reset();
                    onSuccess();
                }
            });
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            {/* Basic Information */}
            <div className="bg-gray-50 rounded-xl p-6 border border-gray-200">
                <h4 className="text-sm font-semibold text-gray-900 uppercase tracking-wider mb-4">Basic Information</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2">
                        <Label htmlFor="name">Account Name *</Label>
                        <Input
                            id="name"
                            value={data.name}
                            onChange={e => setData('name', e.target.value)}
                            required
                        />
                        {errors.name && <p className="text-sm text-red-600">{errors.name}</p>}
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="name_formatted">Formatted Name *</Label>
                        <Input
                            id="name_formatted"
                            value={data.name_formatted}
                            onChange={e => setData('name_formatted', e.target.value)}
                            required
                        />
                        {errors.name_formatted && <p className="text-sm text-red-600">{errors.name_formatted}</p>}
                    </div>

                    <div className="md:col-span-2 space-y-2">
                        <Label htmlFor="desc">Description</Label>
                        <Textarea
                            id="desc"
                            value={data.desc}
                            onChange={e => setData('desc', e.target.value)}
                            rows={3}
                        />
                    </div>
                </div>
            </div>

            {/* Tax Settings */}
            <div className="bg-gray-50 rounded-xl p-6 border border-gray-200">
                <h4 className="text-sm font-semibold text-gray-900 uppercase tracking-wider mb-4">Tax Settings</h4>
                <div className="space-y-4">
                    <div className="space-y-3">
                        <Label>Taxation Type *</Label>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            {[
                                { value: 1, label: 'No Tax', desc: 'No tax' },
                                { value: 2, label: 'Inclusive', desc: 'Tax included' },
                                { value: 3, label: 'Exclusive', desc: 'Tax added' },
                            ].map((option) => (
                                <label
                                    key={option.value}
                                    className={`relative flex flex-col p-3 border-2 rounded-lg cursor-pointer transition-all ${
                                        data.taxation_type === option.value
                                            ? 'border-primary bg-primary/5'
                                            : 'border-input hover:border-primary/50 bg-background'
                                    }`}
                                >
                                    <input
                                        type="radio"
                                        name="taxation_type"
                                        value={option.value}
                                        checked={data.taxation_type === option.value}
                                        onChange={e => setData('taxation_type', parseInt(e.target.value))}
                                        className="sr-only"
                                    />
                                    <span className="font-medium text-foreground">{option.label}</span>
                                    <span className="text-xs text-muted-foreground mt-1">{option.desc}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    {data.taxation_type !== 1 && (
                        <div className="space-y-2">
                            <Label htmlFor="tax_rate">Default Tax Percentage (%)</Label>
                            <Input
                                id="tax_rate"
                                type="number"
                                min="0"
                                max="100"
                                value={data.tax_rate}
                                onChange={e => setData('tax_rate', parseInt(e.target.value))}
                                className="w-full md:w-48"
                            />
                            {errors.tax_rate && <p className="text-sm text-red-600">{errors.tax_rate}</p>}
                        </div>
                    )}

                    <div className="space-y-2">
                        <Label htmlFor="gst">GST Number</Label>
                        <Input
                            id="gst"
                            value={data.gst}
                            onChange={e => setData('gst', e.target.value)}
                            placeholder="Enter GST number"
                        />
                    </div>
                </div>
            </div>

            {/* Contact Information */}
            <div className="bg-gray-50 rounded-xl p-6 border border-gray-200">
                <h4 className="text-sm font-semibold text-gray-900 uppercase tracking-wider mb-4">Contact Information</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-2">
                        <Label htmlFor="call">Phone Number</Label>
                        <Input
                            id="call"
                            type="tel"
                            value={data.call}
                            onChange={e => setData('call', e.target.value)}
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="whatsapp">WhatsApp Number</Label>
                        <Input
                            id="whatsapp"
                            type="tel"
                            value={data.whatsapp}
                            onChange={e => setData('whatsapp', e.target.value)}
                        />
                    </div>

                    <div className="md:col-span-2 space-y-2">
                        <Label htmlFor="address">Address</Label>
                        <Textarea
                            id="address"
                            value={data.address}
                            onChange={e => setData('address', e.target.value)}
                            rows={3}
                        />
                    </div>
                </div>
            </div>

            {/* Additional Settings */}
            <div className="bg-gray-50 rounded-xl p-6 border border-gray-200">
                <h4 className="text-sm font-semibold text-gray-900 uppercase tracking-wider mb-4">Additional Settings</h4>
                <div className="space-y-4">
                    <div className="space-y-2">
                        <Label htmlFor="footer_content">Footer Content</Label>
                        <Textarea
                            id="footer_content"
                            value={data.footer_content}
                            onChange={e => setData('footer_content', e.target.value)}
                            rows={3}
                            placeholder="Footer text for invoices and documents"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="financial_year_start">Financial Year Start</Label>
                        <Input
                            id="financial_year_start"
                            type="datetime-local"
                            value={data.financial_year_start ? data.financial_year_start.replace(' ', 'T').substring(0, 16) : ''}
                            onChange={e => setData('financial_year_start', e.target.value ? e.target.value.replace('T', ' ') + ':00' : '')}
                        />
                    </div>

                    <div className="flex items-center space-x-2">
                        <Checkbox
                            id="signature"
                            checked={data.signature}
                            onCheckedChange={(checked) => setData('signature', checked)}
                        />
                        <Label htmlFor="signature" className="font-medium cursor-pointer">
                            Include signature in documents
                        </Label>
                    </div>
                </div>
            </div>

            {/* Actions */}
            <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
                <Button
                    type="button"
                    variant="outline"
                    onClick={onCancel}
                >
                    Cancel
                </Button>
                <Button
                    type="submit"
                    disabled={processing}
                    className="bg-gradient-to-r from-indigo-600 to-blue-600 hover:from-indigo-700 hover:to-blue-700 text-white shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 transition-all duration-200"
                >
                    {processing ? (isEditing ? 'Updating...' : 'Creating...') : (isEditing ? 'Update Account' : 'Create Account')}
                </Button>
            </div>
        </form>
    );
}
