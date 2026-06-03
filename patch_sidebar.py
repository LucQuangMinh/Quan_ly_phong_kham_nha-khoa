import os
import re

files = [
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\users.html",
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\doctors.html",
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\services.html",
    r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\holidays.html"
]

# 1. Get the aside block from users.html
with open(files[0], 'r', encoding='utf-8') as f:
    users_html = f.read()

start_aside = users_html.find('<aside class="sidebar"')
end_aside = users_html.find('</aside>') + len('</aside>')
aside_block = users_html[start_aside:end_aside]

# 2. Check if holidays is already in there to avoid duplication
if 'href="holidays.html"' not in aside_block:
    new_nav_item = """
      <a href="holidays.html" class="nav-item">
        <span class="icon" aria-hidden="true">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </span>
        Thiết lập ngày nghỉ
      </a>
    """
    # Insert right before </nav>
    nav_end = aside_block.find('</nav>')
    new_aside_block = aside_block[:nav_end] + new_nav_item + aside_block[nav_end:]
else:
    new_aside_block = aside_block

# 3. Patch all files
for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove active class from new_aside_block to make it clean
    clean_aside = new_aside_block.replace('class="nav-item active"', 'class="nav-item"')
    
    # Add active class to the current file's nav item
    filename = os.path.basename(file_path)
    clean_aside = clean_aside.replace(f'href="{filename}" class="nav-item"', f'href="{filename}" class="nav-item active"')
    
    if '<div id="sidebar-container"></div>' in content:
        content = content.replace('<div id="sidebar-container"></div>', clean_aside)
    else:
        s = content.find('<aside class="sidebar"')
        e = content.find('</aside>') + len('</aside>')
        if s != -1 and e != -1:
            content = content[:s] + clean_aside + content[e:]
            
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Successfully patched sidebar in all HTML files.")
