export default function QuickActionButton({ name, icon: Icon, color, bg, onClick }) {
    return (
        <button 
            onClick={onClick}
            className={`${bg} p-4 rounded-xl flex flex-col items-center justify-center gap-2 hover:brightness-95 transition-all group`}
        >
            <div className="p-2 bg-white rounded-full shadow-sm group-hover:scale-110 transition-transform">
                <Icon className={`w-5 h-5 ${color}`} />
            </div>
            <span className="text-xs font-semibold text-gray-700">{name}</span>
        </button>
    );
}
