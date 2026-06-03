import os

file_path = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\holidays.html"

html_content = """<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Thiết lập các ngày nghỉ</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="index.css" />
  <style>
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
  </style>
</head>
<body>
  <div id="sidebar-container"></div>
  <main>
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Thiết lập các ngày nghỉ</strong></div>
      <div class="page-head">
        <h1>Quản lý Lịch nghỉ (UC2.1)</h1>
      </div>
      
      <div class="card">
        <div class="tabs">
          <button class="tab-btn active" onclick="switchTab('calendar')">Lịch tháng</button>
          <button class="tab-btn" onclick="switchTab('list')">Danh sách</button>
        </div>
        
        <div id="calendarView" class="view-section active">
          <div class="page-head" style="margin-bottom: 10px;">
            <h2 style="font-size: 1.125rem; margin: 0;">Lịch nghỉ trực quan</h2>
            <div class="calendar-nav">
              <button class="btn btn-ghost" onclick="prevMonth()">← Tháng trước</button>
              <span id="monthTitle" style="font-weight: 700; font-size: 1rem;">Tháng...</span>
              <button class="btn btn-ghost" onclick="nextMonth()">Tháng sau →</button>
            </div>
          </div>
          <div style="background: #fff1f2; color: #991b1b; padding: 12px; border: 1px solid #fecaca; border-radius: 8px; font-size: 0.875rem; margin-bottom: 15px;">
            <strong>Lưu ý:</strong> Click vào ngày để bật/tắt trạng thái nghỉ. Hệ thống sẽ tự động XÓA các ca làm việc của ngày đó để tránh xung đột lịch. Không thể xóa kỳ nghỉ của ngày hiện tại.
          </div>
          
          <div class="calendar-container">
            <div class="calendar-grid">
              <div class="cal-header">T2</div>
              <div class="cal-header">T3</div>
              <div class="cal-header">T4</div>
              <div class="cal-header">T5</div>
              <div class="cal-header">T6</div>
              <div class="cal-header">T7</div>
              <div class="cal-header">CN</div>
              <div class="cal-header week-action">Tác vụ Tuần</div>
            </div>
            <div class="cal-body" id="calendarBody">
              <!-- Rendered by JS -->
            </div>
          </div>
        </div>
        
        <div id="listView" class="view-section">
          <div class="page-head" style="margin-bottom: 15px;">
            <h2 style="font-size: 1.125rem; margin: 0;">Danh sách ngày nghỉ</h2>
            <button class="btn btn-primary" onclick="openModal('holidayModal')">+ Thêm mới</button>
          </div>
          <div class="table-wrap">
            <table style="width: 100%; border-collapse: collapse;">
              <thead style="background: #f1f5f9; text-align: left;">
                <tr>
                  <th style="padding: 12px;">Ngày nghỉ</th>
                  <th style="padding: 12px;">Trạng thái</th>
                  <th style="padding: 12px;">Thao tác</th>
                </tr>
              </thead>
              <tbody id="holidayListBody"></tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Modal Thêm/Sửa Ngày nghỉ -->
    <div class="modal-backdrop" id="holidayModal" style="position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: none; align-items: center; justify-content: center; z-index: 50;">
      <div class="modal" style="background: #fff; border-radius: 12px; padding: 24px; width: 400px;">
        <h2 id="modalTitle" style="margin: 0 0 16px;">Thêm ngày nghỉ</h2>
        <input type="hidden" id="holidayId" />
        <div style="margin-bottom: 16px;">
          <label style="display: block; margin-bottom: 6px; font-weight: 600;">Ngày nghỉ (*)</label>
          <input type="date" id="holidayDate" style="width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px;" />
        </div>
        <div style="margin-bottom: 16px;">
          <label style="display: block; margin-bottom: 6px; font-weight: 600;">Trạng thái</label>
          <select id="holidayStatus" style="width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px;">
            <option value="Hiệu lực">Hiệu lực</option>
            <option value="Ngưng áp dụng">Ngưng áp dụng</option>
          </select>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px;">
          <button class="btn btn-ghost" onclick="closeModal('holidayModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveHoliday()">Lưu</button>
        </div>
      </div>
    </div>
  </main>
  
  <script src="sidebar.js"></script>
  <script>
    let currentDate = new Date();
    let currentMonth = currentDate.getMonth();
    let currentYear = currentDate.getFullYear();
    let allHolidays = [];

    async function loadData() {
        const res = await fetch('/api/holidays');
        allHolidays = await res.json();
        renderCalendar();
        renderList();
    }

    function switchTab(tabId) {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.view-section').forEach(s => s.classList.remove('active'));
        
        if (tabId === 'calendar') {
            document.querySelectorAll('.tab-btn')[0].classList.add('active');
            document.getElementById('calendarView').classList.add('active');
        } else {
            document.querySelectorAll('.tab-btn')[1].classList.add('active');
            document.getElementById('listView').classList.add('active');
        }
    }

    function prevMonth() {
        currentMonth--;
        if(currentMonth < 0) { currentMonth = 11; currentYear--; }
        renderCalendar();
    }
    function nextMonth() {
        currentMonth++;
        if(currentMonth > 11) { currentMonth = 0; currentYear++; }
        renderCalendar();
    }

    function getLocalIsoDate(dateObj) {
        const d = new Date(dateObj);
        d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
        return d.toISOString().split('T')[0];
    }

    function isHoliday(dateStr) {
        return allHolidays.some(h => h.holidayDate === dateStr);
    }

    function renderCalendar() {
        document.getElementById('monthTitle').innerText = `Tháng ${currentMonth + 1} / ${currentYear}`;
        
        const firstDay = new Date(currentYear, currentMonth, 1);
        const lastDay = new Date(currentYear, currentMonth + 1, 0);
        
        let startDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; 
        
        const calBody = document.getElementById('calendarBody');
        calBody.innerHTML = '';
        
        let dayCounter = 1;
        let isStarted = false;
        let todayStr = getLocalIsoDate(new Date());

        for (let row = 0; row < 6; row++) {
            let rowHtml = '';
            let weekDays = [];
            
            for (let col = 0; col < 7; col++) {
                if (row === 0 && col === startDayOfWeek) { isStarted = true; }
                
                if (isStarted && dayCounter <= lastDay.getDate()) {
                    let d = new Date(currentYear, currentMonth, dayCounter);
                    let isoStr = getLocalIsoDate(d);
                    weekDays.push(isoStr);
                    
                    let holidayData = allHolidays.find(h => h.holidayDate === isoStr);
                    let isHol = !!holidayData;
                    let isToday = (isoStr === todayStr);
                    
                    let cls = 'cal-cell ' + (isHol ? 'is-holiday' : '');
                    let statusHtml = isHol 
                        ? `<span class="cal-status holiday">Ngày nghỉ</span>` 
                        : `<span class="cal-status working">Ngày làm</span>`;
                    
                    let todayBadge = isToday ? '<span style="color:#2563eb; font-size:0.7rem; float:right;">(Hôm nay)</span>' : '';
                        
                    rowHtml += `
                        <div class="${cls}" onclick="toggleHoliday('${isoStr}')">
                            <div class="cal-date">${dayCounter} ${todayBadge}</div>
                            ${statusHtml}
                        </div>
                    `;
                    dayCounter++;
                } else {
                    rowHtml += `<div class="cal-cell" style="background: #f8fafc; cursor: default;"></div>`;
                }
            }
            
            if (weekDays.length > 0) {
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
            } else if(dayCounter > lastDay.getDate()) {
                break;
            }
        }
    }

    async function toggleHoliday(dateStr) {
        const res = await fetch('/api/holidays/toggle', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ date: dateStr })
        });
        if(res.ok) {
            loadData();
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function toggleWeek(startDateStr) {
        const res = await fetch('/api/holidays/toggle-week', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ startDate: startDateStr })
        });
        if(res.ok) {
            loadData();
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    // List View CRUD
    function renderList() {
        const tbody = document.getElementById('holidayListBody');
        tbody.innerHTML = '';
        allHolidays.forEach(h => {
            let parts = h.holidayDate.split('-');
            let fDate = parts[2] + '/' + parts[1] + '/' + parts[0];
            
            tbody.innerHTML += `
                <tr>
                    <td style="padding: 12px; border-bottom: 1px solid #e2e8f0;"><b>${fDate}</b></td>
                    <td style="padding: 12px; border-bottom: 1px solid #e2e8f0;">
                        <span style="background: ${h.status==='Hiệu lực'?'#dcfce7':'#f1f5f9'}; color: ${h.status==='Hiệu lực'?'#166534':'#475569'}; padding: 4px 8px; border-radius: 6px; font-size: 0.8rem; font-weight: 600;">
                            ${h.status}
                        </span>
                    </td>
                    <td style="padding: 12px; border-bottom: 1px solid #e2e8f0;">
                        <button onclick="editHoliday(${h.id})" style="background: none; border: none; color: #2563eb; cursor: pointer; font-weight: 600;">Sửa</button> |
                        <button onclick="deleteHoliday(${h.id})" style="background: none; border: none; color: #dc2626; cursor: pointer; font-weight: 600;">Xóa</button>
                    </td>
                </tr>
            `;
        });
    }

    function openModal(id) { 
        document.getElementById(id).style.display = 'flex'; 
    }
    
    function closeModal(id) { 
        document.getElementById(id).style.display = 'none'; 
        if(id === 'holidayModal') {
            document.getElementById('holidayId').value = '';
            document.getElementById('holidayDate').value = '';
            document.getElementById('holidayStatus').value = 'Hiệu lực';
            document.getElementById('modalTitle').innerText = 'Thêm ngày nghỉ';
        }
    }

    function editHoliday(id) {
        let h = allHolidays.find(x => x.id === id);
        if(h) {
            document.getElementById('holidayId').value = h.id;
            document.getElementById('holidayDate').value = h.holidayDate;
            document.getElementById('holidayStatus').value = h.status;
            document.getElementById('modalTitle').innerText = 'Sửa ngày nghỉ';
            openModal('holidayModal');
        }
    }

    async function saveHoliday() {
        const id = document.getElementById('holidayId').value;
        const data = {
            holidayDate: document.getElementById('holidayDate').value,
            status: document.getElementById('holidayStatus').value
        };
        let url = '/api/holidays';
        let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        
        const res = await fetch(url, {
            method: method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        if (res.ok) {
            alert(id ? "Cập nhật ngày nghỉ thành công!" : "Thêm ngày nghỉ thành công!");
            closeModal('holidayModal');
            loadData();
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function deleteHoliday(id) {
        if(confirm("Bạn có chắc chắn muốn xóa ngày nghỉ này?")) {
            const res = await fetch('/api/holidays/' + id, { method: 'DELETE' });
            if (res.ok) {
                alert("Xóa ngày nghỉ thành công!");
                loadData();
            } else {
                const err = await res.json();
                alert("Lỗi: " + err.message);
            }
        }
    }

    window.onload = function() {
        fetch('sidebar.html').then(r => r.text()).then(html => {
            document.getElementById('sidebar-container').innerHTML = html;
        });
        loadData();
    }
  </script>
</body>
</html>
"""

with open(file_path, "w", encoding="utf-8") as f:
    f.write(html_content)
print("Successfully generated holidays.html")
