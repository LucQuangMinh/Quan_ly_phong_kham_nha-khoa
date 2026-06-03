import os
import glob
import re

html_files = glob.glob(r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html')

for fpath in html_files:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find the line: 'admin': ['users', 'doctors', 'services', 'holidays', 'shifts'],
    # We want to replace it with: 'admin': ['users', 'doctors', 'schedules', 'services', 'holidays', 'shifts'],
    
    # Use regex to be safe about spacing
    content = re.sub(
        r"('admin'\s*:\s*\[\s*'users'\s*,\s*'doctors'\s*,\s*)('services')",
        r"\1'schedules', \2",
        content
    )

    with open(fpath, 'w', encoding='utf-8') as f:
        f.write(content)

print(f"Restored 'schedules' for admin in {len(html_files)} html files.")
