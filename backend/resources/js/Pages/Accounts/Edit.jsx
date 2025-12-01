import { Head, Link, useForm } from '@inertiajs/react';

export default function Edit({ account }) {
    const { data, setData, put, delete: destroy, processing, errors } = useForm({
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

    const handleSubmit = (e) => {
        e.preventDefault();
        put(`/admin/accounts/${account.id}`);
    };

    const handleDelete = () => {
        if (confirm('Are you sure you want to delete this account? This action cannot be undone.')) {
            destroy(`/admin/accounts/${account.id}`);
        }
    };

    return (
        <>
            <Head title={`Edit ${account.name}`} />
            
            <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
                {/* Header */}
                <div className="bg-white/80 backdrop-blur-lg border-b border-gray-200/50">
                    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <Link
                                    href="/admin/accounts"
                                    className="inline-flex items-center justify-center w-10 h-10 rounded-xl bg-gray-100 hover:bg-gray-200 transition-colors"
                                >
                                    <svg className="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                                    </svg>
                                </Link>
                                <div>
                                    <h1 className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-blue-600 bg-clip-text text-transparent">
                                        Edit Account
                                    </h1>
                                    <p className="mt-1 text-sm text-gray-600">
                                        Update account information
                                    </p>
                                </div>
                            </div>
                            <button
                                type="button"
                                onClick={handleDelete}
                                className="px-4 py-2 bg-red-50 text-red-700 font-medium rounded-xl hover:bg-red-100 transition-colors"
                            >
                                Delete Account
                            </button>
                        </div>
                    </div>
                </div>

                {/* Form - Same as Create.jsx */}
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {/* Basic Information */}
                        <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 border border-gray-200/50 shadow-sm">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Basic Information</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Account Name *
                                    </label>
                                    <input
                                        type="text"
                                        value={data.name}
                                        onChange={e => setData('name', e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                        required
                                    />
                                    {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name}</p>}
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Formatted Name *
                                    </label>
                                    <input
                                        type="text"
                                        value={data.name_formatted}
                                        onChange={e => setData('name_formatted', e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                        required
                                    />
                                    {errors.name_formatted && <p className="mt-1 text-sm text-red-600">{errors.name_formatted}</p>}
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Description
                                    </label>
                                    <textarea
                                        value={data.desc}
                                        onChange={e => setData('desc', e.target.value)}
                                        rows={3}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Tax Settings */}
                        <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 border border-gray-200/50 shadow-sm">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Tax Settings</h2>
                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-3">
                                        Taxation Type *
                                    </label>
                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        {[
                                            { value: 1, label: 'No Tax', desc: 'No tax or composition' },
                                            { value: 2, label: 'Inclusive', desc: 'Tax included in price' },
                                            { value: 3, label: 'Exclusive', desc: 'Tax added to price' },
                                        ].map((option) => (
                                            <label
                                                key={option.value}
                                                className={`relative flex flex-col p-4 border-2 rounded-xl cursor-pointer transition-all ${
                                                    data.taxation_type === option.value
                                                        ? 'border-indigo-500 bg-indigo-50'
                                                        : 'border-gray-200 hover:border-gray-300'
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
                                                <span className="font-medium text-gray-900">{option.label}</span>
                                                <span className="text-sm text-gray-500 mt-1">{option.desc}</span>
                                            </label>
                                        ))}
                                    </div>
                                </div>

                                {data.taxation_type !== 1 && (
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Default Tax Percentage (%)
                                        </label>
                                        <input
                                            type="number"
                                            min="0"
                                            max="100"
                                            value={data.tax_rate}
                                            onChange={e => setData('tax_rate', parseInt(e.target.value))}
                                            className="w-full md:w-48 px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                        />
                                        {errors.tax_rate && <p className="mt-1 text-sm text-red-600">{errors.tax_rate}</p>}
                                    </div>
                                )}

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        GST Number
                                    </label>
                                    <input
                                        type="text"
                                        value={data.gst}
                                        onChange={e => setData('gst', e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                        placeholder="Enter GST number"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Contact Information */}
                        <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 border border-gray-200/50 shadow-sm">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Contact Information</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Phone Number
                                    </label>
                                    <input
                                        type="tel"
                                        value={data.call}
                                        onChange={e => setData('call', e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        WhatsApp Number
                                    </label>
                                    <input
                                        type="tel"
                                        value={data.whatsapp}
                                        onChange={e => setData('whatsapp', e.target.value)}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                    />
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Address
                                    </label>
                                    <textarea
                                        value={data.address}
                                        onChange={e => setData('address', e.target.value)}
                                        rows={3}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Additional Settings */}
                        <div className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 border border-gray-200/50 shadow-sm">
                            <h2 className="text-lg font-semibold text-gray-900 mb-4">Additional Settings</h2>
                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Footer Content
                                    </label>
                                    <textarea
                                        value={data.footer_content}
                                        onChange={e => setData('footer_content', e.target.value)}
                                        rows={3}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                        placeholder="Footer text for invoices and documents"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Financial Year Start
                                    </label>
                                    <input
                                        type="datetime-local"
                                        value={data.financial_year_start ? data.financial_year_start.replace(' ', 'T').substring(0, 16) : ''}
                                        onChange={e => setData('financial_year_start', e.target.value ? e.target.value.replace('T', ' ') + ':00' : '')}
                                        className="w-full px-4 py-3 bg-white border border-gray-300 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
                                    />
                                </div>

                                <div className="flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={data.signature}
                                        onChange={e => setData('signature', e.target.checked)}
                                        className="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                                    />
                                    <label className="ml-3 text-sm font-medium text-gray-700">
                                        Include signature in documents
                                    </label>
                                </div>
                            </div>
                        </div>

                        {/* Actions */}
                        <div className="flex justify-end gap-4">
                            <Link
                                href="/admin/accounts"
                                className="px-6 py-3 bg-gray-100 text-gray-700 font-medium rounded-xl hover:bg-gray-200 transition-colors"
                            >
                                Cancel
                            </Link>
                            <button
                                type="submit"
                                disabled={processing}
                                className="px-6 py-3 bg-gradient-to-r from-indigo-600 to-blue-600 text-white font-medium rounded-xl hover:from-indigo-700 hover:to-blue-700 transition-all duration-200 shadow-lg shadow-indigo-500/30 hover:shadow-xl hover:shadow-indigo-500/40 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {processing ? 'Updating...' : 'Update Account'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </>
    );
}
