import { useState } from 'react';
import { getFinancialChartData } from '@/Data/financialChartData';

export default function FinancialChart() {
    const [selectedMetric, setSelectedMetric] = useState('revenue');
    const [selectedPeriod, setSelectedPeriod] = useState('6m');

    const metricData = getFinancialChartData(selectedMetric, selectedPeriod);

    const metricButtons = [
        { id: 'revenue', label: 'Revenue' },
        { id: 'profit', label: 'Profit' },
        { id: 'expenses', label: 'Expenses' }
    ];

    const periodButtons = [
        { id: '7d', label: '7D' },
        { id: '1m', label: '1M' },
        { id: '6m', label: '6M' },
        { id: '1y', label: '1Y' }
    ];

    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            {/* Header with toggles */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-start gap-4 mb-6">
                <div>
                    <h3 className="font-bold text-lg text-gray-900">Financial Overview</h3>
                    <div className="flex items-baseline gap-3 mt-2">
                        <span className="text-3xl font-bold text-gray-900">{metricData.value}</span>
                        <span className="text-sm font-medium text-lime-600 bg-lime-50 px-2 py-1 rounded-full">
                            {metricData.change}
                        </span>
                    </div>
                    <p className="text-xs text-gray-500 mt-1">vs last period</p>
                </div>
                
                {/* Metric toggles */}
                <div className="flex bg-gray-50 p-1 rounded-lg w-full md:w-auto overflow-x-auto">
                    {metricButtons.map((button) => (
                        <button
                            key={button.id}
                            onClick={() => setSelectedMetric(button.id)}
                            className={`flex-1 md:flex-none px-3 py-1.5 text-xs font-medium rounded-md transition-all whitespace-nowrap ${
                                selectedMetric === button.id
                                    ? 'bg-white shadow-sm text-gray-900'
                                    : 'text-gray-500 hover:text-gray-900'
                            }`}
                        >
                            {button.label}
                        </button>
                    ))}
                </div>
            </div>

            {/* Chart Container */}
            <div className="h-72 relative">
                {/* Grid lines and labels */}
                <div className="absolute inset-0 flex flex-col justify-between py-2">
                    {[60, 45, 30, 15, 0].map((value) => (
                        <div key={value} className="flex items-center">
                            <span className="text-xs text-gray-400 w-8">${value}k</span>
                            <div className="flex-1 border-t border-gray-100 ml-2"></div>
                        </div>
                    ))}
                </div>

                {/* Chart SVG */}
                <svg viewBox="0 0 600 200" preserveAspectRatio="none" className="w-full h-full absolute inset-0">
                    <defs>
                        <linearGradient id="limeGradientEnhanced" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stopColor="#84cc16" stopOpacity="0.3" />
                            <stop offset="100%" stopColor="#84cc16" stopOpacity="0.05" />
                        </linearGradient>
                    </defs>

                    {/* Area fill */}
                    <path
                        d={metricData.areaData}
                        fill="url(#limeGradientEnhanced)"
                        className="transition-all duration-500 ease-in-out"
                    />

                    {/* Line stroke */}
                    <path
                        d={metricData.pathData}
                        fill="none"
                        stroke="#84cc16"
                        strokeWidth="3"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        className="transition-all duration-500 ease-in-out"
                    />

                    {/* Data points */}
                    {metricData.points.map((point, index) => (
                        <circle
                            key={index}
                            cx={point.x}
                            cy={point.y}
                            r="5"
                            fill="white"
                            stroke="#84cc16"
                            strokeWidth="2"
                            className="transition-all duration-500 ease-in-out"
                        />
                    ))}
                </svg>

                {/* Period labels */}
                <div className="absolute bottom-0 left-0 right-0 flex justify-between px-10 text-xs text-gray-400">
                    {metricData.labels.map((label, index) => (
                        <span key={index}>{label}</span>
                    ))}
                </div>
            </div>

            {/* Period selector */}
            <div className="flex gap-2 mt-6 justify-end">
                {periodButtons.map((button) => (
                    <button
                        key={button.id}
                        onClick={() => setSelectedPeriod(button.id)}
                        className={`px-3 py-1 text-xs font-medium rounded-md transition-colors ${
                            selectedPeriod === button.id
                                ? 'bg-lime-50 text-lime-600'
                                : 'text-gray-500 hover:text-gray-900 hover:bg-gray-50'
                        }`}
                    >
                        {button.label}
                    </button>
                ))}
            </div>
        </div>
    );
}
