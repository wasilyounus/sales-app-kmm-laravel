import { Card, CardContent } from "@/Components/ui/card";
import { ArrowUpRight } from 'lucide-react';

export default function StatsCard({ 
    label, 
    value, 
    icon: Icon, 
    iconColor = 'text-lime-600', 
    iconBg = 'bg-lime-100',
    borderColor = 'border-l-lime-500',
    change,
    changeColor = 'text-lime-600',
    changeBg = 'bg-lime-50'
}) {
    return (
        <Card className={`border-l-4 ${borderColor} shadow-sm bg-white`}>
            <CardContent className="pt-6">
                <div className="flex justify-between items-start">
                    <div>
                        <p className="text-sm font-medium text-gray-500">{label}</p>
                        <h3 className="text-2xl font-bold mt-2 text-gray-900">{value}</h3>
                    </div>
                    <div className={`p-2 ${iconBg} rounded-lg`}>
                        <Icon className={`w-5 h-5 ${iconColor}`} />
                    </div>
                </div>
                {change && (
                    <div className={`flex items-center mt-4 text-xs ${changeColor} ${changeBg} w-fit px-2 py-1 rounded-lg font-medium`}>
                        <ArrowUpRight className="w-3 h-3 mr-1" />
                        {change}
                    </div>
                )}
            </CardContent>
        </Card>
    );
}
