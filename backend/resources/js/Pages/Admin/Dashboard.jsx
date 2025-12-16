import { Head, usePage } from '@inertiajs/react'; // Added usePage
import AdminLayout from '@/Layouts/AdminLayout';
import {
    TrendingUp,
    Users,
    Package,
    FileText,
    Plus,
    UserPlus,
    ShoppingCart,
    BarChart3
} from 'lucide-react';
import StatCard from '@/Components/Dashboard/StatCard';
import FinancialChart from '@/Components/Dashboard/FinancialChart';
import QuickActionButton from '@/Components/Dashboard/QuickActionButton';
import ActivityItem from '@/Components/Dashboard/ActivityItem';

export default function Dashboard() {
    const { auth } = usePage().props; // Get auth data
    const user = auth?.user;

    const stats = [
        { name: 'Total Sales', value: '$281,161', change: '+12.2%', icon: TrendingUp, color: 'text-primary' }, // Updated colors
        { name: 'Outstanding Invoices', value: '$42,850', change: '+3.5%', icon: FileText, color: 'text-secondary' },
        { name: 'Total Parties', value: '1,257', change: '+8.1%', icon: Users, color: 'text-primary' },
        { name: 'Items Sold', value: '5,846', change: '+5.7%', icon: Package, color: 'text-secondary' },
    ];

    const quickActions = [
        { name: 'New Sale', icon: Plus, color: 'text-primary', bg: 'bg-primary/10' },
        { name: 'Add Party', icon: UserPlus, color: 'text-secondary', bg: 'bg-secondary/10' },
        { name: 'New Item', icon: ShoppingCart, color: 'text-primary', bg: 'bg-primary/10' },
        { name: 'Run Report', icon: BarChart3, color: 'text-secondary', bg: 'bg-secondary/10' },
    ];

    // ... rest of component logic related to activities ...

    const recentActivities = [
        {
            icon: FileText,
            iconColor: 'text-primary',
            iconBg: 'bg-primary/10',
            title: 'Sale #INV-00122 completed',
            subtitle: 'by Michael Brown • 2 hours ago'
        },
        {
            icon: Users,
            iconColor: 'text-secondary',
            iconBg: 'bg-secondary/10',
            title: 'New party registered',
            subtitle: 'Creative Studio • 4 hours ago'
        },
        {
            icon: Package,
            iconColor: 'text-primary',
            iconBg: 'bg-primary/10',
            title: 'New item added',
            subtitle: 'Wireless Headphones • 1 day ago'
        }
    ];

    return (
        <AdminLayout title="Dashboard">
            <Head title="Dashboard" />

            <div className="space-y-8">
                {/* Welcome Section */}
                <div>
                    <h1 className="text-2xl font-bold text-foreground">Welcome back, {user?.name || 'Admin'}!</h1>
                    <p className="text-muted-foreground mt-1">Here's your business snapshot for today.</p>
                </div>

                {/* Stats Row */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                    {stats.map((stat) => (
                        <StatCard key={stat.name} {...stat} />
                    ))}
                </div>

                {/* Main Content Split */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Left Column: Financial Overview */}
                    <div className="lg:col-span-2">
                        <FinancialChart />
                    </div>

                    {/* Right Column: Quick Actions & Activity */}
                    <div className="space-y-8">
                        {/* Quick Actions */}
                        <div className="bg-card p-6 rounded-xl shadow-sm border border-border">
                            <h3 className="font-bold text-lg text-primary dark:text-secondary mb-4">Quick Actions</h3>
                            <div className="grid grid-cols-2 gap-4">
                                {quickActions.map((action) => (
                                    <QuickActionButton key={action.name} {...action} />
                                ))}
                            </div>
                        </div>

                        {/* Recent Activity */}
                        <div className="bg-card p-6 rounded-xl shadow-sm border border-border">
                            <h3 className="font-bold text-lg text-primary dark:text-secondary mb-4">Recent Activity</h3>
                            <div className="space-y-4">
                                {recentActivities.map((activity, index) => (
                                    <ActivityItem key={index} {...activity} />
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </AdminLayout>
    );
}