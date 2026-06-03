import os

target = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\holidays.html"
source = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\doctors.html"

with open(source, 'r', encoding='utf-8') as f:
    doc_html = f.read()

start_style = doc_html.find('<style>') + len('<style>')
end_style = doc_html.find('</style>')
core_css = doc_html[start_style:end_style]

holidays_css = """
    .tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px; }
    .tab-btn { background: none; border: none; font-size: 1rem; font-weight: 600; color: #64748b; cursor: pointer; padding: 5px 10px; border-radius: 6px; }
    .tab-btn.active { color: #2563eb; background: #eff6ff; }
    .view-section { display: none; }
    .view-section.active { display: block; }
    
    .calendar-container {
      background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; margin-top: 10px;
    }
    .calendar-grid {
      display: grid; grid-template-columns: repeat(8, 1fr); background: #f8fafc; border-bottom: 1px solid #e2e8f0;
    }
    .cal-header {
      padding: 12px; font-weight: 600; font-size: 0.875rem; text-align: center; color: #475569; border-right: 1px solid #e2e8f0;
    }
    .cal-header.week-action { background: #f1f5f9; color: #0f172a; border-right: none; }
    
    .cal-body {
      display: grid; grid-template-columns: repeat(8, 1fr);
    }
    .cal-cell {
      min-height: 100px; padding: 10px; border-right: 1px solid #e2e8f0; border-bottom: 1px solid #e2e8f0;
      background: #fff; position: relative; cursor: pointer; transition: background 0.2s;
    }
    .cal-cell:hover { background: #f8fafc; }
    .cal-cell.is-holiday { background: #fef2f2; }
    .cal-cell.is-holiday:hover { background: #fee2e2; }
    .cal-date { font-weight: 600; font-size: 0.875rem; color: #0f172a; margin-bottom: 4px; }
    .cal-status { font-size: 0.75rem; font-weight: 500; padding: 2px 6px; border-radius: 4px; display: inline-block; }
    .cal-status.working { background: #e2e8f0; color: #475569; }
    .cal-status.holiday { background: #dc2626; color: #fff; }
    
    .cal-week-action {
      border-right: none; background: #f8fafc; display: flex; align-items: center; justify-content: center;
    }
    .cal-week-action button {
      background: #fff; border: 1px solid #cbd5e1; padding: 6px 12px; border-radius: 6px; font-size: 0.8125rem;
      font-weight: 600; cursor: pointer; color: #0f172a; transition: all 0.2s;
    }
    .cal-week-action button:hover { background: #f1f5f9; border-color: #94a3b8; }
    .cal-week-action button.is-holiday-week { background: #dc2626; color: #fff; border-color: #dc2626; }
    .cal-week-action button.is-holiday-week:hover { background: #b91c1c; }
    
    .page-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .calendar-nav { display: flex; align-items: center; gap: 15px; }
    .calendar-nav button { padding: 6px 12px; font-size: 0.875rem; }
"""

with open(target, 'r', encoding='utf-8') as f:
    holidays_html = f.read()

# Find exact start and end of <style>...</style> in holidays_html
s_style = holidays_html.find('<style>') + len('<style>')
e_style = holidays_html.find('</style>')

full_css = "\n" + core_css + "\n" + holidays_css + "\n"

new_holidays_html = holidays_html[:s_style] + full_css + holidays_html[e_style:]

with open(target, 'w', encoding='utf-8') as f:
    f.write(new_holidays_html)

print("Successfully fixed CSS in holidays.html")
