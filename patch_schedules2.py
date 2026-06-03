import sys
import re

file_path = r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix removeDoc and approveProposal
content = content.replace("removeDoc(${s.id})", "removeDoc(${s.id}, ${s.isReceptionist})")
content = content.replace("approveProposal(${s.id})", "approveProposal(${s.id}, ${s.isReceptionist})")

content = content.replace("async function removeDoc(id) {", "async function removeDoc(id, isReceptionist) {")
content = content.replace("fetch(`/api/schedules/${id}`", "fetch(isReceptionist ? `/api/schedules/receptionists/${id}` : `/api/schedules/${id}`")

content = content.replace("async function approveProposal(id) {", "async function approveProposal(id, isReceptionist) {")
content = content.replace("fetch(`/api/schedules/${id}/approve`", "fetch(isReceptionist ? `/api/schedules/receptionists/${id}/approve` : `/api/schedules/${id}/approve`")

# Also, toggleShiftAdmin and toggleShiftDoctor need to be replaced with the unified toggle cell logic
toggle_logic = """
    async function toggleShiftAdmin(date, shiftType) {
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

    async function toggleShiftDoctor(date, shiftType) {
        const isRec = currentRoleLower.includes('le tan');
        const apiUrl = isRec ? '/api/schedules/receptionists/toggle-self' : '/api/schedules/toggle-self';
        const bodyData = { date, shiftType };
        if (isRec) bodyData.userId = currentUser.id;
        else bodyData.doctorId = currentUser.id;
        
        const res = await fetch(apiUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-Role': encodeURIComponent(currentUser.role) },
            body: JSON.stringify(bodyData)
        });
        if (res.ok) { loadData(); }
        else { const err = await res.json(); alert(err.message); }
    }
"""

content = re.sub(r'async function toggleShiftAdmin.*?\}\s*async function toggleShiftDoctor.*?\}\s*async function approveProposal', toggle_logic + '\n\n    async function approveProposal', content, flags=re.DOTALL)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied.")
