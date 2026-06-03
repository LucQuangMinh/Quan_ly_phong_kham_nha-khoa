import os

files = [
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\tracking.html",
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\schedules.html",
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\appointments.html"
]

for f_path in files:
    with open(f_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove 'tracking' from 'le-tan' array
    content = content.replace(
        "'le-tan': ['appointments', 'tracking', 'schedules'],",
        "'le-tan': ['appointments', 'schedules'],"
    )
    
    with open(f_path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Patch applied to all 3 files.")
