import os
import re

file_path = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\doctors.html"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# The content we want to inject after `</nav>\n  </aside>`
new_main = """
  <main>
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Hồ sơ Bác sĩ</strong></div>
      <div class="page-head">
        <h1>Quản lý Bác sĩ</h1>
        <button class="btn btn-primary" onclick="openModal('addDocModal')">Thêm Bác sĩ</button>
      </div>
      <div class="card">
        <div class="toolbar">
          <input type="text" id="searchInput" class="search" placeholder="Tìm kiếm theo mã, tên, sđt..." onkeyup="loadDocs()" />
          <select id="statusFilter" class="search" style="max-width: 200px;" onchange="loadDocs()">
            <option value="">Tất cả trạng thái</option>
            <option value="Đang làm việc">Đang làm việc</option>
            <option value="Ngưng hoạt động">Ngưng hoạt động</option>
          </select>
        </div>
        <div class="table-wrap">
          <table id="docsTable">
            <thead>
              <tr>
                <th>Mã BS</th>
                <th>Họ tên</th>
                <th>Ngày sinh</th>
                <th>SĐT / Email</th>
                <th>Bằng cấp</th>
                <th>Tài khoản</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Sửa Bác sĩ -->
    <div class="modal-backdrop" id="addDocModal">
      <div class="modal" style="max-width: 650px;">
        <h2 id="docModalTitle">Thêm Bác sĩ mới</h2>
        <input type="hidden" id="docId" />
        <div class="form-grid">
          <div class="field"><label>Mã Bác sĩ (*)</label><input type="text" id="docCode" placeholder="VD: BS01" /></div>
          <div class="field"><label>Họ và tên (*)</label><input type="text" id="docName" placeholder="VD: Nguyễn Văn A" /></div>
          <div class="field"><label>Ngày sinh</label><input type="date" id="docDob" /></div>
          <div class="field"><label>Số điện thoại</label><input type="text" id="docPhone" placeholder="10-11 số" /></div>
          <div class="field"><label>Email</label><input type="email" id="docEmail" placeholder="abc@gmail.com" /></div>
          <div class="field"><label>Nơi công tác</label><input type="text" id="docWorkplace" /></div>
          <div class="field-full"><label>Bằng cấp / Học hàm</label><input type="text" id="docDegree" /></div>
          <div class="field-full">
            <label>Tài khoản liên kết (Chỉ hiển thị User có Role: Bác sĩ)</label>
            <select id="docUserId">
                <option value="">-- Không gán tài khoản --</option>
            </select>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addDocModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveDoc()">Lưu thông tin</button>
        </div>
      </div>
    </div>

    <!-- Modal Gán tài khoản (Cách 2) -->
    <div class="modal-backdrop" id="assignModal">
      <div class="modal">
        <h2>Gán tài khoản cho Bác sĩ</h2>
        <input type="hidden" id="assignDocId" />
        <p>Bác sĩ: <strong id="assignDocName"></strong></p>
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field">
            <label>Chọn tài khoản User</label>
            <select id="assignUserId">
                <option value="">-- Chọn tài khoản --</option>
            </select>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('assignModal')">Hủy</button>
          <button class="btn btn-primary" onclick="submitAssignUser()">Cập nhật</button>
        </div>
      </div>
    </div>

  </main>

  <script>
    let allUsers = [];

    // Lấy danh sách users để map userId -> username và điền vào dropdown
    async function fetchUsers() {
        try {
            const res = await fetch('/api/users');
            if (res.ok) {
                allUsers = await res.json();
            }
        } catch (e) {
            console.error("Could not fetch users", e);
        }
    }

    function populateUserDropdown(selectId, currentUserId = null) {
        const select = document.getElementById(selectId);
        select.innerHTML = '<option value="">-- Không gán / Chọn tài khoản --</option>';
        
        // Theo nghiệp vụ: Chỉ hiển thị user có Role="Bác sĩ"
        const doctorUsers = allUsers.filter(u => u.role === 'Bác sĩ');
        
        doctorUsers.forEach(u => {
            const opt = document.createElement('option');
            opt.value = u.id;
            opt.textContent = `ID: ${u.id} - ${u.username} (${u.fullname})`;
            if (currentUserId && u.id === currentUserId) {
                opt.selected = true;
            }
            select.appendChild(opt);
        });
    }

    async function loadDocs() {
        await fetchUsers(); // Đảm bảo đã có danh sách users

        const res = await fetch('/api/doctors');
        const docs = await res.json();
        const search = document.getElementById('searchInput').value.toLowerCase();
        const statusFilter = document.getElementById('statusFilter').value;
        
        const tbody = document.querySelector('#docsTable tbody');
        tbody.innerHTML = '';
        
        docs.filter(d => 
            ((d.code && d.code.toLowerCase().includes(search)) || 
             (d.fullname && d.fullname.toLowerCase().includes(search)) || 
             (d.phone && d.phone.toLowerCase().includes(search))) &&
            (statusFilter === '' || d.status === statusFilter)
        ).forEach(d => {
            let statusBadge = d.status === 'Đang làm việc' ? '<span class="badge badge-le">Đang làm việc</span>' : '<span class="badge badge-khac">Ngưng hoạt động</span>';
            
            // Tìm username được liên kết
            let accountInfo = '<span style="color:#94a3b8; font-style:italic">Chưa gán</span>';
            if (d.userId) {
                const u = allUsers.find(x => x.id === d.userId);
                accountInfo = u ? `<b>${u.username}</b>` : `ID: ${d.userId}`;
            }

            let actions = `
                <button class="link-btn" onclick="editDoc(${d.id})">Sửa</button> | 
                <button class="link-btn" onclick="openAssignModal(${d.id}, '${d.fullname}', ${d.userId || 'null'})">Gán TK</button>
            `;
            
            let isLocked = d.status === 'Ngưng hoạt động';
            let lockText = isLocked ? 'Kích hoạt lại' : 'Ngưng hoạt động';
            actions += ` | <button class="link-btn ${isLocked ? '' : 'danger'}" onclick="toggleDocStatus(${d.id}, '${lockText}')">${lockText}</button>`;
            
            tbody.innerHTML += `
                <tr>
                    <td><b>${d.code}</b></td>
                    <td>${d.fullname}</td>
                    <td>${d.dateOfBirth ? d.dateOfBirth : ''}</td>
                    <td>${d.phone || ''}<br><span style="color:#64748b; font-size:0.75rem">${d.email || ''}</span></td>
                    <td>${d.degree || ''}</td>
                    <td>${accountInfo}</td>
                    <td>${statusBadge}</td>
                    <td>${actions}</td>
                </tr>
            `;
        });
    }

    function openModal(id) { 
        if(id === 'addDocModal' && !document.getElementById('docId').value) {
            // Khi mở thêm mới, điền dropdown user
            populateUserDropdown('docUserId');
        }
        document.getElementById(id).classList.add('is-open'); 
    }

    function closeModal(id) { 
        document.getElementById(id).classList.remove('is-open'); 
        if (id === 'addDocModal') {
            document.getElementById('docId').value = '';
            document.getElementById('docCode').value = '';
            document.getElementById('docName').value = '';
            document.getElementById('docDob').value = '';
            document.getElementById('docPhone').value = '';
            document.getElementById('docEmail').value = '';
            document.getElementById('docDegree').value = '';
            document.getElementById('docWorkplace').value = '';
            document.getElementById('docUserId').value = '';
            document.getElementById('docModalTitle').innerText = 'Thêm Bác sĩ mới';
        } else if (id === 'assignModal') {
            document.getElementById('assignDocId').value = '';
            document.getElementById('assignUserId').value = '';
        }
    }

    async function editDoc(id) {
        const res = await fetch('/api/doctors');
        const docs = await res.json();
        const d = docs.find(x => x.id === id);
        if(d) {
            document.getElementById('docId').value = d.id;
            document.getElementById('docCode').value = d.code;
            document.getElementById('docName').value = d.fullname;
            document.getElementById('docDob').value = d.dateOfBirth || '';
            document.getElementById('docPhone').value = d.phone || '';
            document.getElementById('docEmail').value = d.email || '';
            document.getElementById('docDegree').value = d.degree || '';
            document.getElementById('docWorkplace').value = d.workplace || '';
            
            populateUserDropdown('docUserId', d.userId);

            document.getElementById('docModalTitle').innerText = 'Sửa thông tin Bác sĩ';
            openModal('addDocModal');
        }
    }

    async function saveDoc() {
        const id = document.getElementById('docId').value;
        const userIdStr = document.getElementById('docUserId').value;
        const data = {
            code: document.getElementById('docCode').value, 
            fullname: document.getElementById('docName').value,
            dateOfBirth: document.getElementById('docDob').value || null, 
            phone: document.getElementById('docPhone').value,
            email: document.getElementById('docEmail').value, 
            degree: document.getElementById('docDegree').value,
            workplace: document.getElementById('docWorkplace').value, 
            userId: userIdStr ? parseInt(userIdStr) : null
        };
        
        let url = '/api/doctors'; 
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
            alert(id ? "Cập nhật hồ sơ thành công!" : "Thêm mới bác sĩ thành công!");
            closeModal('addDocModal'); 
            loadDocs(); 
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    function openAssignModal(id, name, currentUserId) {
        document.getElementById('assignDocId').value = id;
        document.getElementById('assignDocName').innerText = name;
        populateUserDropdown('assignUserId', currentUserId);
        openModal('assignModal');
    }

    async function submitAssignUser() {
        const id = document.getElementById('assignDocId').value;
        const userIdStr = document.getElementById('assignUserId').value;
        
        const res = await fetch('/api/doctors/' + id + '/assign-user', { 
            method: 'PUT', 
            headers: {'Content-Type': 'application/json'}, 
            body: JSON.stringify({userId: userIdStr ? parseInt(userIdStr) : null}) 
        });
        
        if (res.ok) { 
            alert("Gán tài khoản thành công!");
            closeModal('assignModal'); 
            loadDocs(); 
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function toggleDocStatus(id, actionName) {
        if(confirm(`Bạn có chắc chắn muốn ${actionName.toLowerCase()} bác sĩ này không?`)) {
            let endpoint = actionName === 'Ngưng hoạt động' ? '/deactivate' : '/activate';
            const res = await fetch('/api/doctors/' + id + endpoint, { 
                method: 'PUT', 
                headers: {'Content-Type': 'application/json'} 
            });
            if(res.ok) {
                loadDocs();
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
            loadDocs();
        }
    }
  </script>
</body>
</html>
"""

# Find the end of </nav>\n  </aside>
idx = content.find('</aside>')
if idx != -1:
    idx += len('</aside>')
    new_html = content[:idx] + new_main
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(new_html)
    print("Successfully patched doctors.html")
else:
    print("Could not find </aside>")
