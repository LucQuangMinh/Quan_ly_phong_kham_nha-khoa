import os

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\shifts.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix CSS
css_old = """    .shift-row { display: flex; gap: 4px; flex-wrap: wrap; justify-content: center; margin-top: auto; margin-bottom: auto; }
    
    .btn-shift {
      width: 28px; height: 28px; border-radius: 6px; border: 1px solid #cbd5e1;
      background: white; color: #64748b; font-weight: 700; font-size: 0.75rem;
      cursor: pointer; display: flex; align-items: center; justify-content: center;
      transition: all 0.15s ease;
    }
    
    .btn-shift:hover { background: #f1f5f9; border-color: #94a3b8; }
    
    .btn-shift.active.shift-s { background: #dbeafe; color: #1d4ed8; border-color: #93c5fd; }
    .btn-shift.active.shift-c { background: #fef9c3; color: #a16207; border-color: #fde047; }
    .btn-shift.active.shift-t { background: #f3e8ff; color: #7e22ce; border-color: #d8b4fe; }
    
    .btn-shift.holiday { opacity: 0.3; cursor: not-allowed; border-color: #fee2e2; background: #fef2f2; }"""

css_new = """    .shift-row { display: flex; flex-direction: column; gap: 4px; margin-top: 4px; }
    
    .btn-shift {
      width: 100%; padding: 6px; border-radius: 6px; border: 1px solid #e2e8f0;
      background: white; color: #64748b; font-weight: 700; font-size: 0.75rem;
      cursor: pointer; text-align: left;
      transition: all 0.15s ease; display: block;
    }
    
    .btn-shift:hover { border-color: #cbd5e1; background: #f1f5f9; }
    
    .btn-shift.active.shift-s { background: #dbeafe; color: #1d4ed8; border-color: #93c5fd; }
    .btn-shift.active.shift-c { background: #fef9c3; color: #a16207; border-color: #fde047; }
    .btn-shift.active.shift-t { background: #f3e8ff; color: #7e22ce; border-color: #d8b4fe; }
    
    .btn-shift.holiday { opacity: 0.3; cursor: not-allowed; border-color: #fee2e2; background: #fef2f2; }"""

content = content.replace(css_old, css_new)

# Fix loadData
js_old_loaddata = """    async function loadData() {
        const yyyy = currentDate.getFullYear();
        const mm = currentDate.getMonth();
        const firstDay = new Date(yyyy, mm, 1);
        const lastDay = new Date(yyyy, mm + 1, 0);
        
        // Fetch Holidays
        const holRes = await fetch(`/api/holidays?start=${formatDate(firstDay)}&end=${formatDate(lastDay)}`);
        const holData = await holRes.json();
        holidaysObj = {};
        holData.forEach(h => {
            holidaysObj[h.holidayDate] = true;
        });

        // Fetch Shifts
        const shiftRes = await fetch(`/api/shifts?start=${formatDate(firstDay)}&end=${formatDate(lastDay)}`);
        allShifts = await shiftRes.json();
        
        renderCalendar();
    }"""

js_new_loaddata = """    async function loadData() {
        const yyyy = currentDate.getFullYear();
        const mm = currentDate.getMonth();
        const firstDay = new Date(yyyy, mm, 1);
        const lastDay = new Date(yyyy, mm + 1, 0);
        
        // Calculate exactly what dates are in the grid
        let firstDayIndex = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;
        const gridStart = new Date(yyyy, mm, 1 - firstDayIndex);
        
        let totalDays = firstDayIndex + lastDay.getDate();
        let rem = totalDays % 7;
        let gridEnd = new Date(yyyy, mm + 1, rem === 0 ? 0 : 7 - rem);

        // Fetch Holidays for the entire grid
        const holRes = await fetch(`/api/holidays?start=${formatDate(gridStart)}&end=${formatDate(gridEnd)}`);
        const holData = await holRes.json();
        holidaysObj = {};
        holData.forEach(h => {
            holidaysObj[h.holidayDate] = true;
        });

        // Fetch Shifts for the entire grid
        const shiftRes = await fetch(`/api/shifts?start=${formatDate(gridStart)}&end=${formatDate(gridEnd)}`);
        allShifts = await shiftRes.json();
        
        renderCalendar();
    }"""

content = content.replace(js_old_loaddata, js_new_loaddata)

# Fix render buttons text S C T -> Sáng Chiều Tối
old_render = """<button class="btn-shift shift-s ${sActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Sáng')" title="Ca Sáng">S</button>
                        <button class="btn-shift shift-c ${cActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Chiều')" title="Ca Chiều">C</button>
                        <button class="btn-shift shift-t ${tActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Tối')" title="Ca Tối">T</button>"""

new_render = """<button class="btn-shift shift-s ${sActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Sáng')" title="Ca Sáng">Sáng</button>
                        <button class="btn-shift shift-c ${cActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Chiều')" title="Ca Chiều">Chiều</button>
                        <button class="btn-shift shift-t ${tActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Tối')" title="Ca Tối">Tối</button>"""

content = content.replace(old_render, new_render)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Updated shifts.html successfully!")
