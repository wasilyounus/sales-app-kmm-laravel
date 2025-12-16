import { Link, usePage, router } from '@inertiajs/react';
import { useState, useEffect } from 'react';
import { Button } from "@/Components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "@/Components/ui/sheet";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/Components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/Components/ui/avatar";
import { Menu, Bell, Settings, LayoutDashboard, Users, Package, ShoppingCart, FileText, LogOut, Search, ChevronRight, Layers, Building2, PackageOpen, ClipboardList, Wallet, Tags, Truck, Archive, Warehouse } from 'lucide-react';
import CompanySwitcher from '@/Components/CompanySwitcher';
import CompanySelectionModal from '@/Components/CompanySelectionModal';

export default function AdminLayout({ children, title }) {
    const { currentCompany, flash, auth } = usePage().props;
    const [showCompanyModal, setShowCompanyModal] = useState(false);

    useEffect(() => {
        // Show company selection modal if no company is selected
        if (!currentCompany && auth?.user) {
            setShowCompanyModal(true);
        }
        // Also show if flagged by backend
        if (flash?.show_company_selection) {
            setShowCompanyModal(true);
        }
    }, [currentCompany, flash, auth]);

    const handleCompanySelected = (companyId) => {
        setShowCompanyModal(false);
        // Reload the page to apply the new company
        router.reload();
    };

    const navigation = [
        { name: 'Dashboard', href: '/admin/dashboard', icon: LayoutDashboard },
        { name: 'Quotes', href: '/admin/quotes', icon: FileText },
        { name: 'Sales', href: '/admin/sales', icon: ShoppingCart },
        ...(currentCompany?.enable_delivery_notes ? [{ name: 'Delivery Notes', href: '/admin/delivery-notes', icon: Truck }] : []),
        { name: 'Purchases', href: '/admin/purchases', icon: PackageOpen },
        ...(currentCompany?.enable_grns ? [{ name: 'GRNs', href: '/admin/grns', icon: Archive }] : []),
        { name: 'Orders', href: '/admin/orders', icon: ClipboardList },
        { name: 'Payments', href: '/admin/payments', icon: Wallet },
        { name: 'Price Lists', href: '/admin/price-lists', icon: Tags },
        { name: 'Inventory', href: '/admin/inventory', icon: Warehouse },
        { name: 'Items', href: '/admin/items', icon: Package },
        { name: 'Parties', href: '/admin/parties', icon: Users },
        { name: 'Companies', href: '/admin/companies', icon: Building2 },
        { name: 'Reports', href: '/admin/reports', icon: Layers },
    ];

    const handleLogout = () => {
        router.post('/logout');
    };

    const SidebarContent = () => (
        <div className="flex flex-col h-full bg-background border-r border-border">
            {/* Company Section */}
            <div className="p-4 border-b border-border mb-4">
                <CompanySwitcher currentCompany={currentCompany} />
            </div>

            <nav className="flex-1 px-4 space-y-1 mt-4">
                {navigation.map((item) => {
                    const isActive = window.location.pathname === item.href;
                    const Icon = item.icon;
                    return (
                        <Link
                            key={item.name}
                            href={item.href}
                            className={`group flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${isActive
                                ? 'bg-primary/10 text-primary font-bold'
                                : 'text-muted-foreground hover:bg-muted/50 hover:text-foreground'
                                }`}
                        >
                            <Icon className={`w-5 h-5 ${isActive ? 'text-primary' : 'text-muted-foreground group-hover:text-foreground'}`} />
                            <span className="font-medium">{item.name}</span>
                        </Link>
                    );
                })}
            </nav>

            {/* User Profile */}
            <div className="p-4 border-t border-border">
                <Link
                    href="/admin/settings"
                    className="flex items-center gap-3 px-4 py-3 text-muted-foreground hover:bg-muted/50 hover:text-foreground rounded-xl transition-all duration-200 mb-2"
                >
                    <Settings className="w-5 h-5" />
                    <span className="font-medium">Settings</span>
                </Link>

                <button
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-muted-foreground hover:bg-destructive/10 hover:text-destructive rounded-xl transition-all duration-200 mb-4"
                >
                    <LogOut className="w-5 h-5" />
                    <span className="font-medium">Log Out</span>
                </button>

                <div className="border-t border-border pt-4">
                    <div className="flex items-center gap-3 px-2">
                        <Avatar className="h-10 w-10 border-2 border-background shadow-sm bg-primary/10">
                            <AvatarImage src={auth?.user?.avatar_url} />
                            <AvatarFallback className="text-primary font-bold">
                                {auth?.user?.name ? auth.user.name.substring(0, 2).toUpperCase() : 'AD'}
                            </AvatarFallback>
                        </Avatar>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-bold text-foreground truncate">{auth?.user?.name || 'Admin User'}</p>
                            <p className="text-xs text-muted-foreground truncate">{auth?.user?.email || 'admin@example.com'}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-muted/20 flex">
            {/* Desktop Sidebar */}
            <aside className="hidden lg:block fixed top-0 left-0 z-40 h-screen w-64">
                <SidebarContent />
            </aside>

            {/* Main Content */}
            <div className="lg:ml-64 flex-1 flex flex-col min-h-screen">
                {/* Header */}
                <header className="h-20 flex items-center justify-between px-8 bg-transparent">
                    <div className="flex items-center gap-4 lg:hidden">
                        <Sheet>
                            <SheetTrigger asChild>
                                <Button variant="ghost" size="icon" className="bg-background rounded-xl shadow-sm border border-border">
                                    <Menu className="h-6 w-6" />
                                </Button>
                            </SheetTrigger>
                            <SheetContent side="left" className="p-0 w-64">
                                <SidebarContent />
                            </SheetContent>
                        </Sheet>
                    </div>

                    <div className="flex items-center gap-4 ml-auto">
                        <div className="hidden md:flex items-center bg-background px-4 py-2.5 rounded-full shadow-sm border border-border w-80 focus-within:ring-2 focus-within:ring-ring">
                            <Search className="w-4 h-4 text-muted-foreground mr-2" />
                            <input
                                type="text"
                                placeholder="Search..."
                                className="bg-transparent border-0 focus:ring-0 text-sm w-full p-0 placeholder:text-muted-foreground focus:outline-none"
                            />
                        </div>
                        <Button variant="ghost" size="icon" className="bg-background rounded-full h-10 w-10 shadow-sm border border-border relative">
                            <Bell className="h-5 w-5 text-muted-foreground" />
                            <span className="absolute top-2.5 right-3 h-2 w-2 bg-destructive rounded-full border-2 border-background"></span>
                        </Button>
                    </div>
                </header>

                {/* Page Content */}
                <main className="flex-1 px-8 pb-8">
                    {children}
                </main>
            </div>

            {/* Company Selection Modal */}
            <CompanySelectionModal
                open={showCompanyModal}
                onCompanySelected={handleCompanySelected}
            />
        </div>
    );
}
