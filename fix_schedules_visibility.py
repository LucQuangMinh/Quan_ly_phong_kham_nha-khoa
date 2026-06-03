import os
import glob

files = glob.glob(r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html')
for path in files:
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if 'href="schedules.html"' in line:
            lines[i] = line.replace(' admin-only', '').replace('admin-only ', '').replace('admin-only', '')
    
    new_content = '\n'.join(lines)
    
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Removed admin-only from schedules.html in {os.path.basename(path)}')
