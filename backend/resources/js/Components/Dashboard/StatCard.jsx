export default function StatCard({ name, value, change, icon: Icon, color }) {
    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex justify-between items-start mb-4">
                <p className="text-sm font-medium text-gray-500">{name}</p>
                <Icon className={`w-5 h-5 ${color}`} />
            </div>
            <h3 className="text-3xl font-bold text-gray-900 mb-1">{value}</h3>
            <p className="text-xs font-medium text-emerald-500 bg-emerald-50 inline-block px-2 py-1 rounded-full">
                {change} this month
            </p>
        </div>
    );
}
