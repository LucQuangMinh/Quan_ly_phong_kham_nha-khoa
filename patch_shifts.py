import os

html_content = """<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Thiết lập ca làm việc</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <style>
    :root {
      --sidebar-bg: #f8fafc;
      --sidebar-border: #e2e8f0;
      --text-primary: #1e293b;
      --text-muted: #64748b;
      --active-bg: #e8f1fe;
      --active-text: #2563eb;
      --hover-bg: #f1f5f9;
      --radius-item: 10px;
      --primary: #2563eb;
      --primary-hover: #1d4ed8;
      --surface: #ffffff;
      --danger: #dc2626;
    }

    * { box-sizing: border-box; }

    body {
      margin: 0; min-height: 100vh; font-family: "Inter", system-ui, sans-serif;
      background: #fff; color: var(--text-primary); display: flex;
    }

    .sidebar { width: 288px; min-height: 100vh; background: var(--sidebar-bg); border-right: 1px solid var(--sidebar-border); padding: 20px 12px 24px; flex-shrink: 0; }
    .sidebar-header { padding: 4px 10px 16px; border-bottom: 1px solid var(--sidebar-border); margin-bottom: 12px; }
    .sidebar-header h1 { margin: 0; font-size: 1rem; font-weight: 700; letter-spacing: -0.02em; }
    .sidebar-header p { margin: 4px 0 0; font-size: 0.8125rem; color: var(--text-muted); }
    .nav { display: flex; flex-direction: column; gap: 2px; }
    .nav-item { display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: var(--radius-item); font-size: 0.875rem; font-weight: 500; color: var(--text-primary); text-decoration: none; background: transparent; width: 100%; text-align: left; cursor: pointer; transition: all 0.15s ease; }
    .nav-item:hover { background: var(--hover-bg); }
    .nav-item.active { background: var(--active-bg); color: var(--active-text); }
    
    main { flex: 1; min-width: 0; padding: 24px 32px 40px; background: #fafbfc; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
    
    .content-shell { max-width: 960px; margin: 0 auto; width: 100%; height: 100%; display: flex; flex-direction: column; }
    
    .page-head { display: flex; flex-wrap: wrap; align-items: flex-start; justify-content: space-between; gap: 14px; margin-bottom: 18px; flex-shrink: 0; }
    .page-head h1 { margin: 0; font-size: 1.25rem; font-weight: 600; }
    .crumb { font-size: 0.8125rem; color: var(--text-muted); margin: 0 0 16px; flex-shrink: 0; }
    
    .schedule-layout { display: flex; gap: 20px; flex: 1; min-height: 0; }
    
    .calendar-pane { flex: 1; display: flex; flex-direction: column; background: var(--surface); border: 1px solid var(--sidebar-border); border-radius: 12px; padding: 20px; min-width: 0; min-height: 0; }
    
    .calendar-nav { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; flex-shrink: 0; }
    .btn-ghost { background: transparent; border: 1px solid var(--sidebar-border); padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 0.875rem; }
    .btn-ghost:hover { background: var(--hover-bg); }
    
    .detail-hint { margin-bottom: 16px; font-size: 0.8125rem; line-height: 1.5; padding: 10px 14px; background: #f0f7ff; border: 1px solid #dbeafe; border-radius: 8px; color: #1e40af; flex-shrink: 0; }
    
    .calendar-grid-wrap { border: 1px solid var(--sidebar-border); border-radius: 8px; overflow-y: auto; flex: 1; min-height: 0; }
    .cal-header-row { display: grid; grid-template-columns: repeat(8, 1fr); background: #f1f5f9; border-bottom: 1px solid var(--sidebar-border); position: sticky; top: 0; z-index: 10; }
    .cal-header-cell { padding: 10px; text-align: center; font-weight: 600; font-size: 0.875rem; color: #475569; border-right: 1px solid var(--sidebar-border); }
    
    .cal-row { display: grid; grid-template-columns: repeat(8, 1fr); border-bottom: 1px solid var(--sidebar-border); min-height: 100px; }
    .cal-cell { padding: 8px; border-right: 1px solid var(--sidebar-border); display: flex; flex-direction: column; gap: 4px; }
    .cal-cell:last-child { border-right: none; }
    .cal-cell.other-month { background: #f8fafc; opacity: 0.7; }
    
    .date-num { font-size: 0.875rem; font-weight: 600; color: #64748b; margin-bottom: 4px; text-align: right; }
    
    .shift-row { display: flex; gap: 4px; flex-wrap: wrap; justify-content: center; margin-top: auto; margin-bottom: auto; }
    
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
    
    .btn-shift.holiday { opacity: 0.3; cursor: not-allowed; border-color: #fee2e2; background: #fef2f2; }
    .date-num.holiday { color: var(--danger); }
    
    .btn-week-shift {
      width: 100%; padding: 4px 0; font-size: 0.75rem; font-weight: 600; border-radius: 4px;
      border: 1px solid #cbd5e1; background: white; color: #475569; cursor: pointer;
    }
    .btn-week-shift:hover { background: #f1f5f9; }
  </style>
</head>
<body>

  <aside class="sidebar">
    <div class="sidebar-header">
      <h1>Nha Khoa Care</h1>
      <p>Hệ thống Quản lý Phòng khám</p>
    </div>
    <nav class="nav">
      <a href="dashboard.html" class="nav-item">Bảng điều khiển</a>
      <a href="users.html" class="nav-item admin-only">Quản lý Người dùng</a>
      <a href="doctors.html" class="nav-item admin-only">Hồ sơ Bác sĩ</a>
      <a href="services.html" class="nav-item admin-only">Danh mục Dịch vụ</a>
      <a href="holidays.html" class="nav-item admin-only">Thiết lập ngày nghỉ</a>
      <a href="shifts.html" class="nav-item active admin-only">Thiết lập ca làm việc</a>
      <a href="schedules.html" class="nav-item admin-only">Đăng ký lịch trực</a>
    </nav>
  </aside>

  <main>
    <div class="content-shell">
        <div class="crumb">Hệ thống / <strong>Thiết lập ca làm việc</strong></div>
        <div class="page-head">
          <h1 id="pageTitle">Thiết lập ca làm việc (UC2.2)</h1>
        </div>
        
        <div class="schedule-layout">
          <div class="calendar-pane">
            <div class="detail-hint">
              <strong>Mẫu ca mặc định:</strong> <strong>Sáng (S)</strong> (07:00-13:00), <strong>Chiều (C)</strong> (13:00-19:00), <strong>Tối (T)</strong> (19:00-07:00).<br>
              Nhấn vào từng ô ca <strong>S</strong>, <strong>C</strong>, <strong>T</strong> để bật/tắt ca cho ngày đó. Cột <strong>Cả tuần</strong> bên phải để áp dụng nhanh.
            </div>
          
            <div class="calendar-nav">
              <button class="btn-ghost" onclick="prevMonth()">← Tháng trước</button>
              <span id="monthTitle" style="font-weight: 700; font-size: 1.125rem;">Tháng...</span>
              <button class="btn-ghost" onclick="nextMonth()">Tháng sau →</button>
            </div>
            
            <div class="calendar-grid-wrap">
              <div class="cal-header-row">
                <div class="cal-header-cell">T2</div>
                <div class="cal-header-cell">T3</div>
                <div class="cal-header-cell">T4</div>
                <div class="cal-header-cell">T5</div>
                <div class="cal-header-cell">T6</div>
                <div class="cal-header-cell">T7</div>
                <div class="cal-header-cell">CN</div>
                <div class="cal-header-cell">Cả tuần</div>
              </div>
              <div class="cal-body" id="calendarBody">
                <!-- Rendered by JS -->
              </div>
            </div>
          </div>
        </div>
    </div>
  </main>

  <script>
    // Access Control
    const userStr = localStorage.getItem('user');
    if (!userStr) window.location.href = 'login.html';
    const user = JSON.parse(userStr);
    if (user.role !== 'Admin') {
        alert("Chỉ Admin mới có quyền truy cập chức năng này!");
        window.location.href = 'dashboard.html';
    }
    
    let currentDate = new Date();
    currentDate.setDate(1); // Set to 1st of month
    
    let allShifts = [];
    let holidaysObj = {};

    function formatDate(date) {
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }

    async function loadData() {
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
    }
    
    function hasShift(dateStr, type) {
        return allShifts.some(s => s.shiftDate === dateStr && s.shiftType === type);
    }

    function renderCalendar() {
        const yyyy = currentDate.getFullYear();
        const mm = currentDate.getMonth();
        document.getElementById('monthTitle').textContent = `Tháng ${mm + 1} / ${yyyy}`;
        
        const firstDay = new Date(yyyy, mm, 1);
        const lastDay = new Date(yyyy, mm + 1, 0);
        
        let firstDayIndex = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;
        
        let days = [];
        for (let i = firstDayIndex; i > 0; i--) {
            const d = new Date(yyyy, mm, 1 - i);
            days.push({ date: d, current: false });
        }
        for (let i = 1; i <= lastDay.getDate(); i++) {
            const d = new Date(yyyy, mm, i);
            days.push({ date: d, current: true });
        }
        while (days.length % 7 !== 0) {
            const nextD = new Date(yyyy, mm + 1, (days.length % 7) - firstDayIndex + 1); // rough approx
            // actually just add days linearly from lastDay
            const diff = days.length - (firstDayIndex + lastDay.getDate());
            const d = new Date(yyyy, mm + 1, diff + 1);
            days.push({ date: d, current: false });
        }

        const tbody = document.getElementById('calendarBody');
        tbody.innerHTML = '';
        
        let weeksCount = days.length / 7;
        for (let w = 0; w < weeksCount; w++) {
            const row = document.createElement('div');
            row.className = 'cal-row';
            
            let mondayStr = '';
            
            for (let i = 0; i < 7; i++) {
                const dayObj = days[w * 7 + i];
                const d = dayObj.date;
                const dateStr = formatDate(d);
                if (i === 0) mondayStr = dateStr;
                
                const isHol = holidaysObj[dateStr];
                
                let sActive = hasShift(dateStr, 'Sáng');
                let cActive = hasShift(dateStr, 'Chiều');
                let tActive = hasShift(dateStr, 'Tối');
                
                const cell = document.createElement('div');
                cell.className = `cal-cell ${!dayObj.current ? 'other-month' : ''}`;
                
                cell.innerHTML = `
                    <div class="date-num ${isHol ? 'holiday' : ''}">${d.getDate()}</div>
                    <div class="shift-row">
                        <button class="btn-shift shift-s ${sActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Sáng')" title="Ca Sáng">S</button>
                        <button class="btn-shift shift-c ${cActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Chiều')" title="Ca Chiều">C</button>
                        <button class="btn-shift shift-t ${tActive ? 'active' : ''} ${isHol ? 'holiday' : ''}" 
                            onclick="toggleShift('${dateStr}', 'Tối')" title="Ca Tối">T</button>
                    </div>
                `;
                row.appendChild(cell);
            }
            
            // Week col
            const wCell = document.createElement('div');
            wCell.className = 'cal-cell';
            wCell.innerHTML = `
                <div style="display:flex; flex-direction:column; gap:4px; margin:auto 0;">
                    <button class="btn-week-shift" onclick="toggleWeek('${mondayStr}', 'Sáng')">+ Sáng</button>
                    <button class="btn-week-shift" onclick="toggleWeek('${mondayStr}', 'Chiều')">+ Chiều</button>
                    <button class="btn-week-shift" onclick="toggleWeek('${mondayStr}', 'Tối')">+ Tối</button>
                </div>
            `;
            row.appendChild(wCell);
            tbody.appendChild(row);
        }
    }

    async function toggleShift(dateStr, type) {
        const res = await fetch('/api/shifts/toggle', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({date: dateStr, shiftType: type})
        });
        const data = await res.json();
        if (!res.ok) {
            alert(data.message || "Lỗi");
        }
        loadData();
    }
    
    async function toggleWeek(startDateStr, type) {
        const res = await fetch('/api/shifts/toggle-week', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({startDate: startDateStr, shiftType: type})
        });
        const data = await res.json();
        if (!res.ok) {
            alert(data.message || "Lỗi");
        } else {
            if (data.warning) {
                alert(data.warning);
            }
        }
        loadData();
    }

    function prevMonth() {
        currentDate.setMonth(currentDate.getMonth() - 1);
        loadData();
    }

    function nextMonth() {
        currentDate.setMonth(currentDate.getMonth() + 1);
        loadData();
    }

    loadData();
  </script>
</body>
</html>
"""

with open(r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\shifts.html", "w", encoding="utf-8") as f:
    f.write(html_content)

print("Generated shifts.html")
