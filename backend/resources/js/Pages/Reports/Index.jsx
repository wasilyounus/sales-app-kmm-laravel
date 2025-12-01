import { Head } from '@inertiajs/react';
import AdminLayout from '@/Layouts/AdminLayout';
import PageHeader from '@/Components/PageHeader';
import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { FileText, Download, Calendar, BarChart3, PieChart, TrendingUp, ArrowUpRight } from 'lucide-react';

export default function Index() {
    const reports = [
        { name: 'Sales Summary', description: 'Daily sales performance and revenue analysis', type: 'Financial', icon: BarChart3 },
        { name: 'Inventory Status', description: 'Stock levels, low stock alerts, and valuation', type: 'Inventory', icon: PieChart },
        { name: 'Customer Activity', description: 'Top customers, purchase history, and trends', type: 'CRM', icon: TrendingUp },
        { name: 'Tax Report', description: 'GST collection and tax liability summary', type: 'Financial', icon: FileText },
    ];

    return (
        <AdminLayout title="Reports">
            <Head title="Reports" />

            {/* Header Actions */}
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-6 mb-8 bg-white p-6 rounded-xl border border-gray-100">
                <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-xl bg-lime-100 flex items-center justify-center">
                        <FileText className="w-6 h-6 text-lime-600" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold text-gray-900">Reports & Analytics</h1>
                        <p className="text-sm text-gray-500 mt-0.5">Generate and export business insights</p>
                    </div>
                </div>
                <div className="flex gap-3">
                    <Button variant="outline" className="border-gray-200 hover:bg-gray-50 text-gray-700">
                        <Calendar className="w-4 h-4 mr-2" />
                        Last 30 Days
                    </Button>
                    <Button 
                        className="bg-black hover:bg-gray-800 text-white transition-all duration-200"
                    >
                        <Download className="w-4 h-4 mr-2" />
                        Export All
                    </Button>
                </div>
            </div>

            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <Card className="bg-white border-gray-100 shadow-sm hover:shadow-md transition-shadow">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-gray-500 text-sm font-medium">Total Revenue</p>
                                <h3 className="text-3xl font-bold mt-2 text-gray-900">$45,230.00</h3>
                            </div>
                            <div className="p-2 bg-lime-100 rounded-lg">
                                <BarChart3 className="w-5 h-5 text-lime-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-sm text-lime-600 bg-lime-50 w-fit px-2 py-1 rounded-lg">
                            <ArrowUpRight className="w-4 h-4 mr-1" />
                            +12.5% vs last month
                        </div>
                    </CardContent>
                </Card>
                <Card className="bg-white border-gray-100 shadow-sm hover:shadow-md transition-shadow">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-gray-500 text-sm font-medium">Total Profit</p>
                                <h3 className="text-3xl font-bold mt-2 text-gray-900">$12,450.00</h3>
                            </div>
                            <div className="p-2 bg-purple-100 rounded-lg">
                                <TrendingUp className="w-5 h-5 text-purple-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-sm text-purple-600 bg-purple-50 w-fit px-2 py-1 rounded-lg">
                            <ArrowUpRight className="w-4 h-4 mr-1" />
                            +8.2% vs last month
                        </div>
                    </CardContent>
                </Card>
                <Card className="bg-white border-gray-100 shadow-sm hover:shadow-md transition-shadow">
                    <CardContent className="pt-6">
                        <div className="flex justify-between items-start">
                            <div>
                                <p className="text-gray-500 text-sm font-medium">Net Sales</p>
                                <h3 className="text-3xl font-bold mt-2 text-gray-900">1,245</h3>
                            </div>
                            <div className="p-2 bg-blue-100 rounded-lg">
                                <PieChart className="w-5 h-5 text-blue-600" />
                            </div>
                        </div>
                        <div className="flex items-center mt-4 text-sm text-blue-600 bg-blue-50 w-fit px-2 py-1 rounded-lg">
                            <ArrowUpRight className="w-4 h-4 mr-1" />
                            +15.3% vs last month
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Available Reports */}
            <h3 className="text-lg font-semibold mb-4 text-gray-900">Available Reports</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {reports.map((report) => {
                    const Icon = report.icon;
                    return (
                        <Card key={report.name} className="group hover:shadow-lg transition-all duration-300 border-gray-100 hover:border-lime-500/30 bg-white">
                            <CardContent className="p-6 flex items-start gap-4">
                                <div className="p-3 rounded-xl bg-gray-50 text-gray-400 group-hover:bg-lime-500 group-hover:text-white transition-colors duration-300">
                                    <Icon className="w-6 h-6" />
                                </div>
                                <div className="flex-1">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <h4 className="font-semibold text-lg text-gray-900 group-hover:text-lime-600 transition-colors">{report.name}</h4>
                                            <p className="text-sm text-gray-500 mt-1">{report.description}</p>
                                        </div>
                                        <span className="text-xs font-medium px-2 py-1 rounded-full bg-gray-100 text-gray-600">
                                            {report.type}
                                        </span>
                                    </div>
                                    <div className="mt-4 flex gap-2">
                                        <Button variant="outline" size="sm" className="border-gray-200 hover:border-lime-500 hover:text-lime-600 hover:bg-white text-gray-600">
                                            View Report
                                        </Button>
                                        <Button variant="ghost" size="sm" className="text-gray-400 hover:text-lime-600 hover:bg-lime-50">
                                            <Download className="w-4 h-4 mr-2" />
                                            PDF
                                        </Button>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    );
                })}
            </div>
        </AdminLayout>
    );
}
