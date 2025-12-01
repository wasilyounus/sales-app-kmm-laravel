// Financial chart data organized by metric and period
const financialData = {
    revenue: {
        '7d': {
            value: '$8,450',
            change: '+3.2%',
            pathData: 'M0,180 L100,175 L200,165 L300,155 L400,145 L500,135 L600,120',
            areaData: 'M0,180 L100,175 L200,165 L300,155 L400,145 L500,135 L600,120 L600,200 L0,200 Z',
            points: [{ x: 200, y: 165 }, { x: 400, y: 145 }, { x: 600, y: 120 }],
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        '1m': {
            value: '$32,180',
            change: '+7.8%',
            pathData: 'M0,170 L100,165 L200,155 L300,145 L400,130 L500,115 L600,100',
            areaData: 'M0,170 L100,165 L200,155 L300,145 L400,130 L500,115 L600,100 L600,200 L0,200 Z',
            points: [{ x: 200, y: 155 }, { x: 400, y: 130 }, { x: 600, y: 100 }],
            labels: ['Wk 1', 'Wk 2', 'Wk 3', 'Wk 4']
        },
        '6m': {
            value: '$45,230',
            change: '+12.5%',
            pathData: 'M0,160 L50,145 L100,150 L150,120 L200,125 L250,95 L300,105 L350,75 L400,85 L450,60 L500,50 L550,45 L600,40',
            areaData: 'M0,160 L50,145 L100,150 L150,120 L200,125 L250,95 L300,105 L350,75 L400,85 L450,60 L500,50 L550,45 L600,40 L600,200 L0,200 Z',
            points: [{ x: 150, y: 120 }, { x: 300, y: 105 }, { x: 450, y: 60 }, { x: 600, y: 40 }],
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
        },
        '1y': {
            value: '$95,680',
            change: '+18.3%',
            pathData: 'M0,150 L60,145 L120,135 L180,125 L240,110 L300,95 L360,85 L420,70 L480,55 L540,40 L600,25',
            areaData: 'M0,150 L60,145 L120,135 L180,125 L240,110 L300,95 L360,85 L420,70 L480,55 L540,40 L600,25 L600,200 L0,200 Z',
            points: [{ x: 180, y: 125 }, { x: 360, y: 85 }, { x: 540, y: 40 }, { x: 600, y: 25 }],
            labels: ['Q1', 'Q2', 'Q3', 'Q4']
        }
    },
    profit: {
        '7d': {
            value: '$2,340',
            change: '+2.1%',
            pathData: 'M0,175 L100,170 L200,165 L300,160 L400,155 L500,150 L600,145',
            areaData: 'M0,175 L100,170 L200,165 L300,160 L400,155 L500,150 L600,145 L600,200 L0,200 Z',
            points: [{ x: 200, y: 165 }, { x: 400, y: 155 }, { x: 600, y: 145 }],
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        '1m': {
            value: '$9,850',
            change: '+5.4%',
            pathData: 'M0,165 L100,160 L200,155 L300,145 L400,140 L500,130 L600,120',
            areaData: 'M0,165 L100,160 L200,155 L300,145 L400,140 L500,130 L600,120 L600,200 L0,200 Z',
            points: [{ x: 200, y: 155 }, { x: 400, y: 140 }, { x: 600, y: 120 }],
            labels: ['Wk 1', 'Wk 2', 'Wk 3', 'Wk 4']
        },
        '6m': {
            value: '$12,450',
            change: '+8.2%',
            pathData: 'M0,140 L50,135 L100,145 L150,130 L200,140 L250,120 L300,125 L350,110 L400,115 L450,95 L500,100 L550,85 L600,80',
            areaData: 'M0,140 L50,135 L100,145 L150,130 L200,140 L250,120 L300,125 L350,110 L400,115 L450,95 L500,100 L550,85 L600,80 L600,200 L0,200 Z',
            points: [{ x: 150, y: 130 }, { x: 300, y: 125 }, { x: 450, y: 95 }, { x: 600, y: 80 }],
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
        },
        '1y': {
            value: '$28,920',
            change: '+14.7%',
            pathData: 'M0,145 L60,140 L120,135 L180,130 L240,120 L300,110 L360,100 L420,90 L480,75 L540,60 L600,50',
            areaData: 'M0,145 L60,140 L120,135 L180,130 L240,120 L300,110 L360,100 L420,90 L480,75 L540,60 L600,50 L600,200 L0,200 Z',
            points: [{ x: 180, y: 130 }, { x: 360, y: 100 }, { x: 540, y: 60 }, { x: 600, y: 50 }],
            labels: ['Q1', 'Q2', 'Q3', 'Q4']
        }
    },
    expenses: {
        '7d': {
            value: '$6,110',
            change: '+1.8%',
            pathData: 'M0,185 L100,183 L200,180 L300,178 L400,175 L500,172 L600,170',
            areaData: 'M0,185 L100,183 L200,180 L300,178 L400,175 L500,172 L600,170 L600,200 L0,200 Z',
            points: [{ x: 200, y: 180 }, { x: 400, y: 175 }, { x: 600, y: 170 }],
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        '1m': {
            value: '$22,330',
            change: '+3.2%',
            pathData: 'M0,180 L100,177 L200,173 L300,168 L400,163 L500,158 L600,153',
            areaData: 'M0,180 L100,177 L200,173 L300,168 L400,163 L500,158 L600,153 L600,200 L0,200 Z',
            points: [{ x: 200, y: 173 }, { x: 400, y: 163 }, { x: 600, y: 153 }],
            labels: ['Wk 1', 'Wk 2', 'Wk 3', 'Wk 4']
        },
        '6m': {
            value: '$32,780',
            change: '+4.1%',
            pathData: 'M0,170 L50,165 L100,160 L150,155 L200,150 L250,145 L300,140 L350,135 L400,130 L450,125 L500,120 L550,115 L600,110',
            areaData: 'M0,170 L50,165 L100,160 L150,155 L200,150 L250,145 L300,140 L350,135 L400,130 L450,125 L500,120 L550,115 L600,110 L600,200 L0,200 Z',
            points: [{ x: 150, y: 155 }, { x: 300, y: 140 }, { x: 450, y: 125 }, { x: 600, y: 110 }],
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
        },
        '1y': {
            value: '$66,760',
            change: '+6.8%',
            pathData: 'M0,165 L60,162 L120,158 L180,153 L240,147 L300,140 L360,133 L420,125 L480,115 L540,105 L600,95',
            areaData: 'M0,165 L60,162 L120,158 L180,153 L240,147 L300,140 L360,133 L420,125 L480,115 L540,105 L600,95 L600,200 L0,200 Z',
            points: [{ x: 180, y: 153 }, { x: 360, y: 133 }, { x: 540, y: 105 }, { x: 600, y: 95 }],
            labels: ['Q1', 'Q2', 'Q3', 'Q4']
        }
    }
};

/**
 * Get financial chart data for a specific metric and period
 * @param {string} metric - 'revenue', 'profit', or 'expenses'
 * @param {string} period - '7d', '1m', '6m', or '1y'
 * @returns {object} Chart data including value, change, pathData, areaData, points, and labels
 */
export function getFinancialChartData(metric, period) {
    return financialData[metric]?.[period] || financialData.revenue['6m'];
}
