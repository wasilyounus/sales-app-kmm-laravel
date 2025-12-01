export default function ActivityItem({ icon: Icon, iconColor, iconBg, title, subtitle }) {
    return (
        <div className="flex gap-3 items-start">
            <div className={`p-2 ${iconBg} rounded-lg`}>
                <Icon className={`w-4 h-4 ${iconColor}`} />
            </div>
            <div>
                <p className="text-sm font-medium text-gray-900">{title}</p>
                <p className="text-xs text-gray-500">{subtitle}</p>
            </div>
        </div>
    );
}
