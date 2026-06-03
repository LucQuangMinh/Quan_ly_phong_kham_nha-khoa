import os

file_path = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\services.html"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

new_main = """
  <main>
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Danh mục Dịch vụ</strong></div>
      <div class="page-head">
        <h1>Quản lý Dịch vụ Nha khoa</h1>
        <button class="btn btn-primary" onclick="openModal('addServiceModal')">Thêm mới</button>
      </div>
      
      <div class="card">
        <div class="toolbar" style="display: flex; gap: 10px; margin-bottom: 14px;">
          <input type="text" id="searchInput" class="search" placeholder="Tìm theo mã, tên..." onkeyup="loadServices()" style="flex: 1; max-width: 320px; padding: 9px 12px; border: 1px solid #e2e8f0; border-radius: 8px;" />
          <select id="groupFilter" class="search" style="max-width: 200px; padding: 9px 12px; border: 1px solid #e2e8f0; border-radius: 8px;" onchange="loadServices()">
            <option value="">Tất cả nhóm dịch vụ</option>
            <option value="Khám">Khám</option>
            <option value="Điều trị">Điều trị</option>
            <option value="Thẩm mỹ">Thẩm mỹ</option>
            <option value="Phẫu thuật">Phẫu thuật</option>
          </select>
        </div>
        
        <div id="noDataMessage" style="display: none; padding: 20px; text-align: center; color: #64748b; font-style: italic;">
          Không tìm thấy dịch vụ phù hợp
        </div>

        <div class="table-wrap" id="tableContainer">
          <table id="servicesTable" style="width: 100%; border-collapse: collapse;">
            <thead style="background: #f1f5f9; text-align: left;">
              <tr>
                <th style="padding: 10px 14px;">Mã DV</th>
                <th style="padding: 10px 14px;">Tên dịch vụ</th>
                <th style="padding: 10px 14px;">Nhóm</th>
                <th style="padding: 10px 14px;">Mô tả</th>
                <th style="padding: 10px 14px;">Đơn vị tính</th>
                <th style="padding: 10px 14px;">Trạng thái</th>
                <th style="padding: 10px 14px;">Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Sửa Dịch vụ -->
    <div class="modal-backdrop" id="addServiceModal" style="position: fixed; inset: 0; background: rgba(15, 23, 42, 0.45); display: none; align-items: center; justify-content: center; z-index: 50;">
      <div class="modal" style="background: #fff; border-radius: 12px; padding: 22px 24px; max-width: 500px; width: 100%;">
        <h2 id="modalTitle" style="margin: 0 0 16px; font-size: 1.0625rem; font-weight: 600;">Thêm Dịch vụ mới</h2>
        <input type="hidden" id="serviceId" />
        
        <div class="form-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
          <div class="field" style="grid-column: 1 / -1;">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Mã dịch vụ (*)</label>
            <input type="text" id="serviceCode" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />
          </div>
          <div class="field" style="grid-column: 1 / -1;">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Tên dịch vụ (*)</label>
            <input type="text" id="serviceName" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />
          </div>
          <div class="field">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Nhóm dịch vụ (*)</label>
            <select id="serviceCategory" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;">
              <option value="">-- Chọn nhóm --</option>
              <option value="Khám">Khám</option>
              <option value="Điều trị">Điều trị</option>
              <option value="Thẩm mỹ">Thẩm mỹ</option>
              <option value="Phẫu thuật">Phẫu thuật</option>
            </select>
          </div>
          <div class="field">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Đơn vị tính</label>
            <input type="text" id="serviceUnit" placeholder="Lần, Răng, Trọn gói..." style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />
          </div>
          <div class="field" style="grid-column: 1 / -1;">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Mô tả chi tiết</label>
            <textarea id="serviceDesc" rows="3" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px; resize: vertical;"></textarea>
          </div>
        </div>
        
        <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; padding-top: 16px; border-top: 1px solid #e2e8f0;">
          <button class="btn btn-ghost" onclick="closeModal('addServiceModal')" style="padding: 10px 18px; border-radius: 8px; background: transparent; border: 1px solid #e2e8f0; cursor: pointer; font-weight: 600;">Hủy</button>
          <button class="btn btn-primary" onclick="saveService()" style="padding: 10px 18px; border-radius: 8px; background: #2563eb; color: #fff; border: none; cursor: pointer; font-weight: 600;">Lưu thông tin</button>
        </div>
      </div>
    </div>

  </main>

  <script>
    async function loadServices() {
        const res = await fetch('/api/services');
        const services = await res.json();
        
        const search = document.getElementById('searchInput').value.toLowerCase();
        const groupFilter = document.getElementById('groupFilter').value;
        
        const filtered = services.filter(s => 
            ((s.serviceCode && s.serviceCode.toLowerCase().includes(search)) || 
             (s.serviceName && s.serviceName.toLowerCase().includes(search))) &&
            (groupFilter === '' || s.category === groupFilter)
        );

        const tbody = document.querySelector('#servicesTable tbody');
        const noData = document.getElementById('noDataMessage');
        const tableContainer = document.getElementById('tableContainer');
        
        tbody.innerHTML = '';
        
        if (filtered.length === 0) {
            noData.style.display = 'block';
            tableContainer.style.display = 'none';
        } else {
            noData.style.display = 'none';
            tableContainer.style.display = 'block';
            
            filtered.forEach(s => {
                let statusBadge = s.status === 'Áp dụng' 
                    ? '<span style="display:inline-block; padding: 3px 8px; border-radius: 6px; font-size: 0.6875rem; font-weight: 600; background: #dcfce7; color: #166534;">Áp dụng</span>' 
                    : '<span style="display:inline-block; padding: 3px 8px; border-radius: 6px; font-size: 0.6875rem; font-weight: 600; background: #e2e8f0; color: #475569;">Ngưng áp dụng</span>';
                
                let isLocked = s.status === 'Ngưng áp dụng';
                let lockText = isLocked ? 'Kích hoạt lại' : 'Ngưng áp dụng';
                let lockColor = isLocked ? '#2563eb' : '#dc2626';
                
                let actions = `
                    <button onclick="editService(${s.id})" style="background: none; border: none; color: #2563eb; cursor: pointer; font-weight: 500; font-family: inherit;">Sửa</button> | 
                    <button onclick="toggleServiceStatus(${s.id}, '${lockText}')" style="background: none; border: none; color: ${lockColor}; cursor: pointer; font-weight: 500; font-family: inherit;">${lockText}</button>
                `;
                
                tbody.innerHTML += `
                    <tr>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;"><b>${s.serviceCode}</b></td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;">${s.serviceName}</td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;">${s.category || ''}</td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0; max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${s.description || ''}</td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;">${s.unit || ''}</td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;">${statusBadge}</td>
                        <td style="padding: 10px 14px; border-bottom: 1px solid #e2e8f0;">${actions}</td>
                    </tr>
                `;
            });
        }
    }

    function openModal(id) { 
        document.getElementById(id).style.display = 'flex'; 
    }

    function closeModal(id) { 
        document.getElementById(id).style.display = 'none'; 
        if (id === 'addServiceModal') {
            document.getElementById('serviceId').value = '';
            document.getElementById('serviceCode').value = '';
            document.getElementById('serviceName').value = '';
            document.getElementById('serviceCategory').value = '';
            document.getElementById('serviceDesc').value = '';
            document.getElementById('serviceUnit').value = '';
            document.getElementById('modalTitle').innerText = 'Thêm Dịch vụ mới';
        }
    }

    async function editService(id) {
        const res = await fetch('/api/services');
        const services = await res.json();
        const s = services.find(x => x.id === id);
        if(s) {
            document.getElementById('serviceId').value = s.id;
            document.getElementById('serviceCode').value = s.serviceCode;
            document.getElementById('serviceName').value = s.serviceName;
            document.getElementById('serviceCategory').value = s.category || '';
            document.getElementById('serviceDesc').value = s.description || '';
            document.getElementById('serviceUnit').value = s.unit || '';
            document.getElementById('modalTitle').innerText = 'Sửa thông tin Dịch vụ';
            openModal('addServiceModal');
        }
    }

    async function saveService() {
        const id = document.getElementById('serviceId').value;
        const data = {
            serviceCode: document.getElementById('serviceCode').value, 
            serviceName: document.getElementById('serviceName').value,
            category: document.getElementById('serviceCategory').value,
            description: document.getElementById('serviceDesc').value,
            unit: document.getElementById('serviceUnit').value
        };
        
        let url = '/api/services'; 
        let method = 'POST';
        
        if (id) { 
            url += '/' + id; 
            method = 'PUT'; 
        }
        
        const res = await fetch(url, { 
            method: method, 
            headers: {'Content-Type': 'application/json'}, 
            body: JSON.stringify(data) 
        });
        
        if (res.ok) { 
            alert(id ? "Cập nhật dịch vụ thành công" : "Thêm mới dịch vụ thành công");
            closeModal('addServiceModal'); 
            loadServices(); 
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function toggleServiceStatus(id, actionName) {
        if(confirm(`Bạn có chắc chắn muốn ${actionName.toLowerCase()} dịch vụ này không?`)) {
            let endpoint = actionName === 'Ngưng áp dụng' ? '/deactivate' : '/activate';
            const res = await fetch('/api/services/' + id + endpoint, { 
                method: 'PUT', 
                headers: {'Content-Type': 'application/json'} 
            });
            if(res.ok) {
                alert(`${actionName} dịch vụ thành công`);
                loadServices();
            } else {
                const err = await res.json();
                alert("Lỗi: " + err.message);
            }
        }
    }

    // Auth check logic
    window.onload = function() {
        const user = JSON.parse(localStorage.getItem('user'));
        if(!user || user.role !== 'Admin') {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            loadServices();
        }
    }
  </script>
</body>
</html>
"""

idx = content.find('</aside>')
if idx != -1:
    idx += len('</aside>')
    new_html = content[:idx] + new_main
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(new_html)
    print("Successfully patched services.html")
else:
    print("Could not find </aside>")
