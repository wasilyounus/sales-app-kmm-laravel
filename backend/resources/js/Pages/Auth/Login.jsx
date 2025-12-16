import { Head, useForm } from '@inertiajs/react';
import { Button } from "@/Components/ui/button";
import { Input } from "@/Components/ui/input";
import { Label } from "@/Components/ui/label";
import { LayoutDashboard } from 'lucide-react';

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
        <div className="min-h-screen flex items-center justify-center bg-background">
            <Head title="Log in" />

            <div className="w-full max-w-md space-y-8 p-8 bg-card rounded-2xl shadow-sm border border-border">
                <div className="flex flex-col items-center justify-center text-center">
                    <div className="w-16 h-16 bg-primary rounded-2xl flex items-center justify-center text-secondary shadow-lg shadow-primary/20 mb-6 rotate-3 hover:rotate-0 transition-transform duration-300">
                        <LayoutDashboard className="w-8 h-8" />
                    </div>
                    <h2 className="text-2xl font-bold text-primary dark:text-secondary tracking-tight">Sales App</h2>
                    <p className="text-sm text-muted-foreground mt-2 font-medium uppercase tracking-wider">by Wy Co</p>
                </div>

                <form onSubmit={submit} className="space-y-6">
                    <div className="space-y-2">
                        <Label htmlFor="email">Email</Label>
                        <Input
                            id="email"
                            type="email"
                            value={data.email}
                            onChange={(e) => setData('email', e.target.value)}
                            className="bg-muted/50 border-input focus:bg-background transition-colors"
                            placeholder="name@example.com"
                            autoComplete="username"
                        />
                        {errors.email && <p className="text-sm text-destructive">{errors.email}</p>}
                    </div>

                    <div className="space-y-2">
                        <div className="flex items-center justify-between">
                            <Label htmlFor="password">Password</Label>
                            {/* <a href="#" className="text-sm font-medium text-secondary hover:text-secondary/80">
                                Forgot password?
                            </a> */}
                        </div>
                        <Input
                            id="password"
                            type="password"
                            value={data.password}
                            onChange={(e) => setData('password', e.target.value)}
                            className="bg-muted/50 border-input focus:bg-background transition-colors"
                            autoComplete="current-password"
                        />
                        {errors.password && <p className="text-sm text-destructive">{errors.password}</p>}
                    </div>

                    <Button
                        type="submit"
                        className="w-full bg-primary hover:bg-primary/90 text-primary-foreground h-11 rounded-xl font-medium transition-all shadow-md hover:shadow-lg"
                        disabled={processing}
                    >
                        {processing ? 'Signing in...' : 'Sign in'}
                    </Button>
                </form>

                <div className="mt-8 pt-6 border-t border-border/50 text-center">
                    <p className="text-xs text-muted-foreground">
                        &copy; {new Date().getFullYear()} VTC Sales App. All rights reserved.
                    </p>
                </div>
            </div>
        </div>
    );
}
