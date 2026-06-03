import os

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

old_code = """        if (isAdmin) {
            const dRes = await fetch('/api/doctors');
            allDoctors = await dRes.json();
            renderDoctorList();
        }"""

new_code = """        if (isAdmin) {
            const checkedIds = getSelectedDoctorIds(); // Giữ lại trạng thái checkbox cũ
            
            const dRes = await fetch('/api/doctors');
            allDoctors = await dRes.json();
            renderDoctorList();
            
            // Phục hồi lại trạng thái checkbox
            document.querySelectorAll('.doc-checkbox').forEach(cb => {
                if (checkedIds.includes(parseInt(cb.value))) {
                    cb.checked = true;
                }
            });
        }"""

content = content.replace(old_code, new_code)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched schedules.html with checkbox persistence!")
