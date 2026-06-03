import os
import glob

# Paths to all HTML files in static directory
html_files = glob.glob(r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html")

link_to_add = '      <a href="schedules.html" class="nav-item admin-only">Đăng ký lịch trực</a>\n'

for filepath in html_files:
    if filepath.endswith("schedules.html"):
        continue # skip the one we already made correctly
    if filepath.endswith("login.html") or filepath.endswith("sidebar.html"):
        continue

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find where to insert it. Usually after holidays.html
    # Look for: <a href="holidays.html" class="nav-item admin-only">Thiết lập ngày nghỉ</a>
    target = 'href="holidays.html"'
    if target in content:
        # Check if already added
        if 'href="schedules.html"' not in content:
            # We want to insert after the end of the <a> tag for holidays.html
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
