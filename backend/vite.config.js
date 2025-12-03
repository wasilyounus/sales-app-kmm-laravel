import { defineConfig } from 'vite';
import laravel from 'laravel-vite-plugin';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
    plugins: [
        laravel({
            input: ['resources/css/app.css', 'resources/js/app.jsx'],
            refresh: true,
        }),
        react(),
        tailwindcss(),
    ],
    server: {
        host: '0.0.0.0', // Listen on all network interfaces
        port: 5173,
        strictPort: true,
        cors: true, // Enable CORS
        hmr: {
            host: '192.168.1.131', // Your network IP
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './resources/js/test/setup.jsx',
    },
});
