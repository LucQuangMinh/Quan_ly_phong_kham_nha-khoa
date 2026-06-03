const fs = require('fs');
const destDir = 'd:\\\\Đánh giá và kiểm định\\\\demo\\\\demo\\\\src\\\\main\\\\resources\\\\static\\\\';
const baseHtml = fs.readFileSync(destDir + 'users.html', 'utf8');

function create(name, title, mainHtml) {
    const titleParts = baseHtml.split('<title>');
    const titleParts2 = titleParts[1].split('</title>');
    let out = titleParts[0] + '<title>' + title + '</title>' + titleParts2.slice(1).join('</title>');
    const parts = out.split('<main>');
    const parts2 = parts[1].split('</main>');
    out = parts[0] + '<main>\\n' + mainHtml + '\\n</main>' + parts2.slice(1).join('</main>');
    fs.writeFileSync(destDir + name, out, 'utf8');
    console.log('Created ' + name);
}

const docs = `
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Hồ sơ Bác sĩ</strong></div>
      <div class="page-head">
        <h1>Quản lý Bác sĩ</h1>
        <button class="btn btn-primary" onclick="openModal('addDocModal')">Thêm Bác sĩ</button>
      </div>
      <div class="card">
        <div class="table-wrap">
          <table id="docsTable">
            <thead><tr><th>Mã BS</th><th>Họ tên</th><th>Chuyên môn</th><th>Số điện thoại</th><th>Tài khoản User ID</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody></tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="modal-backdrop" id="addDocModal">
      <div class="modal" style="max-width: 500px;">
        <h2 id="docModalTitle">Thêm Bác sĩ mới</h2>
        <input type="hidden" id="docId" />
        <div class="form-grid">
          <div class="field"><label>Mã Bác sĩ</label><input type="text" id="docCode" /></div>
          <div class="field"><label>Họ và tên</label><input type="text" id="docName" /></div>
          <div class="field"><label>Ngày sinh</label><input type="date" id="docDob" /></div>
          <div class="field"><label>Số điện thoại</label><input type="text" id="docPhone" /></div>
          <div class="field"><label>Email</label><input type="email" id="docEmail" /></div>
          <div class="field"><label>Bằng cấp</label><input type="text" id="docDegree" /></div>
          <div class="field"><label>Nơi công tác</label><input type="text" id="docWorkplace" /></div>
          <div class="field"><label>Gán Tài khoản User ID</label><input type="number" id="docUserId" /></div>
        </div>
        <div class="form-actions">
          <button class="btn btn-ghost" onclick="closeModal('addDocModal')">Hủy</button>
          <button class="btn btn-primary" onclick="saveDoc()">Lưu</button>
        </div>
      </div>
    </div>
    <script>
    async function loadDocs() {
        const res = await fetch('/api/doctors');
        const docs = await res.json();
        const tbody = document.querySelector('#docsTable tbody');
        tbody.innerHTML = '';
        docs.forEach(d => {
            let statusBadge = d.status === 'Hoạt động' ? '<span class="badge badge-le">Hoạt động</span>' : '<span class="badge badge-khac">Ngừng HĐ</span>';
            let lockText = d.status === 'Hoạt động' ? 'Ngừng HĐ' : 'Kích hoạt';
            let lockStatus = d.status === 'Hoạt động' ? 'Ngừng HĐ' : 'Hoạt động';
            tbody.innerHTML += '<tr><td>'+d.code+'</td><td>'+d.fullname+'</td><td>'+(d.degree||'')+'</td><td>'+(d.phone||'')+'</td><td>'+(d.userId?'ID: '+d.userId:'Chưa gán')+'</td><td>'+statusBadge+'</td><td><button class="link-btn" onclick="editDoc('+d.id+')">Sửa</button> | <button class="link-btn danger" onclick="toggleDocStatus('+d.id+', \\''+lockStatus+'\\')">'+lockText+'</button></td></tr>';
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
            code: document.getElementById('docCode').value, fullname: document.getElementById('docName').value,
            dateOfBirth: document.getElementById('docDob').value || null, phone: document.getElementById('docPhone').value,
            email: document.getElementById('docEmail').value, degree: document.getElementById('docDegree').value,
            workplace: document.getElementById('docWorkplace').value, userId: document.getElementById('docUserId').value ? parseInt(document.getElementById('docUserId').value) : null
        };
        let url = '/api/doctors'; let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addDocModal'); loadDocs(); }
    }
    async function toggleDocStatus(id, newStatus) {
        if(confirm('Chuyển trạng thái bác sĩ thành ' + newStatus + '?')) {
            await fetch('/api/doctors/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadDocs();
        }
    }
    window.onload = loadDocs;
    </script>
`;

create('doctors.html', 'Quản lý Bác sĩ', docs);

const svcs = `
    <div class="content-shell">
      <div class="crumb">Hệ thống / <strong>Quản lý Dịch vụ</strong></div>
      <div class="page-head">
        <h1>Danh mục Dịch vụ & Giá</h1>
        <div><button class="btn btn-ghost" onclick="openModal('addPriceModal')">Thiết lập Giá</button> <button class="btn btn-primary" onclick="openModal('addSvcModal')">Thêm Dịch vụ</button></div>
      </div>
      <div class="card">
        <div class="toolbar" style="margin-bottom: 20px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px;">
            <button class="btn btn-primary" id="tabSvc" onclick="switchTab('svc')">Danh mục Dịch vụ</button>
            <button class="btn btn-ghost" id="tabPrice" onclick="switchTab('price')">Bảng giá</button>
        </div>
        <div class="table-wrap" id="svcView">
          <table id="svcTable"><thead><tr><th>Mã DV</th><th>Tên DV</th><th>Đơn vị tính</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody></tbody></table>
        </div>
        <div class="table-wrap" id="priceView" style="display: none;">
          <table id="priceTable"><thead><tr><th>Tên Dịch vụ</th><th>Đơn giá (VNĐ)</th><th>Ngày áp dụng</th><th>Ngày kết thúc</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody></tbody></table>
        </div>
      </div>
    </div>
    <div class="modal-backdrop" id="addSvcModal">
      <div class="modal">
        <h2 id="svcModalTitle">Thêm Dịch vụ mới</h2>
        <input type="hidden" id="svcId" />
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field"><label>Mã Dịch vụ</label><input type="text" id="svcCode" /></div>
          <div class="field"><label>Tên Dịch vụ</label><input type="text" id="svcName" /></div>
          <div class="field"><label>Đơn vị tính</label><input type="text" id="svcUnit" /></div>
        </div>
        <div class="form-actions"><button class="btn btn-ghost" onclick="closeModal('addSvcModal')">Hủy</button> <button class="btn btn-primary" onclick="saveSvc()">Lưu</button></div>
      </div>
    </div>
    <div class="modal-backdrop" id="addPriceModal">
      <div class="modal">
        <h2 id="priceModalTitle">Thiết lập Giá</h2>
        <input type="hidden" id="priceId" />
        <div class="form-grid" style="grid-template-columns: 1fr;">
          <div class="field"><label>Dịch vụ</label><select id="priceSvcId"></select></div>
          <div class="field"><label>Đơn giá (VNĐ)</label><input type="number" id="priceValue" /></div>
          <div class="field"><label>Ngày bắt đầu</label><input type="date" id="priceStart" /></div>
          <div class="field"><label>Ngày kết thúc</label><input type="date" id="priceEnd" /></div>
        </div>
        <div class="form-actions"><button class="btn btn-ghost" onclick="closeModal('addPriceModal')">Hủy</button> <button class="btn btn-primary" onclick="savePrice()">Lưu</button></div>
      </div>
    </div>
    <script>
    let allServices = [];
    async function loadServices() {
        const res = await fetch('/api/services');
        allServices = await res.json();
        const tbody = document.querySelector('#svcTable tbody'); tbody.innerHTML = '';
        const select = document.getElementById('priceSvcId'); select.innerHTML = '';
        allServices.forEach(s => {
            select.innerHTML += '<option value="'+s.id+'">'+s.serviceName+'</option>';
            let statusBadge = s.status === 'Áp dụng' ? '<span class="badge badge-le">Áp dụng</span>' : '<span class="badge badge-khac">Ngừng</span>';
            let lockText = s.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
            let lockStatus = s.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
            tbody.innerHTML += '<tr><td>'+s.serviceCode+'</td><td>'+s.serviceName+'</td><td>'+s.unit+'</td><td>'+statusBadge+'</td><td><button class="link-btn" onclick="editSvc('+s.id+')">Sửa</button> | <button class="link-btn danger" onclick="toggleSvcStatus('+s.id+', \\''+lockStatus+'\\')">'+lockText+'</button></td></tr>';
        });
        loadAllPrices();
    }
    async function loadAllPrices() {
        const tbody = document.querySelector('#priceTable tbody'); tbody.innerHTML = '';
        for (let s of allServices) {
            const res = await fetch('/api/services/' + s.id + '/prices');
            const prices = await res.json();
            prices.forEach(p => {
                let statusBadge = p.status === 'Áp dụng' ? '<span class="badge badge-le">Áp dụng</span>' : '<span class="badge badge-khac">Ngừng</span>';
                let lockText = p.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
                let lockStatus = p.status === 'Áp dụng' ? 'Ngừng' : 'Áp dụng';
                tbody.innerHTML += '<tr><td><strong>'+s.serviceName+'</strong></td><td>'+new Intl.NumberFormat('vi-VN').format(p.price)+'</td><td>'+p.startDate+'</td><td>'+(p.endDate || '-')+'</td><td>'+statusBadge+'</td><td><button class="link-btn" onclick="editPrice('+p.id+', '+s.id+')">Sửa</button> | <button class="link-btn danger" onclick="togglePriceStatus('+p.id+', \\''+lockStatus+'\\')">'+lockText+'</button></td></tr>';
            });
        }
    }
    function switchTab(tab) {
        if(tab === 'svc') {
            document.getElementById('svcView').style.display = 'block'; document.getElementById('priceView').style.display = 'none';
            document.getElementById('tabSvc').className = 'btn btn-primary'; document.getElementById('tabPrice').className = 'btn btn-ghost';
        } else {
            document.getElementById('svcView').style.display = 'none'; document.getElementById('priceView').style.display = 'block';
            document.getElementById('tabSvc').className = 'btn btn-ghost'; document.getElementById('tabPrice').className = 'btn btn-primary';
        }
    }
    function openModal(id) { document.getElementById(id).classList.add('is-open'); }
    function closeModal(id) { document.getElementById(id).classList.remove('is-open'); if(id==='addSvcModal') { document.getElementById('svcId').value = ''; } if(id==='addPriceModal') { document.getElementById('priceId').value = ''; } }
    async function editSvc(id) {
        const s = allServices.find(x => x.id === id);
        if(s) {
            document.getElementById('svcId').value = s.id; document.getElementById('svcCode').value = s.serviceCode;
            document.getElementById('svcName').value = s.serviceName; document.getElementById('svcUnit').value = s.unit;
            document.getElementById('svcModalTitle').innerText = 'Sửa Dịch vụ'; openModal('addSvcModal');
        }
    }
    async function saveSvc() {
        const id = document.getElementById('svcId').value;
        const data = { serviceCode: document.getElementById('svcCode').value, serviceName: document.getElementById('svcName').value, unit: document.getElementById('svcUnit').value };
        let url = '/api/services'; let method = 'POST';
        if (id) { url += '/' + id; method = 'PUT'; }
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addSvcModal'); loadServices(); }
    }
    async function toggleSvcStatus(id, newStatus) {
        if(confirm('Chuyển trạng thái dịch vụ thành ' + newStatus + '?')) {
            await fetch('/api/services/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadServices();
        }
    }
    async function editPrice(priceId, serviceId) {
        const res = await fetch('/api/services/' + serviceId + '/prices'); const prices = await res.json();
        const p = prices.find(x => x.id === priceId);
        if(p) {
            document.getElementById('priceId').value = p.id; document.getElementById('priceSvcId').value = serviceId;
            document.getElementById('priceValue').value = p.price; document.getElementById('priceStart').value = p.startDate;
            document.getElementById('priceEnd').value = p.endDate || ''; document.getElementById('priceModalTitle').innerText = 'Sửa Giá';
            openModal('addPriceModal');
        }
    }
    async function savePrice() {
        const id = document.getElementById('priceId').value; const svcId = document.getElementById('priceSvcId').value;
        const data = { price: document.getElementById('priceValue').value, startDate: document.getElementById('priceStart').value, endDate: document.getElementById('priceEnd').value || null };
        let url = '/api/services/' + svcId + '/prices'; let method = 'POST';
        if (id) { url = '/api/services/prices/' + id; method = 'PUT'; }
        const res = await fetch(url, { method: method, headers: {'Content-Type': 'application/json'}, body: JSON.stringify(data) });
        if (res.ok) { closeModal('addPriceModal'); loadServices(); }
    }
    async function togglePriceStatus(id, newStatus) {
        if(confirm('Chuyển trạng thái giá thành ' + newStatus + '?')) {
            await fetch('/api/services/prices/' + id + '/status', { method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({status: newStatus}) });
            loadServices();
        }
    }
    window.onload = loadServices;
    </script>
`;

create('services.html', 'Danh mục Dịch vụ', svcs);
