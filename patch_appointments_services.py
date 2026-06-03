import re

file_path = r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\appointments.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add table header
content = content.replace('<th>Ca khám</th>', '<th>Ca khám</th>\n                        <th>Dịch vụ</th>')

# 2. Add table cell
content = content.replace('<td>${a.shiftType}</td>', '<td>${a.shiftType}</td>\n                    <td>${a.serviceName || ""}</td>')

# 3. Add to modal UI
modal_injection = """        <div style="display: flex; gap: 16px;">
            <div class="form-group" style="flex: 1;">
                <label>Nhóm dịch vụ</label>
                <select id="serviceCategory" class="form-control" onchange="handleCategoryChange()">
                    <option value="">-- Chọn nhóm --</option>
                </select>
            </div>
            <div class="form-group" style="flex: 1;">
                <label>Dịch vụ</label>
                <select id="serviceId" class="form-control" onchange="handleServiceChange()">
                    <option value="">-- Chọn dịch vụ --</option>
                </select>
            </div>
        </div>
        <div id="priceQuote" style="grid-column: span 2; display: none; color: #dc2626; font-weight: 600; background: #fee2e2; padding: 10px; border-radius: 6px; text-align: center; border: 1px solid #fca5a5;">
            Giá tham khảo: <span id="quotedPriceText">0</span> VNĐ
        </div>
        
        <div class="form-group" id="statusGroup\""""

content = content.replace('<div class="form-group" id="statusGroup"', modal_injection)

# 4. Add data variables
content = content.replace('let doctorSchedules = [];', 'let doctorSchedules = [];\n    let allServices = [];')

# 5. Add to loadData
fetch_services = """
        try {
            const resS = await fetch('/api/services', { headers });
            allServices = await resS.json();
        } catch (e) { console.error(e); }
        
        try {"""
content = content.replace('try {\n            const resH', fetch_services + '\n            const resH')

# 6. Add JS logic functions
js_logic = """
    function populateCategories() {
        const catSet = new Set(allServices.map(s => s.category));
        const catSel = document.getElementById('serviceCategory');
        catSel.innerHTML = '<option value="">-- Chọn nhóm --</option>';
        catSet.forEach(c => {
            if(c) catSel.innerHTML += `<option value="${c}">${c}</option>`;
        });
    }

    function handleCategoryChange() {
        const cat = document.getElementById('serviceCategory').value;
        const srvSel = document.getElementById('serviceId');
        srvSel.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
        document.getElementById('priceQuote').style.display = 'none';
        
        if(cat) {
            allServices.filter(s => s.category === cat && s.status === 'Áp dụng').forEach(s => {
                srvSel.innerHTML += `<option value="${s.id}">${s.name}</option>`;
            });
        }
    }

    function handleServiceChange() {
        const srvId = document.getElementById('serviceId').value;
        const quoteDiv = document.getElementById('priceQuote');
        if(srvId) {
            const srv = allServices.find(s => s.id == srvId);
            if(srv && srv.price != null) {
                document.getElementById('quotedPriceText').innerText = srv.price.toLocaleString('vi-VN');
                quoteDiv.style.display = 'block';
            } else {
                quoteDiv.style.display = 'none';
            }
        } else {
            quoteDiv.style.display = 'none';
        }
    }

    function openAddModal() {"""

content = content.replace('function openAddModal() {', js_logic)

# 7. Update openAddModal
open_add_reset = """        document.getElementById('note').value = '';
        document.getElementById('selectedInfo').innerText = 'Vui lòng chọn ca khám trên lịch';
        
        populateCategories();
        document.getElementById('serviceCategory').value = '';
        document.getElementById('serviceId').innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
        document.getElementById('priceQuote').style.display = 'none';
"""
content = content.replace("        document.getElementById('note').value = '';\n        document.getElementById('selectedInfo').innerText = 'Vui lòng chọn ca khám trên lịch';", open_add_reset)

# 8. Update openEditModal
open_edit_reset = """        document.getElementById('note').value = a.note || '';
        
        populateCategories();
        if (a.serviceId) {
            const srv = allServices.find(s => s.id == a.serviceId);
            if (srv) {
                document.getElementById('serviceCategory').value = srv.category;
                handleCategoryChange();
                document.getElementById('serviceId').value = a.serviceId;
                if (a.quotedPrice != null) {
                    document.getElementById('quotedPriceText').innerText = a.quotedPrice.toLocaleString('vi-VN');
                    document.getElementById('priceQuote').style.display = 'block';
                }
            }
        } else {
            document.getElementById('serviceCategory').value = '';
            document.getElementById('serviceId').innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
            document.getElementById('priceQuote').style.display = 'none';
        }
"""
content = content.replace("        document.getElementById('note').value = a.note || '';", open_edit_reset)

# 9. Update saveAppointment
save_obj = """            examinationDate: document.getElementById('selectedDate').value,
            shiftType: document.getElementById('selectedShift').value,
            note: document.getElementById('note').value
        };
        
        const srvId = document.getElementById('serviceId').value;
        if (srvId) {
            const srv = allServices.find(s => s.id == srvId);
            if (srv) {
                data.serviceId = srv.id;
                data.serviceName = srv.name;
                data.quotedPrice = srv.price;
            }
        }"""
content = content.replace("            shiftType: document.getElementById('selectedShift').value,\n            note: document.getElementById('note').value\n        };", save_obj)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied successfully.")
