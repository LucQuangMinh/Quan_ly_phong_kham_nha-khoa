import os
import re

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add global allShifts variable
content = content.replace("let allSchedules = [];", "let allSchedules = [];\n    let activeShifts = [];")

# Fetch shifts in loadData
old_load = """const holRes = await fetch(`/api/holidays?start=${formatDate(firstDay)}&end=${formatDate(lastDay)}`);
        const holData = await holRes.json();
        holidaysObj = {};
        holData.forEach(h => {
            holidaysObj[h.holidayDate] = true;
        });"""

new_load = """const holRes = await fetch(`/api/holidays?start=${formatDate(firstDay)}&end=${formatDate(lastDay)}`);
        const holData = await holRes.json();
        holidaysObj = {};
        holData.forEach(h => {
            holidaysObj[h.holidayDate] = true;
        });
        
        const shiftRes = await fetch(`/api/shifts?start=${formatDate(firstDay)}&end=${formatDate(lastDay)}`);
        activeShifts = await shiftRes.json();"""

content = content.replace(old_load, new_load)

# Add helper function
helper_func = """
    function hasActiveShift(dateStr, type) {
        return activeShifts.some(s => s.shiftDate === dateStr && s.shiftType === type);
    }
"""
content = content.replace("function renderCalendar() {", helper_func + "\n    function renderCalendar() {")

# Update render logic to only render shift blocks if they are active
old_render = """                cell.innerHTML = `
                    <div class="date-num ${isHol ? 'holiday' : ''}">${d.getDate()}</div>
                    <div class="shift-block" onclick="toggleShiftDoctor('${dateStr}', 'Sáng')">
                        Sáng
                        <div class="doc-tags" id="tags-${dateStr}-Sáng"></div>
                    </div>
                    <div class="shift-block" onclick="toggleShiftDoctor('${dateStr}', 'Chiều')">
                        Chiều
                        <div class="doc-tags" id="tags-${dateStr}-Chiều"></div>
                    </div>
                    <div class="shift-block" onclick="toggleShiftDoctor('${dateStr}', 'Tối')">
                        Tối
                        <div class="doc-tags" id="tags-${dateStr}-Tối"></div>
                    </div>
                `;"""

new_render = """                let html = `<div class="date-num ${isHol ? 'holiday' : ''}">${d.getDate()}</div>`;
                
                ['Sáng', 'Chiều', 'Tối'].forEach(type => {
                    if (hasActiveShift(dateStr, type)) {
                        html += `
                        <div class="shift-block" onclick="toggleShiftDoctor('${dateStr}', '${type}')">
                            ${type}
                            <div class="doc-tags" id="tags-${dateStr}-${type}"></div>
                        </div>`;
                    }
                });
                
                cell.innerHTML = html;"""

content = content.replace(old_render, new_render)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated schedules.html with Shift validation rendering.")
