const fs = require('fs');
const path = require('path');

const srcPath = 'd:\\Đánh giá và kiểm định\\UC 2.html';
const destDir = 'd:\\Đánh giá và kiểm định\\demo\\demo\\src\\main\\resources\\static\\';

const content = fs.readFileSync(srcPath, 'utf8');

// Extract head and sidebar
const mainIndex = content.indexOf('<main>');
if (mainIndex === -1) throw new Error("Could not find <main>");

let prefix = content.substring(0, mainIndex);

// Replace active nav item
prefix = prefix.replace(/<button class="nav-item active">/g, '<button class="nav-item">');
prefix = prefix.replace(/<button class="usecase-nav active"/g, '<button class="usecase-nav"');

const suffix = `
</body>
</html>`;

function createPage(filename, title, mainContent, scriptContent) {
    let html = prefix.replace('<title>Thiết lập các ngày nghỉ</title>', `<title>${title}</title>`);
    html += '<main>\n' + mainContent + '\n</main>\n';
    if (scriptContent) {
        html += '<script>\n' + scriptContent + '\n</script>\n';
    }
    html += suffix;
    fs.writeFileSync(path.join(destDir, filename), html, 'utf8');
    console.log(`Created ${filename}`);
}

const usersMain = `
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Người dùng</strong></div>
      
      <div class="page-head">
        <h1>Quản lý Tài khoản & Phân quyền</h1>
        <button class="btn btn-primary" onclick="openModal('addUserModal')">
          <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"></path></svg>
          Thêm Người dùng
        </button>
      </div>

      <div class="card">
        <div class="toolbar">
          <input type="text" id="searchInput" class="search" placeholder="Tìm kiếm theo tên, username, email..." onkeyup="loadUsers()" />
        </div>
        <div class="table-wrap">
          <table id="usersTable">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tài khoản</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Vai trò (Role)</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <!-- Data will be loaded here -->
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Thêm/Sửa User -->
    <div class="modal-backdrop" id="addUserModal">
      <div class="modal">
        <h2 id="modalTitle">Thêm Người dùng mới</h2>
        <input type="hidden" id="userId" />
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field">
            <label>Tên đăng nhập (Username)</label>
            <input type="text" id="username" placeholder="Nhập tên đăng nhập" />
          </div>
          <div class="field">
            <label>Mật khẩu</label>
            <input type="password" id="password" placeholder="Nhập mật khẩu" />
          </div>
          <div class="field">
            <label>Họ và tên</label>
            <input type="text" id="fullname" placeholder="Nhập họ tên" />
          </div>
          <div class="field">
            <label>Email</label>
            <input type="email" id="email" placeholder="Nhập email" />
          </div>
          <div class="field">
            <label>Số điện thoại</label>
            <input type="text" id="phone" placeholder="Nhập số điện thoại" />
          </div>
          <div class="field">
            <label>Vai trò</label>
            <select id="role">
              <option value="Admin">Admin</option>
              <option value="Quản lý">Quản lý</option>
              <option value="Bác sĩ">Bác sĩ</option>
              <option value="Lễ tân">Lễ tân</option>
              <option value="Bệnh nhân">Bệnh nhân</option>
            </select>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addUserModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveUser()">Lưu thông tin</button>
        </div>
      </div>
    </div>
`;

const usersScript = `
    async function loadUsers() {
        const res = await fetch('/api/users');
        const users = await res.json();
        const search = document.getElementById('searchInput').value.toLowerCase();
        
        const tbody = document.querySelector('#usersTable tbody');
        tbody.innerHTML = '';
        
        users.filter(u => 
            (u.username && u.username.toLowerCase().includes(search)) || 
            (u.fullname && u.fullname.toLowerCase().includes(search)) || 
            (u.email && u.email.toLowerCase().includes(search))
        ).forEach(u => {
            let statusBadge = u.status === 'Hoạt động' ? '<span class="badge badge-le">Hoạt động</span>' : '<span class="badge badge-khac">Khóa</span>';
            let actions = \`
                <button class="link-btn" onclick="editUser(\${u.id})">Sửa</button>
            \`;
            
            if (u.id !== 1) {
                let lockText = u.status === 'Hoạt động' ? 'Khóa' : 'Mở khóa';
                let lockStatus = u.status === 'Hoạt động' ? 'Khóa' : 'Hoạt động';
                actions += \` | <button class="link-btn danger" onclick="toggleStatus(\${u.id}, '\${lockStatus}')">\${lockText}</button>\`;
            }
            
            tbody.innerHTML += \`
                <tr>
                    <td>\${u.id}</td>
                    <td>\${u.username}</td>
                    <td>\${u.fullname}</td>
                    <td>\${u.email}</td>
                    <td>\${u.phone || ''}</td>
                    <td>\${u.role}</td>
                    <td>\${statusBadge}</td>
                    <td>\${actions}</td>
                </tr>
            \`;
        });
    }

    function openModal(id) {
        document.getElementById(id).classList.add('is-open');
    }

    function closeModal(id) {
        document.getElementById(id).classList.remove('is-open');
        document.getElementById('userId').value = '';
        document.getElementById('username').value = '';
        document.getElementById('password').value = '';
        document.getElementById('password').disabled = false;
        document.getElementById('fullname').value = '';
        document.getElementById('email').value = '';
        document.getElementById('phone').value = '';
        document.getElementById('role').value = 'Bệnh nhân';
        document.getElementById('modalTitle').innerText = 'Thêm Người dùng mới';
    }

    async function editUser(id) {
        const res = await fetch('/api/users');
        const users = await res.json();
        const user = users.find(u => u.id === id);
        if(user) {
            document.getElementById('userId').value = user.id;
            document.getElementById('username').value = user.username;
            document.getElementById('password').value = '********';
            document.getElementById('password').disabled = true; // Don't allow edit password here
            document.getElementById('fullname').value = user.fullname;
            document.getElementById('email').value = user.email;
            document.getElementById('phone').value = user.phone;
            document.getElementById('role').value = user.role;
            document.getElementById('modalTitle').innerText = 'Sửa thông tin Người dùng';
            openModal('addUserModal');
        }
    }

    async function saveUser() {
        const id = document.getElementById('userId').value;
        const data = {
            username: document.getElementById('username').value,
            fullname: document.getElementById('fullname').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            role: document.getElementById('role').value
        };
        
        let url = '/api/users';
        let method = 'POST';
        
        if (id) {
            url += '/' + id;
            method = 'PUT';
        } else {
            data.password = document.getElementById('password').value;
        }
        
        const res = await fetch(url, {
            method: method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });
        
        if (res.ok) {
            closeModal('addUserModal');
            loadUsers();
        } else {
            const err = await res.json();
            alert("Lỗi: " + err.message);
        }
    }

    async function toggleStatus(id, newStatus) {
        if(confirm(\`Bạn có chắc chắn muốn \${newStatus === 'Khóa' ? 'khóa' : 'mở khóa'} tài khoản này?\`)) {
            await fetch('/api/users/' + id + '/status', {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({status: newStatus})
            });
            loadUsers();
        }
    }

    // Auth check logic
    window.onload = function() {
        const user = JSON.parse(localStorage.getItem('user'));
        if(!user || user.role !== 'Admin') {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = '/login.html';
        } else {
            loadUsers();
        }
    }
\`;

createPage('users.html', 'Quản lý Người dùng', usersMain, usersScript);

const doctorsMain = \`
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Hồ sơ Bác sĩ</strong></div>
      
      <div class="page-head">
        <h1>Quản lý Bác sĩ</h1>
        <button class="btn btn-primary" onclick="openModal('addDocModal')">
          Thêm Bác sĩ
        </button>
      </div>

      <div class="card">
        <div class="table-wrap">
          <table id="docsTable">
            <thead>
              <tr>
                <th>Mã BS</th>
                <th>Họ tên</th>
                <th>Chuyên môn</th>
                <th>Số điện thoại</th>
                <th>Tài khoản User ID</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal-backdrop" id="addDocModal">
      <div class="modal" style="max-width: 500px;">
        <h2 id="docModalTitle">Thêm Bác sĩ mới</h2>
        <input type="hidden" id="docId" />
        <div class="form-grid">
          <div class="field">
            <label>Mã Bác sĩ</label>
            <input type="text" id="docCode" placeholder="Ví dụ: BS01" />
          </div>
          <div class="field">
            <label>Họ và tên</label>
            <input type="text" id="docName" />
          </div>
          <div class="field">
            <label>Ngày sinh</label>
            <input type="date" id="docDob" />
          </div>
          <div class="field">
            <label>Số điện thoại</label>
            <input type="text" id="docPhone" />
          </div>
          <div class="field">
            <label>Email</label>
            <input type="email" id="docEmail" />
          </div>
          <div class="field">
            <label>Bằng cấp / Chuyên môn</label>
            <input type="text" id="docDegree" />
          </div>
          <div class="field">
            <label>Nơi công tác</label>
            <input type="text" id="docWorkplace" />
          </div>
          <div class="field">
            <label>Gán Tài khoản User ID (Tùy chọn)</label>
            <input type="number" id="docUserId" placeholder="Nhập ID User" />
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addDocModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveDoc()">Lưu thông tin</button>
        </div>
      </div>
    </div>
\`;

const doctorsScript = \`
    async function loadDocs() {
        const res = await fetch('/api/doctors');
        const docs = await res.json();
        
        const tbody = document.querySelector('#docsTable tbody');
        tbody.innerHTML = '';
        
        docs.forEach(d => {
            let statusBadge = d.status === 'Hoạt động' ? '<span class="badge badge-le">Hoạt động</span>' : '<span class="badge badge-khac">Ngừng HĐ</span>';
            let lockText = d.status === 'Hoạt động' ? 'Ngừng HĐ' : 'Kích hoạt';
            let lockStatus = d.status === 'Hoạt động' ? 'Ngừng HĐ' : 'Hoạt động';
            
            tbody.innerHTML += \\\`
                <tr>
                    <td>\\\${d.code}</td>
                    <td>\\\${d.fullname}</td>
                    <td>\\\${d.degree}</td>
                    <td>\\\${d.phone || ''}</td>
                    <td>\\\${d.userId ? 'ID: ' + d.userId : 'Chưa gán'}</td>
                    <td>\\\${statusBadge}</td>
                    <td>
                        <button class="link-btn" onclick="editDoc(\\\${d.id})">Sửa</button> | 
                        <button class="link-btn danger" onclick="toggleDocStatus(\\\${d.id}, '\\\${lockStatus}')">\\\${lockText}</button>
                    </td>
                </tr>
            \\\`;
        });
    }

    function openModal(id) { document.getElementById(id).classList.add('is-open'); }
    function closeModal(id) { document.getElementById(id).classList.remove('is-open'); document.getElementById('docId').value = ''; }

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
            document.getElementById('docUserId').value = d.userId || '';
            document.getElementById('docModalTitle').innerText = 'Sửa thông tin Bác sĩ';
            openModal('addDocModal');
        }
    }

    async function saveDoc() {
        const id = document.getElementById('docId').value;
        const data = {
            code: document.getElementById('docCode').value,
            fullname: document.getElementById('docName').value,
            dateOfBirth: document.getElementById('docDob').value || null,
            phone: document.getElementById('docPhone').value,
            email: document.getElementById('docEmail').value,
            degree: document.getElementById('docDegree').value,
            workplace: document.getElementById('docWorkplace').value,
            userId: document.getElementById('docUserId').value ? parseInt(document.getElementById('docUserId').value) : null
        };
        
        let url = '/api/doctors';
        let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addDocModal'); loadDocs(); }
    }

    async function toggleDocStatus(id, newStatus) {
        if(confirm(\\\`Chuyển trạng thái bác sĩ thành \\\${newStatus}?\\\`)) {
            await fetch('/api/doctors/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadDocs();
        }
    }

    window.onload = loadDocs;
\`;

createPage('doctors.html', 'Quản lý Hồ sơ Bác sĩ', doctorsMain, doctorsScript);

const servicesMain = \`
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Dịch vụ</strong></div>
      
      <div class="page-head">
        <h1>Danh mục Dịch vụ & Giá</h1>
        <div>
            <button class="btn btn-ghost" onclick="openModal('addPriceModal')">Thiết lập Giá</button>
            <button class="btn btn-primary" onclick="openModal('addSvcModal')">Thêm Dịch vụ</button>
        </div>
      </div>

      <div class="card">
        <div class="toolbar" style="margin-bottom: 20px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px;">
            <button class="btn btn-primary" id="tabSvc" onclick="switchTab('svc')">Danh mục Dịch vụ</button>
            <button class="btn btn-ghost" id="tabPrice" onclick="switchTab('price')">Bảng giá</button>
        </div>
        
        <div class="table-wrap" id="svcView">
          <table id="svcTable">
            <thead>
              <tr>
                <th>Mã Dịch vụ</th>
                <th>Tên Dịch vụ</th>
                <th>Đơn vị tính</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
        
        <div class="table-wrap" id="priceView" style="display: none;">
          <table id="priceTable">
            <thead>
              <tr>
                <th>Tên Dịch vụ</th>
                <th>Đơn giá (VNĐ)</th>
                <th>Ngày áp dụng</th>
                <th>Ngày kết thúc</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal Dịch vụ -->
    <div class="modal-backdrop" id="addSvcModal">
      <div class="modal">
        <h2 id="svcModalTitle">Thêm Dịch vụ mới</h2>
        <input type="hidden" id="svcId" />
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field">
            <label>Mã Dịch vụ</label>
            <input type="text" id="svcCode" placeholder="VD: DV01" />
          </div>
          <div class="field">
            <label>Tên Dịch vụ</label>
            <input type="text" id="svcName" />
          </div>
          <div class="field">
            <label>Đơn vị tính</label>
            <input type="text" id="svcUnit" placeholder="VD: Lần, Răng..." />
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addSvcModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveSvc()">Lưu</button>
        </div>
      </div>
    </div>
    
    <!-- Modal Giá -->
    <div class="modal-backdrop" id="addPriceModal">
      <div class="modal">
        <h2 id="priceModalTitle">Thiết lập Giá</h2>
        <input type="hidden" id="priceId" />
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field">
            <label>Dịch vụ</label>
            <select id="priceSvcId"></select>
          </div>
          <div class="field">
            <label>Đơn giá (VNĐ)</label>
            <input type="number" id="priceValue" />
          </div>
          <div class="field">
            <label>Ngày bắt đầu áp dụng</label>
            <input type="date" id="priceStart" />
          </div>
          <div class="field">
            <label>Ngày kết thúc (Tùy chọn)</label>
            <input type="date" id="priceEnd" />
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addPriceModal')">Hủy</button>
          <button class="btn btn-primary" onclick="savePrice()">Lưu</button>
        </div>
      </div>
    </div>
\`;

const servicesScript = \`
    let allServices = [];

    async function loadServices() {
        const res = await fetch('/api/services');
        allServices = await res.json();
        
        const tbody = document.querySelector('#svcTable tbody');
        tbody.innerHTML = '';
        
        const select = document.getElementById('priceSvcId');
        select.innerHTML = '';
        
        allServices.forEach(s => {
            select.innerHTML += \\\`<option value="\\\${s.id}">\\\${s.serviceName}</option>\\\`;
            let statusBadge = s.status === 'Áp dụng' ? '<span class="badge badge-le">Áp dụng</span>' : '<span class="badge badge-khac">Ngừng</span>';
            let lockText = s.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
            let lockStatus = s.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
            
            tbody.innerHTML += \\\`
                <tr>
                    <td>\\\${s.serviceCode}</td>
                    <td>\\\${s.serviceName}</td>
                    <td>\\\${s.unit}</td>
                    <td>\\\${statusBadge}</td>
                    <td>
                        <button class="link-btn" onclick="editSvc(\\\${s.id})">Sửa</button> | 
                        <button class="link-btn danger" onclick="toggleSvcStatus(\\\${s.id}, '\\\${lockStatus}')">\\\${lockText}</button>
                    </td>
                </tr>
            \\\`;
        });
        
        loadAllPrices();
    }
    
    async function loadAllPrices() {
        const tbody = document.querySelector('#priceTable tbody');
        tbody.innerHTML = '';
        
        for (let s of allServices) {
            const res = await fetch('/api/services/' + s.id + '/prices');
            const prices = await res.json();
            
            prices.forEach(p => {
                let statusBadge = p.status === 'Áp dụng' ? '<span class="badge badge-le">Áp dụng</span>' : '<span class="badge badge-khac">Ngừng</span>';
                let lockText = p.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
                let lockStatus = p.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
                
                tbody.innerHTML += \\\`
                    <tr>
                        <td><strong>\\\${s.serviceName}</strong></td>
                        <td>\\\${new Intl.NumberFormat('vi-VN').format(p.price)}</td>
                        <td>\\\${p.startDate}</td>
                        <td>\\\${p.endDate || '-'}</td>
                        <td>\\\${statusBadge}</td>
                        <td>
                            <button class="link-btn" onclick="editPrice(\\\${p.id}, \\\${s.id})">Sửa</button> | 
                            <button class="link-btn danger" onclick="togglePriceStatus(\\\${p.id}, '\\\${lockStatus}')">\\\${lockText}</button>
                        </td>
                    </tr>
                \\\`;
            });
        }
    }

    function switchTab(tab) {
        if(tab === 'svc') {
            document.getElementById('svcView').style.display = 'block';
            document.getElementById('priceView').style.display = 'none';
            document.getElementById('tabSvc').className = 'btn btn-primary';
            document.getElementById('tabPrice').className = 'btn btn-ghost';
        } else {
            document.getElementById('svcView').style.display = 'none';
            document.getElementById('priceView').style.display = 'block';
            document.getElementById('tabSvc').className = 'btn btn-ghost';
            document.getElementById('tabPrice').className = 'btn btn-primary';
        }
    }

    function openModal(id) { document.getElementById(id).classList.add('is-open'); }
    function closeModal(id) { 
        document.getElementById(id).classList.remove('is-open'); 
        if(id==='addSvcModal') { document.getElementById('svcId').value = ''; }
        if(id==='addPriceModal') { document.getElementById('priceId').value = ''; }
    }

    async function editSvc(id) {
        const s = allServices.find(x => x.id === id);
        if(s) {
            document.getElementById('svcId').value = s.id;
            document.getElementById('svcCode').value = s.serviceCode;
            document.getElementById('svcName').value = s.serviceName;
            document.getElementById('svcUnit').value = s.unit;
            document.getElementById('svcModalTitle').innerText = 'Sửa Dịch vụ';
            openModal('addSvcModal');
        }
    }

    async function saveSvc() {
        const id = document.getElementById('svcId').value;
        const data = {
            serviceCode: document.getElementById('svcCode').value,
            serviceName: document.getElementById('svcName').value,
            unit: document.getElementById('svcUnit').value
        };
        
        let url = '/api/services';
        let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addSvcModal'); loadServices(); }
    }

    async function toggleSvcStatus(id, newStatus) {
        if(confirm(\\\`Chuyển trạng thái dịch vụ thành \\\${newStatus}?\\\`)) {
            await fetch('/api/services/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadServices();
        }
    }
    
    // Price actions
    async function editPrice(priceId, serviceId) {
        const res = await fetch('/api/services/' + serviceId + '/prices');
        const prices = await res.json();
        const p = prices.find(x => x.id === priceId);
        if(p) {
            document.getElementById('priceId').value = p.id;
            document.getElementById('priceSvcId').value = serviceId;
            document.getElementById('priceValue').value = p.price;
            document.getElementById('priceStart').value = p.startDate;
            document.getElementById('priceEnd').value = p.endDate || '';
            document.getElementById('priceModalTitle').innerText = 'Sửa Giá';
            openModal('addPriceModal');
        }
    }
    
    async function savePrice() {
        const id = document.getElementById('priceId').value;
        const svcId = document.getElementById('priceSvcId').value;
        const data = {
            price: document.getElementById('priceValue').value,
            startDate: document.getElementById('priceStart').value,
            endDate: document.getElementById('priceEnd').value || null
        };
        
        let url = '/api/services/' + svcId + '/prices';
        let method = 'POST';
        if (id) { url = '/api/services/prices/' + id; method = 'PUT'; }
        
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addPriceModal'); loadServices(); }
    }

    async function togglePriceStatus(id, newStatus) {
        if(confirm(\\\`Chuyển trạng thái giá thành \\\${newStatus}?\\\`)) {
            await fetch('/api/services/prices/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadServices();
        }
    }

    window.onload = loadServices;
\`;

createPage('services.html', 'Quản lý Dịch vụ', servicesMain, servicesScript);


