import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

prices_html_content = """<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Thiết lập Giá dịch vụ - Nha khoa Admin</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="index.css">
  <style>
    /* CSS reuse from services.html for consistency */
    body { font-family: 'Inter', sans-serif; display: flex; height: 100vh; margin: 0; background-color: #f8fafc; overflow: hidden; }
    .sidebar { width: 280px; background: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; z-index: 10; flex-shrink: 0; }
    .sidebar-header { padding: 24px 20px; border-bottom: 1px solid #e2e8f0; }
    .sidebar-header h1 { margin: 0; font-size: 1.25rem; font-weight: 700; color: #0f172a; letter-spacing: -0.02em; }
    .sidebar-header p { margin: 4px 0 0; font-size: 0.8125rem; color: #64748b; }
    .nav { flex: 1; overflow-y: auto; padding: 20px 12px; display: flex; flex-direction: column; gap: 4px; }
    .nav-item { display: flex; align-items: center; gap: 12px; padding: 10px 12px; text-decoration: none; color: #475569; border-radius: 8px; font-size: 0.9375rem; font-weight: 500; transition: all 0.2s ease; }
    .nav-item:hover { background: #f1f5f9; color: #0f172a; }
    .nav-item.active { background: #e0f2fe; color: #0369a1; }
    .nav-item.active .icon { color: #0369a1; }
    .icon { flex-shrink: 0; width: 22px; height: 22px; color: #94a3b8; }
    main { flex: 1; overflow-y: auto; padding: 24px 32px; background: #f8fafc; }
    .content-shell { max-width: 960px; margin: 0 auto; }
    .crumb { font-size: 0.8125rem; color: #64748b; margin-bottom: 16px; }
    .page-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .page-head h1 { margin: 0; font-size: 1.5rem; font-weight: 700; color: #0f172a; }
    .btn { display: inline-flex; align-items: center; gap: 8px; justify-content: center; padding: 10px 16px; font-size: 0.875rem; font-weight: 600; border-radius: 8px; cursor: pointer; transition: all 0.2s; border: none; }
    .btn-primary { background: #2563eb; color: #fff; box-shadow: 0 1px 2px rgba(37, 99, 235, 0.2); }
    .btn-primary:hover { background: #1d4ed8; }
    .card { background: #fff; border-radius: 12px; border: 1px solid #e2e8f0; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    table { width: 100%; border-collapse: collapse; }
    th { padding: 12px 14px; text-align: left; font-size: 0.8125rem; font-weight: 600; color: #475569; background: #f1f5f9; border-bottom: 1px solid #e2e8f0; }
    td { padding: 12px 14px; font-size: 0.875rem; color: #0f172a; border-bottom: 1px solid #e2e8f0; }
  </style>
</head>
<body>
  <aside class="sidebar">
    <header class="sidebar-header">
      <h1>Quản trị Viên</h1>
      <p>Phòng Hành chính</p>
    </header>
    <nav class="nav" id="sidebarNav">
      <a href="appointments.html" class="nav-item everyone">Đăng ký khám</a>
      <a href="tracking.html" class="nav-item everyone">Theo dõi lịch khám</a>
      <a href="schedules.html" class="nav-item staff-only">Lịch trực bác sĩ</a>
      <a href="doctors.html" class="nav-item admin-only">Hồ sơ Bác sĩ</a>
      <a href="users.html" class="nav-item admin-only">Quản lý Người dùng</a>
      <a href="services.html" class="nav-item admin-only">Danh mục Dịch vụ</a>
      <a href="prices.html" class="nav-item admin-only active">Thiết lập giá dịch vụ</a>
      <a href="holidays.html" class="nav-item admin-only">Thiết lập ngày nghỉ</a>
      <a href="shifts.html" class="nav-item admin-only">Thiết lập ca làm việc</a>
      <div style="margin-top: 30px; border-top: 1px solid #e2e8f0; padding-top: 10px;"></div>
      <a href="#" class="nav-item" onclick="logout(event)" style="color: #dc2626;">Đăng xuất</a>
    </nav>
  </aside>
  
  <main>
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Thiết lập giá dịch vụ</strong></div>
      <div class="page-head">
        <h1>Quản lý Giá Dịch vụ</h1>
        <button class="btn btn-primary" onclick="openModal('addPriceModal')">Thêm Giá Mới</button>
      </div>
      
      <div class="card">
        <div style="margin-bottom: 16px; display: flex; gap: 10px;">
          <input type="text" id="searchInput" placeholder="Tìm theo tên dịch vụ..." onkeyup="loadPrices()" style="flex: 1; max-width: 300px; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;">
          <select id="statusFilter" style="padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" onchange="loadPrices()">
            <option value="">Tất cả trạng thái</option>
            <option value="Đang áp dụng">Đang áp dụng</option>
            <option value="Ngưng áp dụng">Ngưng áp dụng</option>
          </select>
        </div>
        
        <div id="tableContainer">
          <table id="pricesTable">
            <thead>
              <tr>
                <th>Dịch vụ</th>
                <th>Đơn giá (VNĐ)</th>
                <th>Ngày áp dụng</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Sửa Giá -->
    <div id="addPriceModal" style="position: fixed; inset: 0; background: rgba(15, 23, 42, 0.45); display: none; align-items: center; justify-content: center; z-index: 50;">
      <div style="background: #fff; border-radius: 12px; padding: 24px; max-width: 450px; width: 100%;">
        <h2 id="modalTitle" style="margin: 0 0 16px; font-size: 1.1rem;">Thêm Giá Dịch Vụ Mới</h2>
        <input type="hidden" id="priceId" />
        
        <div style="margin-bottom: 12px;">
          <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Dịch vụ (*)</label>
          <select id="dentalService" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;"></select>
        </div>
        
        <div style="margin-bottom: 12px;">
          <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Đơn giá (VNĐ) (*)</label>
          <input type="number" id="priceValue" min="1" step="1000" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />
        </div>
        
        <div style="margin-bottom: 20px;">
          <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Ngày áp dụng (*)</label>
          <input type="date" id="effectiveDate" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />
        </div>
        
        <div style="display: flex; justify-content: flex-end; gap: 10px; border-top: 1px solid #e2e8f0; padding-top: 16px;">
          <button onclick="closeModal('addPriceModal')" style="padding: 10px 16px; background: transparent; border: 1px solid #e2e8f0; border-radius: 8px; cursor: pointer;">Hủy</button>
          <button onclick="savePrice()" style="padding: 10px 16px; background: #2563eb; color: #fff; border: none; border-radius: 8px; cursor: pointer;">Lưu thông tin</button>
        </div>
      </div>
    </div>
  </main>

  <script>
    function getAuthHeaders() {
        const user = JSON.parse(localStorage.getItem('user'));
        return {
            'Content-Type': 'application/json',
            'X-Role': user ? encodeURIComponent(user.role) : ''
        };
    }

    let allServices = [];

    async function loadServices() {
        try {
            const res = await fetch('/api/services', { headers: getAuthHeaders() });
            allServices = await res.json();
            
            // Populate dropdown with active services only
            const select = document.getElementById('dentalService');
            select.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
            allServices.filter(s => s.status === 'Áp dụng').forEach(s => {
                select.innerHTML += `<option value="${s.id}">${s.name} (${s.code})</option>`;
            });
        } catch (e) {
            console.error("Failed to load services", e);
        }
    }

    async function loadPrices() {
        const res = await fetch('/api/prices', { headers: getAuthHeaders() });
        let prices = await res.json();
        
        const search = document.getElementById('searchInput').value.toLowerCase();
        const statusFilter = document.getElementById('statusFilter').value;
        
        if (search) {
            prices = prices.filter(p => p.dentalService && p.dentalService.name && p.dentalService.name.toLowerCase().includes(search));
        }
        if (statusFilter) {
            prices = prices.filter(p => p.status === statusFilter);
        }

        const tbody = document.querySelector('#pricesTable tbody');
        tbody.innerHTML = '';
        
        prices.forEach(p => {
            let statusBadge = p.status === 'Đang áp dụng' 
                ? '<span style="padding: 3px 8px; border-radius: 6px; font-size: 0.6875rem; font-weight: 600; background: #dcfce7; color: #166534;">Đang áp dụng</span>' 
                : '<span style="padding: 3px 8px; border-radius: 6px; font-size: 0.6875rem; font-weight: 600; background: #e2e8f0; color: #475569;">Ngưng áp dụng</span>';
            
            let isLocked = p.status === 'Ngưng áp dụng';
            let actions = isLocked ? '' : `
                <button onclick="editPrice(${p.id})" style="background: none; border: none; color: #2563eb; cursor: pointer; font-weight: 500;">Sửa</button> | 
                <button onclick="deactivatePrice(${p.id})" style="background: none; border: none; color: #dc2626; cursor: pointer; font-weight: 500;">Ngưng áp dụng</button>
            `;
            
            let serviceName = p.dentalService ? p.dentalService.name : 'N/A';
            let formattedPrice = new Intl.NumberFormat('vi-VN').format(p.price);
            
            tbody.innerHTML += `
                <tr>
                    <td><b>${serviceName}</b></td>
                    <td style="color: #0369a1; font-weight: 600;">${formattedPrice}</td>
                    <td>${p.effectiveDate}</td>
                    <td>${statusBadge}</td>
                    <td>${actions}</td>
                </tr>
            `;
        });
    }

    function openModal(id) { document.getElementById(id).style.display = 'flex'; }
    
    function closeModal(id) { 
        document.getElementById(id).style.display = 'none';
        document.getElementById('priceId').value = '';
        document.getElementById('dentalService').value = '';
        document.getElementById('priceValue').value = '';
        document.getElementById('effectiveDate').value = '';
        document.getElementById('dentalService').disabled = false;
        document.getElementById('modalTitle').innerText = 'Thêm Giá Dịch Vụ Mới';
    }

    async function editPrice(id) {
        const res = await fetch('/api/prices', { headers: getAuthHeaders() });
        const prices = await res.json();
        const p = prices.find(x => x.id === id);
        if(p) {
            document.getElementById('priceId').value = p.id;
            
            // Allow selecting the service even if it's inactive by re-adding it temporarily if needed
            let select = document.getElementById('dentalService');
            let optionExists = Array.from(select.options).some(opt => opt.value == p.dentalService.id);
            if (!optionExists) {
                select.innerHTML += `<option value="${p.dentalService.id}">${p.dentalService.name}</option>`;
            }
            select.value = p.dentalService.id;
            select.disabled = true; // Don't allow changing service when editing history
            
            document.getElementById('priceValue').value = p.price;
            document.getElementById('effectiveDate').value = p.effectiveDate;
            document.getElementById('modalTitle').innerText = 'Cập nhật Giá (Lưu vết)';
            openModal('addPriceModal');
        }
    }

    async function savePrice() {
        const id = document.getElementById('priceId').value;
        const data = {
            dentalService: { id: document.getElementById('dentalService').value },
            price: document.getElementById('priceValue').value,
            effectiveDate: document.getElementById('effectiveDate').value
        };
        
        let url = '/api/prices'; 
        let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        
        const res = await fetch(url, { 
            method: method, 
            headers: getAuthHeaders(),
            body: JSON.stringify(data) 
        });
        
        if (res.ok) { 
            alert(id ? "Cập nhật giá và lưu vết thành công" : "Thêm giá thành công");
            closeModal('addPriceModal'); 
            loadPrices(); 
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function deactivatePrice(id) {
        if(confirm(`Bạn có chắc chắn muốn ngưng áp dụng giá này không?`)) {
            const res = await fetch('/api/prices/' + id + '/deactivate', { 
                method: 'PUT', 
                headers: getAuthHeaders() 
            });
            if(res.ok) {
                alert(`Ngưng áp dụng thành công`);
                loadPrices();
            } else {
                const err = await res.json();
                alert("Lỗi: " + err.message);
            }
        }
    }

    window.onload = function() {
        const user = JSON.parse(localStorage.getItem('user'));
        let normalizedRole = '';
        if (user && user.role) {
            normalizedRole = user.role.toLowerCase()
                .replace(/đ/g, 'd').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/\s+/g, '-');
        }

        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            if (normalizedRole === 'quan-ly-phong-kham') {
                document.querySelectorAll('.admin-only').forEach(el => {
                    if (el.textContent.includes('Hồ sơ Bác sĩ') || el.textContent.includes('Danh mục Dịch vụ') || el.textContent.includes('Thiết lập giá dịch vụ')) return;
                    el.style.display = 'none';
                });
            }
            loadServices().then(loadPrices);
        }
    }

    function logout(e) {
      if (e) e.preventDefault();
      localStorage.removeItem('user');
      window.location.href = 'login.html';
    }
  </script>
</body>
</html>
"""

with open(os.path.join(base_dir, "prices.html"), 'w', encoding='utf-8') as f:
    f.write(prices_html_content)

# Patch sidebars in all HTML files
for filename in os.listdir(base_dir):
    if not filename.endswith('.html'):
        continue
    
    filepath = os.path.join(base_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # We want to insert the link right after 'Danh mục Dịch vụ'
    target_link = '<a href="services.html" class="nav-item admin-only'
    insert_str = '<a href="prices.html" class="nav-item admin-only'
    
    if target_link in content and insert_str not in content:
        # Find the end of the services.html anchor tag
        parts = content.split('Danh mục Dịch vụ</a>')
        if len(parts) == 2:
            new_link = '\n      <a href="prices.html" class="nav-item admin-only">\n        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg></span>\n        <span class="menu-text">Thiết lập giá dịch vụ</span>\n      </a>'
            content = parts[0] + 'Danh mục Dịch vụ</a>' + new_link + parts[1]
            
            # Also update role check in scripts for Quản lý phòng khám if present
            content = content.replace("el.textContent.includes('Danh mục Dịch vụ'))", "el.textContent.includes('Danh mục Dịch vụ') || el.textContent.includes('Thiết lập giá dịch vụ'))")
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Patched sidebar in {filename}")

print("UI Patch completed successfully!")
