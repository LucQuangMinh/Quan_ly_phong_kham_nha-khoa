import os
import glob

files = glob.glob(r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\*.html')
targets = ['users.html', 'doctors.html', 'services.html', 'holidays.html', 'shifts.html', 'schedules.html', 'tracking.html']

logout_btn = '''
      <div style="margin-top: 30px; border-top: 1px solid var(--sidebar-border); padding-top: 10px;"></div>
      <a href="#" class="nav-item" onclick="logout(event)" style="color: #dc2626;">
        <span class="icon" aria-hidden="true">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
        </span>
        Đăng xuất
      </a>
    </nav>'''

logout_js = '''
  <script>
    function logout(e) {
      if (e) e.preventDefault();
      localStorage.removeItem('user');
      window.location.href = 'login.html';
    }
  </script>
</body>'''

for path in files:
    if os.path.basename(path) in targets:
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        if 'function logout(' not in content:
            content = content.replace('</nav>', logout_btn)
            content = content.replace('</body>', logout_js)
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Updated {os.path.basename(path)}')
