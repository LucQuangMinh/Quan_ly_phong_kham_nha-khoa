import sys
import re

file_path = r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update title for non-admins
content = content.replace(
    "document.getElementById('pageTitle').innerText = 'Đăng ký lịch trực cá nhân';",
    "document.getElementById('pageTitle').innerText = `Đăng ký lịch trực cá nhân (${currentUser.fullname})`;"
)

# 2. Update HTML docPane
html_replace = """          <!-- Danh sách bác sĩ (Chỉ Admin) -->
          <div class="doctor-pane admin-only" id="doctorPane">
            <div class="doc-pane-header" style="background: #e0f2fe; color: #0369a1;">
              <span>Danh sách Bác sĩ</span>
              <label style="font-size: 0.8125rem; font-weight: normal; cursor: pointer;">
                <input type="checkbox" id="selectAllDocs" onchange="toggleAllDocs()" /> Chọn hết
              </label>
            </div>
            <div class="doc-list" id="doctorList" style="max-height: 30vh; overflow-y: auto;">
              <!-- Rendered by JS -->
            </div>
            
            <div class="doc-pane-header" style="background: #fce7f3; color: #be185d; border-top: 1px solid #cbd5e1;">
              <span>Danh sách Lễ tân</span>
              <label style="font-size: 0.8125rem; font-weight: normal; cursor: pointer;">
                <input type="checkbox" id="selectAllRecs" onchange="toggleAllRecs()" /> Chọn hết
              </label>
            </div>
            <div class="doc-list" id="receptionistList" style="max-height: 30vh; overflow-y: auto;">
              <!-- Rendered by JS -->
            </div>
          </div>"""
content = re.sub(r'<!-- Danh sách bác sĩ \(Chỉ Admin\)[^\n]*.*?</div>\s*</div>\s*</div>', html_replace + '\n        </div>', content, flags=re.DOTALL)


# 3. Add global var allReceptionists
content = content.replace("let allDoctors = [];", "let allDoctors = [];\n    let allReceptionists = [];")

# 4. Fetch Receptionists & their schedules
fetch_logic = """
            if (isAdmin) {
                const checkedIds = getSelectedDoctorIds(); // Giữ lại trạng thái checkbox cũ
                const checkedRecIds = getSelectedRecIds();
                
                const dRes = await fetch('/api/doctors', { headers });
                if (!dRes.ok) throw new Error("API /api/doctors failed with status " + dRes.status);
                allDoctors = await dRes.json();
                renderDoctorList();
                
                const uRes = await fetch('/api/users', { headers });
                if (uRes.ok) {
                    const users = await uRes.json();
                    allReceptionists = users.filter(u => u.role === 'Lễ tân');
                    renderReceptionistList();
                }
                
                // Phục hồi lại trạng thái checkbox
                document.querySelectorAll('.doc-checkbox').forEach(cb => {
                    if (checkedIds.includes(parseInt(cb.value))) cb.checked = true;
                });
                document.querySelectorAll('.rec-checkbox').forEach(cb => {
                    if (checkedRecIds.includes(parseInt(cb.value))) cb.checked = true;
                });
            }
"""
content = re.sub(r'if \(isAdmin\) \{.*?// Phục hồi lại trạng thái checkbox.*?\}\s*\}\s*renderCalendar\(\);', fetch_logic + '            \n            renderCalendar();', content, flags=re.DOTALL)

# Add receptionist schedule merging
merge_logic = """
            const sRes = await fetch(url, { headers });
            if (!sRes.ok) throw new Error("API /api/schedules failed with status " + sRes.status);
            allSchedules = await sRes.json();
            
            try {
                let recUrl = `/api/schedules/receptionists?start=${getLocalIsoDate(gridStartDate)}&end=${getLocalIsoDate(gridEndDate)}&role=${encodeURIComponent(currentUser.role)}&userId=${currentUser.id || ''}`;
                const recRes = await fetch(recUrl, { headers });
                if (recRes.ok) {
                    const recData = await recRes.json();
                    recData.forEach(r => {
                        r.isReceptionist = true;
                        r.doctor = { fullname: r.receptionist ? r.receptionist.fullname : 'N/A' };
                    });
                    allSchedules = allSchedules.concat(recData);
                }
            } catch(e) { console.error("Lỗi lấy lịch lễ tân", e); }
"""
content = content.replace("""const sRes = await fetch(url, { headers });
            if (!sRes.ok) throw new Error("API /api/schedules failed with status " + sRes.status);
            allSchedules = await sRes.json();""", merge_logic)

# 5. Functions for Receptionists
func_add = """
    function getSelectedRecIds() {
        const checkboxes = document.querySelectorAll('.rec-checkbox:checked');
        return Array.from(checkboxes).map(cb => parseInt(cb.value));
    }
    function toggleAllRecs() {
        const isChecked = document.getElementById('selectAllRecs').checked;
        document.querySelectorAll('.rec-checkbox').forEach(cb => cb.checked = isChecked);
    }
    function renderReceptionistList() {
        const list = document.getElementById('receptionistList');
        list.innerHTML = '';
        allReceptionists.forEach(rec => {
            list.innerHTML += `
                <label class="doc-item">
                    <input type="checkbox" class="rec-checkbox" value="${rec.id}" />
                    <span class="doc-name" style="color: #be185d;">${rec.fullname}</span>
                </label>
            `;
        });
    }
"""
content = content.replace("function renderDoctorList() {", func_add + "\n    function renderDoctorList() {")

# 6. Delete shift logic
content = content.replace(
    "`<span class=\"remove-btn\" onclick=\"removeDoctor(${s.id}, event)\">×</span>` : '';",
    "`<span class=\"remove-btn\" onclick=\"removeDoctor(${s.id}, event, ${s.isReceptionist})\">×</span>` : '';"
)

content = content.replace("async function removeDoctor(id, e) {", "async function removeDoctor(id, e, isReceptionist) {")
content = content.replace("fetch(`/api/schedules/${id}`", "fetch(isReceptionist ? `/api/schedules/receptionists/${id}` : `/api/schedules/${id}`")
content = content.replace("fetch(`/api/schedules/${id}/approve`", "fetch(isReceptionist ? `/api/schedules/receptionists/${id}/approve` : `/api/schedules/${id}/approve`")
content = content.replace("async function approveProposal(id, status) {", "async function approveProposal(id, status, isReceptionist) {")
content = content.replace("onclick=\"approveProposal(${s.id}, '${s.status}')\"", "onclick=\"approveProposal(${s.id}, '${s.status}', ${s.isReceptionist})\"")

# 7. toggle cell logic
toggle_cell = """
            if (currentRoleLower.includes('bac si') || currentRoleLower.includes('le tan')) {
                // Tự đăng ký
                const isRec = currentRoleLower.includes('le tan');
                const apiUrl = isRec ? '/api/schedules/receptionists/toggle-self' : '/api/schedules/toggle-self';
                const bodyData = { date, shiftType };
                if (isRec) bodyData.userId = currentUser.id;
                else bodyData.doctorId = currentUser.id; // actually bac si needs doctorId but backend toggles by doctorId or userId based on endpoint? Wait, toggleDoctorSelf uses doctorId, toggleReceptionistSelf uses userId.
                
                const res = await fetch(apiUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
                    body: JSON.stringify(bodyData)
                });
                if (res.ok) { loadData(); }
                else { const err = await res.json(); alert(err.message); }
            } else {
                // Admin xếp lịch cho list
                const docIds = getSelectedDoctorIds();
                const recIds = getSelectedRecIds();
                if (docIds.length === 0 && recIds.length === 0) {
                    alert('Vui lòng tích chọn ít nhất một Bác sĩ hoặc Lễ tân từ danh sách bên phải!');
                    return;
                }
                if (docIds.length > 0) {
                    await fetch('/api/schedules/toggle-bulk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
                        body: JSON.stringify({ doctorIds: docIds, date, shiftType })
                    });
                }
                if (recIds.length > 0) {
                    await fetch('/api/schedules/receptionists/toggle-bulk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
                        body: JSON.stringify({ userIds: recIds, date, shiftType })
                    });
                }
                loadData();
            }
"""
content = re.sub(r'if \(currentRoleLower\.includes\(\'bac si\'\)\) \{.*?else \{.*?const docIds.*?loadData\(\);\s*\}', toggle_cell, content, flags=re.DOTALL)

# 8. Week logic
week_logic = """
            const docIds = getSelectedDoctorIds();
            const recIds = getSelectedRecIds();
            if (docIds.length === 0 && recIds.length === 0) {
                alert('Vui lòng tích chọn ít nhất một Bác sĩ hoặc Lễ tân từ danh sách bên phải!');
                return;
            }
            if (!confirm(`Phân công CẢ TUẦN ca ${shiftType} cho nhân sự đã chọn?`)) return;
            
            if (docIds.length > 0) {
                await fetch('/api/schedules/toggle-week', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
                    body: JSON.stringify({ doctorIds: docIds, startDate: startOfWeek, shiftType })
                });
            }
            if (recIds.length > 0) {
                await fetch('/api/schedules/receptionists/toggle-week', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
                    body: JSON.stringify({ userIds: recIds, startDate: startOfWeek, shiftType })
                });
            }
            loadData();
"""
content = re.sub(r'const docIds = getSelectedDoctorIds\(\);.*?if \(!confirm.*?loadData\(\);', week_logic, content, flags=re.DOTALL)


with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied.")
