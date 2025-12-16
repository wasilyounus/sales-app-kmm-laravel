<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}" class="h-full">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>VTC Sales App</title>
    <link rel="preconnect" href="https://fonts.bunny.net">
    <link href="https://fonts.bunny.net/css?family=instrument-sans:400,500,600,700&display=swap" rel="stylesheet" />
    @vite(['resources/css/app.css', 'resources/js/app.jsx'])
    <!-- Note: app.jsx or app.js depending on setup, original file had logic, assuming app.jsx/js based on list_dir -->
</head>

<body
    class="h-full bg-background text-foreground font-sans antialiased selection:bg-secondary selection:text-secondary-foreground flex flex-col">

    <div
        class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8 bg-background transition-colors duration-300">
        <div class="sm:mx-auto sm:w-full sm:max-w-sm">
            <!-- Branding Icon -->
            <div
                class="mx-auto h-20 w-20 rounded-2xl bg-primary flex items-center justify-center shadow-lg shadow-primary/20 rotate-3 hover:rotate-0 transition-transform duration-300">
                <svg class="h-10 w-10 text-secondary" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <!-- Abstract Geometric Shape or Dashboard Icon -->
                    <path fill-rule="evenodd" d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"
                        clip-rule="evenodd" />
                </svg>
            </div>

            <h2 class="mt-8 text-center text-2xl font-bold leading-9 tracking-tight text-primary dark:text-secondary">
                Sales App
            </h2>
            <p class="mt-2 text-center text-sm font-medium tracking-wider text-muted-foreground uppercase">
                by Wy Co
            </p>
        </div>

        <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
            <div class="space-y-4">
                @if (Route::has('login'))
                    @auth
                        <a href="{{ url('/dashboard') }}"
                            class="group flex w-full justify-center rounded-lg bg-primary px-3 py-3 text-sm font-semibold leading-6 text-primary-foreground shadow-sm hover:bg-primary/90 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary transition-all">
                            <span class="flex items-center gap-2">
                                Dashboard
                                <svg class="h-4 w-4 text-secondary group-hover:translate-x-1 transition-transform" fill="none"
                                    viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round"
                                        d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
                                </svg>
                            </span>
                        </a>
                    @else
                        <a href="{{ route('login') }}"
                            class="flex w-full justify-center rounded-lg bg-primary px-3 py-3 text-sm font-semibold leading-6 text-primary-foreground shadow-sm hover:bg-primary/90 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary transition-all">
                            Log in
                        </a>

                        @if (Route::has('register'))
                            <a href="{{ route('register') }}"
                                class="flex w-full justify-center rounded-lg bg-white dark:bg-zinc-900 border border-input px-3 py-3 text-sm font-semibold leading-6 text-foreground shadow-sm hover:bg-muted focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary transition-all">
                                Register
                            </a>
                        @endif
                    @endauth
                @endif
            </div>

            <div class="mt-16 border-t border-border/50 pt-8">
                <p class="text-center text-xs leading-5 text-muted-foreground">
                    &copy; {{ date('Y') }} VTC Sales App. <br />All rights reserved.
                </p>
            </div>
        </div>
    </div>

    <!-- Theme Toggle / Footer Elements if needed -->
    <script>
        // Simple script to support system theme preference if not handled by app.js
        if (localStorage.theme === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
            document.documentElement.classList.add('dark')
        } else {
            document.documentElement.classList.remove('dark')
        }
    </script>
</body>

</html>