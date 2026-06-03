import re

target = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\holidays.html"

with open(target, 'r', encoding='utf-8') as f:
    content = f.read()

new_render_calendar = """    function renderCalendar() {
        document.getElementById('monthTitle').innerText = `Tháng ${currentMonth + 1} / ${currentYear}`;
        
        const firstDay = new Date(currentYear, currentMonth, 1);
        let startDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; 
        
        let gridStartDate = new Date(currentYear, currentMonth, 1 - startDayOfWeek);
        
        const calBody = document.getElementById('calendarBody');
        calBody.innerHTML = '';
        
        let todayStr = getLocalIsoDate(new Date());

        for (let row = 0; row < 6; row++) {
            let rowHtml = '';
            let weekDays = [];
            
            for (let col = 0; col < 7; col++) {
                let d = new Date(gridStartDate.getFullYear(), gridStartDate.getMonth(), gridStartDate.getDate() + (row * 7) + col);
                let isoStr = getLocalIsoDate(d);
                weekDays.push(isoStr);
                
                let isCurrentMonth = d.getMonth() === currentMonth;
                let holidayData = allHolidays.find(h => h.holidayDate === isoStr);
                let isHol = !!holidayData;
                let isToday = (isoStr === todayStr);
                
                let cls = 'cal-cell ' + (isHol ? 'is-holiday' : '');
                
                let statusHtml = isHol 
                    ? `<span class="cal-status holiday">Ngày nghỉ</span>` 
                    : `<span class="cal-status working">Ngày làm</span>`;
                
                let todayBadge = isToday ? '<span style="color:#2563eb; font-size:0.7rem; float:right;">(Hôm nay)</span>' : '';
                    
                rowHtml += `
                    <div class="${cls}" onclick="toggleHoliday('${isoStr}')" style="${!isCurrentMonth ? 'opacity: 0.5;' : ''}">
                        <div class="cal-date">${d.getDate()} ${todayBadge}</div>
                        ${statusHtml}
                    </div>
                `;
            }
            
            let weekStartDate = weekDays[0];
            let allHols = weekDays.every(d => isHoliday(d));
            
            let btnCls = allHols ? 'is-holiday-week' : '';
            let btnText = allHols ? '- Bỏ nghỉ' : '+ Nghỉ';
            
            rowHtml += `
                <div class="cal-cell cal-week-action">
                    <button class="${btnCls}" onclick="toggleWeek('${weekStartDate}')">${btnText}</button>
                </div>
            `;
            calBody.innerHTML += rowHtml;
        }
    }"""

# Using regex to replace the function definition block
pattern = re.compile(r'    function renderCalendar\(\) \{.*?(?=    async function toggleHoliday)', re.DOTALL)
new_content = pattern.sub(new_render_calendar + '\n\n', content)

with open(target, 'w', encoding='utf-8') as f:
    f.write(new_content)

print("Successfully replaced renderCalendar function.")
