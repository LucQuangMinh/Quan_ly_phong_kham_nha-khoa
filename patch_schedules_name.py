import os

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace doc.name
content = content.replace("${doc.name}", "${doc.fullname}")
content = content.replace("${s.doctor.name}", "${s.doctor.fullname}")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched schedules.html successfully!")
