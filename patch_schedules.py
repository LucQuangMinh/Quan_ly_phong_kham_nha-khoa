import os

file_path = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

html_content = """<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Đăng ký lịch trực</title>
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
      --table-head: #f1f5f9;
      --danger: #dc2626;
      --danger-bg: #fef2f2;
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
    .nav-item { display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: var(--radius-item); font-size: 0.875rem; font-weight: 500; color: var(--text-primary); text-decoration: none; border: none; background: transparent; width: 100%; text-align: left; cursor: pointer; transition: background 0.15s ease, color 0.15s ease; }
    .nav-item:hover { background: var(--hover-bg); }
    .nav-item .icon { flex-shrink: 0; width: 22px; height: 22px; color: var(--text-muted); }
    .nav-item.active { background: var(--active-bg); color: var(--active-text); }
    .nav-item.active .icon { color: var(--active-text); }
    
    main { flex: 1; min-width: 0; padding: 24px 32px 40px; background: #fafbfc; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }
    
    .page-head { display: flex; flex-wrap: wrap; align-items: flex-start; justify-content: space-between; gap: 14px; margin-bottom: 18px; flex-shrink: 0; }
    .page-head h1 { margin: 0; font-size: 1.25rem; font-weight: 600; }
    .crumb { font-size: 0.8125rem; color: var(--text-muted); margin: 0 0 16px; flex-shrink: 0; }
    .crumb strong { color: var(--text-primary); font-weight: 600; }

    .schedule-layout { display: flex; gap: 20px; flex: 1; min-height: 0; }
    
    .calendar-pane { flex: 1; display: flex; flex-direction: column; background: var(--surface); border: 1px solid var(--sidebar-border); border-radius: 12px; padding: 20px; min-width: 0; overflow-y: auto; }
    .doctor-pane { width: 320px; background: var(--surface); border: 1px solid var(--sidebar-border); border-radius: 12px; display: flex; flex-direction: column; overflow: hidden; flex-shrink: 0; }
    
    /* Calendar styles */
    .calendar-nav { display: flex; align-items: center; gap: 15px; margin-bottom: 15px; }
    .btn-ghost { background: transparent; border: 1px solid var(--sidebar-border); padding: 6px 12px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 0.875rem; }
    .btn-ghost:hover { background: var(--hover-bg); }
    
    .calendar-grid-wrap { border: 1px solid var(--sidebar-border); border-radius: 8px; overflow: hidden; }
    .cal-header-row { display: grid; grid-template-columns: repeat(8, 1fr); background: #f1f5f9; border-bottom: 1px solid var(--sidebar-border); }
    .cal-header-cell { padding: 10px; text-align: center; font-weight: 600; font-size: 0.875rem; color: #475569; border-right: 1px solid var(--sidebar-border); }
    .cal-header-cell:last-child { border-right: none; }
    
    .cal-body { display: flex; flex-direction: column; }
    .cal-row { display: grid; grid-template-columns: repeat(8, 1fr); border-bottom: 1px solid var(--sidebar-border); }
    .cal-row:last-child { border-bottom: none; }
    .cal-cell { min-height: 120px; padding: 6px; border-right: 1px solid var(--sidebar-border); background: #fff; display: flex; flex-direction: column; gap: 4px; }
    .cal-cell:last-child { border-right: none; background: #f8fafc; align-items: center; justify-content: center; padding: 10px; }
    
    .cal-date { font-weight: 600; font-size: 0.875rem; margin-bottom: 4px; padding-bottom: 4px; border-bottom: 1px dashed #e2e8f0; }
    .cal-cell.out-month { opacity: 0.5; }
    .cal-cell.is-holiday { background: #fef2f2 !important; }
    
    .shift-block { border: 1px solid #e2e8f0; border-radius: 6px; padding: 6px; font-size: 0.75rem; cursor: pointer; transition: all 0.15s; background: #fff; margin-bottom: 4px; }
    .shift-block:hover { border-color: #cbd5e1; background: #f1f5f9; }
    .shift-title { font-weight: 700; margin-bottom: 4px; }
    .shift-title.sang { color: #2563eb; }
    .shift-title.chieu { color: #d97706; }
    .shift-title.toi { color: #7c3aed; }
    
    .doc-tag { background: #dcfce7; border: 1px solid #86efac; color: #15803d; padding: 2px 6px; border-radius: 4px; font-size: 0.6875rem; display: flex; justify-content: space-between; align-items: center; margin-bottom: 2px; }
    .doc-tag.proposal { background: #fef9c3; border: 1px dashed #fef08a; color: #a16207; font-style: italic; cursor: pointer; }
    .doc-tag.proposal:hover { background: #fef08a; }
    
    .remove-btn { color: #ef4444; cursor: pointer; font-weight: bold; margin-left: 4px; padding: 0 4px; }
    .remove-btn:hover { background: #fecaca; border-radius: 2px; }
    
    .week-action-btn { width: 100%; padding: 4px 0; margin-bottom: 4px; font-size: 0.75rem; font-weight: 600; border: 1px solid #cbd5e1; border-radius: 4px; background: #fff; cursor: pointer; }
    .week-action-btn:hover { background: #e2e8f0; }

    /* Doctor list styles */
    .doc-pane-header { padding: 16px; border-bottom: 1px solid var(--sidebar-border); background: #f8fafc; font-weight: 600; display: flex; justify-content: space-between; align-items: center; }
    .doc-list { overflow-y: auto; padding: 10px; flex: 1; }
    .doc-item { display: flex; align-items: center; gap: 10px; padding: 8px 10px; border-radius: 6px; cursor: pointer; }
    .doc-item:hover { background: var(--hover-bg); }
    .doc-item input { cursor: pointer; }
    .doc-name { font-size: 0.875rem; font-weight: 500; }
  </style>
</head>
<body>
  <!-- SIDEBAR WILL BE INJECTED HERE BY SCRIPT, or hardcoded for now -->
  <aside class="sidebar" aria-label="Điều hướng chính">
    <header class="sidebar-header">
      <h1>Hệ thống Nha Khoa</h1>
      <p id="userRoleDisplay">Quản trị Viên</p>
    </header>
    <nav class="nav" aria-label="Menu quản trị">
      <a href="dashboard.html" class="nav-item">Bảng điều khiển</a>
      <a href="users.html" class="nav-item admin-only">Quản lý Người dùng</a>
      <a href="doctors.html" class="nav-item admin-only">Hồ sơ Bác sĩ</a>
      <a href="services.html" class="nav-item admin-only">Danh mục Dịch vụ</a>
      <a href="holidays.html" class="nav-item admin-only">Thiết lập ngày nghỉ</a>
      <a href="schedules.html" class="nav-item active">Đăng ký lịch trực</a>
    </nav>
  </aside>

  <main>
    <div class="crumb">Hệ thống / <strong>Đăng ký lịch trực</strong></div>
    <div class="page-head">
      <h1 id="pageTitle">Quản lý lịch trực hệ thống (Admin)</h1>
    </div>
    
    <div class="schedule-layout">
      <!-- Lịch trực quan -->
      <div class="calendar-pane">
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
            <div class="cal-header-cell admin-only">Cả tuần</div>
          </div>
          <div class="cal-body" id="calendarBody">
            <!-- Rendered by JS -->
          </div>
        </div>
      </div>
      
      <!-- Danh sách bác sĩ (Chỉ Admin) -->
      <div class="doctor-pane admin-only" id="doctorPane">
        <div class="doc-pane-header">
          <span>Danh sách Bác sĩ</span>
          <label style="font-size: 0.8125rem; font-weight: normal; cursor: pointer;">
            <input type="checkbox" id="selectAllDocs" onchange="toggleAllDocs()" /> Chọn hết
          </label>
        </div>
        <div class="doc-list" id="doctorList">
          <!-- Rendered by JS -->
        </div>
      </div>
    </div>
  </main>

  <script>
    const userStr = localStorage.getItem('user');
    if (!userStr) window.location.href = 'login.html';
    const currentUser = JSON.parse(userStr);
    const isAdmin = currentUser.role === 'Admin';
    const currentDocId = currentUser.doctorId; // Backend needs to supply this on login, or we match by user_id

    document.getElementById('userRoleDisplay').innerText = currentUser.role;
    
    if (!isAdmin) {
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
        document.getElementById('pageTitle').innerText = 'Đăng ký lịch trực cá nhân';
        document.getElementById('doctorPane').style.display = 'none';
    }

    let currentMonth = new Date().getMonth();
    let currentYear = new Date().getFullYear();
    let allHolidays = [];
    let allSchedules = [];
    let allDoctors = [];

    async function loadData() {
        // Load holidays
        const hRes = await fetch('/api/holidays');
        allHolidays = await hRes.json();
        
        // Load schedules (Admin loads all, Doctor loads self)
        let sUrl = `/api/schedules?start=${currentYear}-${String(currentMonth+1).padStart(2,'0')}-01&end=${currentYear}-${String(currentMonth+1).padStart(2,'0')}-28&role=${currentUser.role}`;
        if (!isAdmin && currentUser.id) {
            // Need doctorId. Assuming user ID maps to Doctor ID for simplicity if missing, or we find it.
            sUrl += `&doctorId=${currentUser.id}`; // Simple mock
        }
        
        // Actually, let's fetch start of grid and end of grid
        const firstDay = new Date(currentYear, currentMonth, 1);
        let startDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; 
        let gridStartDate = new Date(currentYear, currentMonth, 1 - startDayOfWeek);
        let gridEndDate = new Date(gridStartDate.getFullYear(), gridStartDate.getMonth(), gridStartDate.getDate() + 41);
        
        let url = `/api/schedules?start=${getLocalIsoDate(gridStartDate)}&end=${getLocalIsoDate(gridEndDate)}&role=${currentUser.role}`;
        if (!isAdmin) url += `&doctorId=${currentUser.id}`;
        
        const sRes = await fetch(url);
        allSchedules = await sRes.json();
        
        if (isAdmin) {
            const dRes = await fetch('/api/doctors');
            allDoctors = await dRes.json();
            renderDoctorList();
        }
        
        renderCalendar();
    }

    function prevMonth() { currentMonth--; if(currentMonth < 0) { currentMonth = 11; currentYear--; } loadData(); }
    function nextMonth() { currentMonth++; if(currentMonth > 11) { currentMonth = 0; currentYear++; } loadData(); }
    function getLocalIsoDate(dateObj) { const d = new Date(dateObj); d.setMinutes(d.getMinutes() - d.getTimezoneOffset()); return d.toISOString().split('T')[0]; }

    function getSelectedDoctorIds() {
        const checkboxes = document.querySelectorAll('.doc-checkbox:checked');
        return Array.from(checkboxes).map(cb => parseInt(cb.value));
    }

    function toggleAllDocs() {
        const isChecked = document.getElementById('selectAllDocs').checked;
        document.querySelectorAll('.doc-checkbox').forEach(cb => cb.checked = isChecked);
    }

    function renderDoctorList() {
        const list = document.getElementById('doctorList');
        list.innerHTML = '';
        allDoctors.forEach(doc => {
            list.innerHTML += `
                <label class="doc-item">
                    <input type="checkbox" class="doc-checkbox" value="${doc.id}" />
                    <span class="doc-name">${doc.name} (${doc.code})</span>
                </label>
            `;
        });
    }

    function renderCalendar() {
        document.getElementById('monthTitle').innerText = `Tháng ${currentMonth + 1} / ${currentYear}`;
        
        const firstDay = new Date(currentYear, currentMonth, 1);
        let startDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; 
        let gridStartDate = new Date(currentYear, currentMonth, 1 - startDayOfWeek);
        
        const calBody = document.getElementById('calendarBody');
        calBody.innerHTML = '';
        let todayStr = getLocalIsoDate(new Date());

        for (let row = 0; row < 6; row++) {
            let rowHtml = '<div class="cal-row">';
            let weekDays = [];
            
            for (let col = 0; col < 7; col++) {
                let d = new Date(gridStartDate.getFullYear(), gridStartDate.getMonth(), gridStartDate.getDate() + (row * 7) + col);
                let isoStr = getLocalIsoDate(d);
                weekDays.push(isoStr);
                
                let isCurrentMonth = d.getMonth() === currentMonth;
                let isHol = allHolidays.some(h => h.holidayDate === isoStr);
                
                let cls = 'cal-cell';
                if (!isCurrentMonth) cls += ' out-month';
                if (isHol) cls += ' is-holiday';
                
                let badge = (isoStr === todayStr) ? '<span style="color:#2563eb; font-size:0.7rem; float:right;">(Hôm nay)</span>' : '';
                
                let cellHtml = `<div class="${cls}">
                    <div class="cal-date">${d.getDate()} ${badge}</div>`;
                
                if (isHol) {
                    cellHtml += `<div style="color: #dc2626; font-size: 0.8rem; font-weight: 600; text-align: center; margin-top: 20px;">Nghỉ Lễ</div>`;
                } else {
                    // Render Shifts
                    const shifts = ['Sáng', 'Chiều', 'Tối'];
                    shifts.forEach(st => {
                        let shiftClass = st === 'Sáng' ? 'sang' : st === 'Chiều' ? 'chieu' : 'toi';
                        let blockClick = isAdmin ? `onclick="toggleShiftAdmin('${isoStr}', '${st}')"` : `onclick="toggleShiftDoctor('${isoStr}', '${st}')"`;
                        
                        let tagsHtml = '';
                        let daySchedules = allSchedules.filter(s => s.shiftDate === isoStr && s.shiftType === st);
                        
                        daySchedules.forEach(s => {
                            if (isAdmin) {
                                if (s.status === 'Đề xuất của tôi') {
                                    tagsHtml += `<div class="doc-tag proposal" onclick="event.stopPropagation(); approveProposal(${s.id})" title="Nhấn để duyệt">
                                        ${s.doctor.name} (Đề xuất)
                                    </div>`;
                                } else {
                                    tagsHtml += `<div class="doc-tag">
                                        ${s.doctor.name}
                                        <span class="remove-btn" onclick="event.stopPropagation(); removeDoc(${s.id})">×</span>
                                    </div>`;
                                }
                            } else {
                                // Doctor view
                                if (s.status === 'Đề xuất của tôi') {
                                    tagsHtml += `<div class="doc-tag proposal" onclick="event.stopPropagation(); removeDoc(${s.id})" title="Nhấn để hủy">Đề xuất của tôi (×)</div>`;
                                } else {
                                    tagsHtml += `<div class="doc-tag">Đã duyệt trực</div>`;
                                }
                            }
                        });
                        
                        cellHtml += `
                            <div class="shift-block" ${blockClick}>
                                <div class="shift-title ${shiftClass}">${st}</div>
                                ${tagsHtml}
                            </div>
                        `;
                    });
                }
                
                cellHtml += `</div>`;
                rowHtml += cellHtml;
            }
            
            // Cột tác vụ tuần (Chỉ Admin)
            if (isAdmin) {
                let weekStartDate = weekDays[0];
                rowHtml += `
                    <div class="cal-cell">
                        <button class="week-action-btn" onclick="toggleWeekAdmin('${weekStartDate}', 'Sáng')">+ Sáng</button>
                        <button class="week-action-btn" onclick="toggleWeekAdmin('${weekStartDate}', 'Chiều')">+ Chiều</button>
                        <button class="week-action-btn" onclick="toggleWeekAdmin('${weekStartDate}', 'Tối')">+ Tối</button>
                    </div>
                `;
            } else {
                rowHtml += `<div class="cal-cell" style="display:none;"></div>`; // Ẩn cột tuần
            }
            
            rowHtml += '</div>';
            calBody.innerHTML += rowHtml;
        }
    }

    async function toggleShiftAdmin(date, shiftType) {
        const docIds = getSelectedDoctorIds();
        if (docIds.length === 0) {
            alert("Vui lòng tích chọn ít nhất một bác sĩ từ danh sách bên phải để xếp ca trực!");
            return;
        }
        const res = await fetch('/api/schedules/toggle-bulk', {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ doctorIds: docIds, date: date, shiftType: shiftType })
        });
        if(res.ok) loadData(); else alert((await res.json()).message);
    }

    async function toggleWeekAdmin(startDate, shiftType) {
        const docIds = getSelectedDoctorIds();
        if (docIds.length === 0) {
            alert("Vui lòng tích chọn ít nhất một bác sĩ từ danh sách bên phải để xếp ca trực!");
            return;
        }
        const res = await fetch('/api/schedules/toggle-week', {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ doctorIds: docIds, startDate: startDate, shiftType: shiftType })
        });
        if(res.ok) loadData(); else alert((await res.json()).message);
    }

    async function toggleShiftDoctor(date, shiftType) {
        // Kiểm tra xem đã có lịch duyệt chưa
        let daySchedules = allSchedules.filter(s => s.shiftDate === date && s.shiftType === shiftType);
        if (daySchedules.length > 0 && daySchedules[0].status === 'Đã duyệt trực') {
            alert("Lịch đã được Admin duyệt, bạn không thể tự ý hủy!");
            return;
        }
        
        const res = await fetch('/api/schedules/toggle-self', {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ doctorId: currentUser.id, date: date, shiftType: shiftType })
        });
        if(res.ok) loadData(); else alert((await res.json()).message);
    }

    async function approveProposal(id) {
        if(confirm("Xác nhận phê duyệt lịch trực này?")) {
            const res = await fetch(`/api/schedules/${id}/approve`, { method: 'PUT' });
            if(res.ok) loadData(); else alert((await res.json()).message);
        }
    }

    async function removeDoc(id) {
        if(confirm("Xác nhận xóa lịch trực này?")) {
            const res = await fetch(`/api/schedules/${id}`, { method: 'DELETE' });
            if(res.ok) loadData(); else alert((await res.json()).message);
        }
    }

    window.onload = loadData;
  </script>
</body>
</html>
"""

with open(file_path, "w", encoding="utf-8") as f:
    f.write(html_content)
print("Successfully generated schedules.html")
