import os
import glob
import re

files = glob.glob(r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html')

menu_items = [
    ('dashboard.html', 'Bảng điều khiển', 'staff-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>'),
    ('appointments.html', 'Đăng ký khám', 'everyone', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>'),
    ('tracking.html', 'Theo dõi lịch khám', 'everyone', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M13.8 12H3"/></svg>'),
    ('schedules.html', 'Lịch trực bác sĩ', 'staff-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>'),
    ('doctors.html', 'Hồ sơ Bác sĩ', 'admin-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>'),
    ('users.html', 'Quản lý Người dùng', 'admin-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>'),
    ('services.html', 'Danh mục Dịch vụ', 'admin-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>'),
    ('holidays.html', 'Thiết lập ngày nghỉ', 'admin-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>'),
    ('shifts.html', 'Thiết lập ca làm việc', 'admin-only', '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>')
]

for filepath in files:
    filename = os.path.basename(filepath)
    if filename in ['login.html', 'index.html', 'dashboard.html']:
        continue
        
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Generate the nav HTML
    nav_html = '<nav class="nav" id="sidebarNav" aria-label="Menu quản trị">\n'
    for link, name, role_class, icon in menu_items:
        active = ' active' if link == filename else ''
        nav_html += f'      <a href="{link}" class="nav-item {role_class}{active}">\n'
        nav_html += f'        <span class="icon" aria-hidden="true">{icon}</span>\n'
        nav_html += f'        <span class="menu-text">{name}</span>\n'
        nav_html += f'      </a>\n'
        
    nav_html += '      <div style="margin-top: 30px; border-top: 1px solid var(--sidebar-border); padding-top: 10px;"></div>\n'
    nav_html += '      <a href="#" class="nav-item" onclick="logout(event)" style="color: #dc2626;">\n'
    nav_html += '        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg></span>\n'
    nav_html += '        <span class="menu-text">Đăng xuất</span>\n'
    nav_html += '      </a>\n'
    nav_html += '    </nav>'

    # Replace the existing <nav ...> ... </nav> block
    new_content = re.sub(r'<nav.*?class="nav".*?>.*?</nav>', lambda m: nav_html, content, flags=re.DOTALL)
        
    if new_content != content:
        # Also need to inject JS logic to hide elements based on roles
        js_logic = '''
            // Lọc menu theo role
            const currentRoleLower = (currentUser.role || 'Admin').toLowerCase().normalize('NFD').replace(/[\\u0300-\\u036f]/g, '').replace(/đ/g, 'd');
            if (currentRoleLower.includes('benh nhan')) {
                document.querySelectorAll('.admin-only, .staff-only').forEach(el => el.style.display = 'none');
                document.querySelectorAll('.menu-text').forEach(el => {
                    if (el.textContent.includes('Đăng ký khám')) el.textContent = 'Lịch khám của tôi';
                    if (el.textContent.includes('Theo dõi lịch khám')) el.textContent = 'Theo dõi lịch khám của tôi';
                });
            } else if (currentRoleLower.includes('bac si') || currentRoleLower.includes('le tan')) {
                document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
            }
        '''
        
        # We find the place to put js_logic. Inside checkLogin() or initUI() or window.onload or just script
        if 'document.querySelectorAll(\'.admin-only\').forEach(el => el.style.display = \'none\');' in new_content:
            new_content = new_content.replace('document.querySelectorAll(\'.admin-only\').forEach(el => el.style.display = \'none\');', js_logic)
        elif 'function initUI()' in new_content:
            new_content = new_content.replace('function initUI() {', 'function initUI() {' + js_logic)
        elif 'function checkLogin()' in new_content:
            new_content = new_content.replace('function checkLogin() {', 'function checkLogin() {' + js_logic)
        elif 'nav.innerHTML = navHtml;' in new_content:
            # appointments.html specific
            new_content = re.sub(r'const nav = document.getElementById\(\'sidebarNav\'\);.*?nav\.innerHTML = navHtml;', lambda m: js_logic, new_content, flags=re.DOTALL)
            
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Updated {filename}')
    else:
        print(f'Nav block not found or unchanged in {filename}')
