import os
import glob

html_files = glob.glob(r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html")

link_to_add = '      <a href="shifts.html" class="nav-item admin-only">Thiết lập ca làm việc</a>\n'

for filepath in html_files:
    if filepath.endswith("shifts.html"):
        continue
    if filepath.endswith("login.html") or filepath.endswith("sidebar.html") or filepath.endswith("dashboard.html"):
        continue

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    target = 'href="holidays.html"'
    if target in content:
        if 'href="shifts.html"' not in content:
            parts = content.split('</a>')
            new_content = ""
            for i, part in enumerate(parts):
                new_content += part
                if target in part:
                    new_content += '</a>\n' + link_to_add
                elif i < len(parts) - 1:
                    new_content += '</a>'
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Updated {os.path.basename(filepath)}")
        else:
            print(f"Already updated {os.path.basename(filepath)}")

print("Done patching sidebars.")
