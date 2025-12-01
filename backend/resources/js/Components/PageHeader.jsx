import { Button } from "@/Components/ui/button";

export default function PageHeader({ 
    icon: Icon, 
    title, 
    subtitle, 
    actionLabel,
    actionIcon: ActionIcon,
    onAction 
}) {
    return (
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-6 mb-8 bg-white p-6 rounded-xl border border-gray-100">
            <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-lime-100 flex items-center justify-center">
                    <Icon className="w-6 h-6 text-lime-600" />
                </div>
                <div>
                    <h1 className="text-xl font-bold text-gray-900">{title}</h1>
                    <p className="text-sm text-gray-500 mt-0.5">{subtitle}</p>
                </div>
            </div>
            {actionLabel && (
                <Button 
                    onClick={onAction}
                    className="bg-black hover:bg-gray-800 text-white transition-all duration-200"
                >
                    {ActionIcon && <ActionIcon className="w-4 h-4 mr-2" />}
                    {actionLabel}
                </Button>
            )}
        </div>
    );
}
