import { Card, CardContent, CardHeader, CardTitle } from "@/Components/ui/card";
import { Button } from "@/Components/ui/button";
import { Edit } from 'lucide-react';

export default function EntityCard({ 
    children,
    title,
    icon: Icon,
    subtitle,
    onEdit,
    className = '' 
}) {
    return (
        <Card className={`group hover:shadow-xl hover:shadow-lime-500/5 transition-all duration-300 hover:-translate-y-1 border-gray-100 overflow-hidden bg-white ${className}`}>
            <CardHeader className="flex flex-row items-start justify-between space-y-0 pb-3 bg-gradient-to-br from-transparent to-lime-500/5">
                <div className="space-y-1 flex-1">
                    <CardTitle className="text-lg font-semibold text-gray-900 group-hover:text-lime-600 transition-colors flex items-center gap-2">
                        {Icon && <Icon className="h-4 w-4 text-gray-400" />}
                        {title}
                    </CardTitle>
                    {subtitle}
                </div>
                {onEdit && (
                    <Button
                        size="sm"
                        variant="ghost"
                        onClick={onEdit}
                        className="h-8 w-8 p-0 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-lime-50 hover:text-lime-600"
                    >
                        <Edit className="h-4 w-4" />
                    </Button>
                )}
            </CardHeader>
            <CardContent className="pt-4">
                {children}
            </CardContent>
        </Card>
    );
}
