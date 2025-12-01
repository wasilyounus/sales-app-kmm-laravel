export default function Welcome() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-gray-900 via-purple-900 to-violet-900 flex items-center justify-center p-6">
            <div className="max-w-2xl w-full bg-white/10 backdrop-blur-lg rounded-2xl shadow-2xl p-8 border border-white/20">
                <h1 className="text-5xl font-bold text-white mb-4 text-center">
                    Welcome to Laravel 12
                </h1>
                <p className="text-xl text-gray-200 text-center mb-8">
                    with Inertia.js + React + Tailwind CSS
                </p>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-white/5 rounded-lg p-4 border border-white/10 hover:bg-white/10 transition">
                        <h3 className="text-lg font-semibold text-white mb-2">⚡ Laravel 12</h3>
                        <p className="text-sm text-gray-300">Latest PHP framework</p>
                    </div>
                    <div className="bg-white/5 rounded-lg p-4 border border-white/10 hover:bg-white/10 transition">
                        <h3 className="text-lg font-semibold text-white mb-2">⚛️ React</h3>
                        <p className="text-sm text-gray-300">Modern UI library</p>
                    </div>
                    <div className="bg-white/5 rounded-lg p-4 border border-white/10 hover:bg-white/10 transition">
                        <h3 className="text-lg font-semibold text-white mb-2">🎨 Tailwind</h3>
                        <p className="text-sm text-gray-300">Utility-first CSS</p>
                    </div>
                </div>
            </div>
        </div>
    );
}
