import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

standard_sidebar = """
  <aside class="sidebar" aria-label="Điều hướng chính" id="dynamicSidebar">
    <header class="sidebar-header">
      <h1 id="sidebarTitle">Hệ thống Nha Khoa</h1>
      <p id="sidebarSubtitle">Vai trò</p>
    </header>

    <nav class="nav" id="sidebarNav" aria-label="Menu chính">
      <a href="appointments.html" class="nav-item" data-menu="appointments">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg></span>
        <span class="menu-text">Đăng ký khám</span>
      </a>
      <a href="tracking.html" class="nav-item" data-menu="tracking">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M13.8 12H3"/></svg></span>
        <span class="menu-text">Theo dõi lịch khám</span>
      </a>
      <a href="schedules.html" class="nav-item" data-menu="schedules">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg></span>
        <span class="menu-text">Lịch trực bác sĩ</span>
      </a>
      <a href="doctors.html" class="nav-item" data-menu="doctors">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg></span>
        <span class="menu-text">Hồ sơ Bác sĩ</span>
      </a>
      <a href="users.html" class="nav-item" data-menu="users">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></span>
        <span class="menu-text">Quản lý Người dùng</span>
      </a>
      <a href="services.html" class="nav-item" data-menu="services">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg></span>
        <span class="menu-text">Danh mục Dịch vụ</span>
      </a>
      <a href="prices.html" class="nav-item" data-menu="prices">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg></span>
        <span class="menu-text">Thiết lập giá dịch vụ</span>
      </a>
      <a href="holidays.html" class="nav-item" data-menu="holidays">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg></span>
        <span class="menu-text">Thiết lập ngày nghỉ</span>
      </a>
      <a href="shifts.html" class="nav-item" data-menu="shifts">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg></span>
        <span class="menu-text">Thiết lập ca làm việc</span>
      </a>
      <div style="margin-top: 30px; border-top: 1px solid var(--sidebar-border); padding-top: 10px;"></div>
      <a href="#" class="nav-item" onclick="logout(event)" style="color: #dc2626;">
        <span class="icon" aria-hidden="true"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg></span>
        <span class="menu-text">Đăng xuất</span>
      </a>
    </nav>
  </aside>
"""

# Script to inject for dynamic UI layout processing
# We will execute this script in window.onload or as a separate function called immediately
dynamic_ui_script = """
    function setupDynamicUI() {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.role) return;

        let normalizedRole = user.role.toLowerCase()
            .replace(/đ/g, 'd').normalize('NFD').replace(/[\\u0300-\\u036f]/g, '').replace(/\\s+/g, '-');

        const titleMap = {
            'admin': { title: 'Quản trị Viên', subtitle: 'Phòng Hành chính' },
            'quan-ly-phong-kham': { title: 'Quản lý', subtitle: 'Phòng Khám Nha Khoa' },
            'le-tan': { title: 'Lễ tân', subtitle: 'Quầy tiếp đón' },
            'bac-si': { title: 'Bác sĩ', subtitle: 'Chuyên môn' },
            'benh-nhan': { title: 'Bệnh nhân', subtitle: 'Cổng thông tin' }
        };

        const allowedMenus = {
            'admin': ['users', 'doctors', 'services', 'prices', 'holidays', 'shifts'],
            'quan-ly-phong-kham': ['doctors', 'schedules', 'services', 'prices', 'holidays', 'shifts'],
            'le-tan': ['appointments', 'tracking', 'schedules'],
            'bac-si': ['schedules', 'tracking'],
            'benh-nhan': ['appointments']
        };

        const currentSettings = titleMap[normalizedRole] || { title: 'Hệ thống', subtitle: 'Người dùng' };
        
        const titleEl = document.getElementById('sidebarTitle');
        const subtitleEl = document.getElementById('sidebarSubtitle');
        if (titleEl) titleEl.innerText = currentSettings.title;
        if (subtitleEl) subtitleEl.innerText = currentSettings.subtitle;

        const allowed = allowedMenus[normalizedRole] || [];
        document.querySelectorAll('#sidebarNav .nav-item[data-menu]').forEach(el => {
            const menuId = el.getAttribute('data-menu');
            if (!allowed.includes(menuId)) {
                el.style.display = 'none';
            } else {
                el.style.display = 'flex';
                // Set active class if this is the current page
                if (window.location.href.includes(menuId + '.html')) {
                    el.classList.add('active');
                } else {
                    el.classList.remove('active');
                }
            }
        });
    }
"""

html_files = ["appointments.html", "tracking.html", "schedules.html", "doctors.html", "users.html", "services.html", "prices.html", "holidays.html", "shifts.html"]

for filename in html_files:
    filepath = os.path.join(base_dir, filename)
    if not os.path.exists(filepath):
        continue
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 1. Replace the entire <aside class="sidebar">...</aside> block
    # Regex to match the sidebar block robustly
    pattern = r'<aside class="sidebar".*?</aside>'
    new_content = re.sub(pattern, standard_sidebar.strip(), content, flags=re.DOTALL)
    
    # 2. Inject setupDynamicUI script if not present
    if "function setupDynamicUI()" not in new_content:
        # Add it before the closing </body> tag or inside the first <script>
        script_block = "<script>\n" + dynamic_ui_script + "\n</script>\n</body>"
        new_content = new_content.replace("</body>", script_block)
        
    # 3. Modify window.onload to call setupDynamicUI()
    # Find window.onload = function() {
    if "window.onload = function() {" in new_content:
        if "setupDynamicUI();" not in new_content:
            new_content = new_content.replace("window.onload = function() {", "window.onload = function() {\n        setupDynamicUI();")
    
    # 4. Remove all my old hacky admin-hiding and quan-ly-phong-kham-hiding scripts!
    # Let's use regex to strip out my old hacky logic in window.onload
    # Remove: if (normalizedRole === 'admin' || (user && user.role === 'Admin')) { ... }
    bad_admin_logic = re.compile(r"if\s*\(\s*normalizedRole\s*===\s*'admin'\s*\|\|\s*\(user\s*&&\s*user\.role\s*===\s*'Admin'\)\)\s*\{[\s\S]*?\}\s*\}", re.MULTILINE)
    new_content = re.sub(bad_admin_logic, "", new_content)
    
    # Remove: if (normalizedRole === 'quan-ly-phong-kham') { ... }
    bad_manager_logic = re.compile(r"if\s*\(\s*normalizedRole\s*===\s*'quan-ly-phong-kham'\s*\)\s*\{[\s\S]*?\}\s*\}", re.MULTILINE)
    new_content = re.sub(bad_manager_logic, "", new_content)

    # Some old ones didn't have the closing brace properly matched in regex, let's just do simple replacements
    new_content = re.sub(r"document\.querySelectorAll\('\.admin-only'\)\.forEach\(el => \{.*?\}\);", "", new_content, flags=re.DOTALL)
    new_content = re.sub(r"document\.querySelectorAll\('\.everyone'\)\.forEach\(el => \{.*?\}\);", "", new_content, flags=re.DOTALL)
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print(f"Unified sidebar for {filename}")

print("Sidebar unification completed successfully!")
