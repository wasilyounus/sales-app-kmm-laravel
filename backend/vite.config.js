import { defineConfig, loadEnv } from 'vite';
import laravel from 'laravel-vite-plugin';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import fs from 'fs';
import path from 'path';

// Load shared .env from root directory
function loadRootEnv() {
    const rootEnvPath = path.resolve(__dirname, '../.env');
    const config = {
        API_PROTOCOL: 'http',
        API_HOST: '192.168.1.174',
        API_PORT: '8000',
        APP_ENV: 'development',
        APP_DEBUG: 'true',
    };
    
    if (fs.existsSync(rootEnvPath)) {
        const content = fs.readFileSync(rootEnvPath, 'utf-8');
        content.split('\n').forEach(line => {
            if (line.trim() && !line.startsWith('#') && line.includes('=')) {
                const [key, ...valueParts] = line.split('=');
                config[key.trim()] = valueParts.join('=').trim();
            }
        });
    }
    
    return config;
}

const envConfig = loadRootEnv();

export default defineConfig({
    plugins: [
        laravel({
            input: ['resources/css/app.css', 'resources/js/app.jsx'],
            refresh: true,
        }),
        react(),
        tailwindcss(),
    ],
    define: {
        // Expose env variables to frontend code via import.meta.env
        'import.meta.env.VITE_API_PROTOCOL': JSON.stringify(envConfig.API_PROTOCOL),
        'import.meta.env.VITE_API_HOST': JSON.stringify(envConfig.API_HOST),
        'import.meta.env.VITE_API_PORT': JSON.stringify(envConfig.API_PORT),
        'import.meta.env.VITE_API_BASE_URL': JSON.stringify(
            `${envConfig.API_PROTOCOL}://${envConfig.API_HOST}:${envConfig.API_PORT}/api/`
        ),
        'import.meta.env.VITE_APP_ENV': JSON.stringify(envConfig.APP_ENV),
        'import.meta.env.VITE_APP_DEBUG': JSON.stringify(envConfig.APP_DEBUG === 'true'),
    },
    server: {
        host: '0.0.0.0', // Listen on all network interfaces
        port: 5173,
        strictPort: true,
        cors: true, // Enable CORS
        hmr: {
            host: envConfig.API_HOST, // Use configured IP for HMR
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './resources/js/test/setup.jsx',
    },
});
