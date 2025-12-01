import { Head, useForm } from '@inertiajs/react';
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Label } from "@/Components/ui/label";
import { Layers } from 'lucide-react';

export default function Login() {
    const { data, setData, post, processing, errors } = useForm({
        email: 'test@wyco.in',
        password: 'pass',
        remember: false,
    });

    const submit = (e) => {
        e.preventDefault();
        post('/login');
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <Head title="Log in" />

            <div className="w-full max-w-md space-y-8 p-8 bg-white rounded-2xl shadow-sm border border-gray-100">
                <div className="flex flex-col items-center justify-center text-center">
                    <div className="w-12 h-12 bg-lime-500 rounded-xl flex items-center justify-center text-white shadow-lg shadow-lime-500/30 mb-4">
                        <Layers className="w-7 h-7" />
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 tracking-tight">Welcome back</h2>
                    <p className="text-sm text-gray-500 mt-2">Sign in to your account to continue</p>
                </div>

                <form onSubmit={submit} className="space-y-6">
                    <div className="space-y-2">
                        <Label htmlFor="email">Email</Label>
                        <Input
                            id="email"
                            type="email"
                            value={data.email}
                            onChange={(e) => setData('email', e.target.value)}
                            className="bg-gray-50 border-gray-200 focus:bg-white transition-colors"
                            placeholder="name@example.com"
                            autoComplete="username"
                        />
                        {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
                    </div>

                    <div className="space-y-2">
                        <div className="flex items-center justify-between">
                            <Label htmlFor="password">Password</Label>
                            <a href="#" className="text-sm font-medium text-lime-600 hover:text-lime-700">
                                Forgot password?
                            </a>
                        </div>
                        <Input
                            id="password"
                            type="password"
                            value={data.password}
                            onChange={(e) => setData('password', e.target.value)}
                            className="bg-gray-50 border-gray-200 focus:bg-white transition-colors"
                            autoComplete="current-password"
                        />
                        {errors.password && <p className="text-sm text-red-500">{errors.password}</p>}
                    </div>

                    <Button 
                        type="submit" 
                        className="w-full bg-black hover:bg-gray-800 text-white h-11 rounded-xl font-medium transition-all"
                        disabled={processing}
                    >
                        {processing ? 'Signing in...' : 'Sign in'}
                    </Button>
                </form>

                <div className="text-center text-sm text-gray-500">
                    Don't have an account?{' '}
                    <a href="#" className="font-medium text-lime-600 hover:text-lime-700">
                        Contact support
                    </a>
                </div>
            </div>
        </div>
    );
}
