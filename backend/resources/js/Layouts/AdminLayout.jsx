import { Link, usePage, router } from '@inertiajs/react';
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
import { Menu, Bell, Settings, LayoutDashboard, Users, Package, ShoppingCart, FileText, LogOut, Search, ChevronRight, Layers, Building2 } from 'lucide-react';
import AccountSwitcher from '@/Components/AccountSwitcher';

export default function AdminLayout({ children, title }) {
    const { shared } = usePage().props;
    const accounts = shared?.accounts || [];

    const navigation = [
        { name: 'Dashboard', href: '/admin/dashboard', icon: LayoutDashboard },
        { name: 'Sales', href: '/admin/sales', icon: FileText },
        { name: 'Items', href: '/admin/items', icon: Package },
        { name: 'Parties', href: '/admin/parties', icon: Users },
        { name: 'Accounts', href: '/admin/accounts', icon: Building2 },
        { name: 'Reports', href: '/admin/reports', icon: Layers },
    ];

    const handleLogout = () => {
        router.post('/logout');
    };

    const SidebarContent = () => (
        <div className="flex flex-col h-full bg-white border-r border-gray-100">
            {/* Logo Section */}
            <div className="p-6 flex items-center gap-3">
                <div className="w-8 h-8 bg-lime-500 rounded-lg flex items-center justify-center text-white shadow-lg shadow-lime-500/30">
                    <Layers className="w-5 h-5" />
                </div>
                <span className="text-xl font-bold text-gray-900 tracking-tight">Acme Inc.</span>
            </div>

            {/* Navigation */}
            <nav className="flex-1 px-4 space-y-1 mt-4">
                {navigation.map((item) => {
                    const isActive = window.location.pathname === item.href;
                    const Icon = item.icon;
                    return (
                        <Link
                            key={item.name}
                            href={item.href}
                            className={`group flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                                isActive
                                    ? 'bg-lime-50 text-lime-600 font-semibold'
                                    : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'
                            }`}
                        >
                            <Icon className={`w-5 h-5 ${isActive ? 'text-lime-600' : 'text-gray-400 group-hover:text-gray-600'}`} />
                            <span>{item.name}</span>
                        </Link>
                    );
                })}
            </nav>

            {/* User Profile */}
            <div className="p-4 border-t border-gray-100">
                <Link 
                    href="/admin/settings" 
                    className="flex items-center gap-3 px-4 py-3 text-gray-500 hover:bg-gray-50 hover:text-gray-900 rounded-xl transition-all duration-200 mb-2"
                >
                    <Settings className="w-5 h-5" />
                    <span className="font-medium">Settings</span>
                </Link>
                
                <button 
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-gray-500 hover:bg-red-50 hover:text-red-600 rounded-xl transition-all duration-200 mb-4"
                >
                    <LogOut className="w-5 h-5" />
                    <span className="font-medium">Log Out</span>
                </button>

                <div className="border-t border-gray-100 pt-4">
                    <div className="flex items-center gap-3 px-2">
                        <Avatar className="h-10 w-10 border-2 border-white shadow-sm bg-lime-100">
                            <AvatarImage src="" />
                            <AvatarFallback className="text-lime-700 font-bold">AD</AvatarFallback>
                        </Avatar>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-bold text-gray-900 truncate">Admin User</p>
                            <p className="text-xs text-gray-400 truncate">superadmin@mail.com</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50/50 flex">
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
                                <Button variant="ghost" size="icon" className="bg-white rounded-xl shadow-sm">
                                    <Menu className="h-6 w-6" />
                                </Button>
                            </SheetTrigger>
                            <SheetContent side="left" className="p-0 w-64">
                                <SidebarContent />
                            </SheetContent>
                        </Sheet>
                    </div>

                    <div className="hidden lg:block">
                        <AccountSwitcher accounts={accounts} />
                    </div>

                    <div className="flex items-center gap-4 ml-auto">
                        <div className="hidden md:flex items-center bg-white px-4 py-2.5 rounded-full shadow-sm border border-gray-100 w-80">
                            <Search className="w-4 h-4 text-gray-400 mr-2" />
                            <input 
                                type="text" 
                                placeholder="Search..." 
                                className="bg-transparent border-0 focus:ring-0 text-sm w-full p-0 placeholder:text-gray-400"
                            />
                        </div>
                        <Button variant="ghost" size="icon" className="bg-white rounded-full h-10 w-10 shadow-sm border border-gray-100 relative">
                            <Bell className="h-5 w-5 text-gray-600" />
                            <span className="absolute top-2.5 right-3 h-2 w-2 bg-red-500 rounded-full border-2 border-white"></span>
                        </Button>
                    </div>
                </header>

                {/* Page Content */}
                <main className="flex-1 px-8 pb-8">
                    {children}
                </main>
            </div>
        </div>
    );
}
