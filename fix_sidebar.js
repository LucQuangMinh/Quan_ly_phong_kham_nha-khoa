const fs = require('fs');
const path = require('path');
const destDir = 'd:\\\\Đánh giá và kiểm định\\\\demo\\\\demo\\\\src\\\\main\\\\resources\\\\static\\\\';

const files = ['users.html', 'doctors.html', 'services.html'];

files.forEach(file => {
    let content = fs.readFileSync(path.join(destDir, file), 'utf8');
    
    content = content.split('<button type="button" class="usecase-nav" data-route="bac-si">Hồ sơ bác sĩ</button>')
        .join('<button type="button" class="usecase-nav" onclick="window.location.href=\\'doctors.html\\'">Hồ sơ bác sĩ</button>');
        
    content = content.split('<button type="button" class="usecase-nav" data-route="dich-vu">Danh mục dịch vụ & giá</button>')
        .join('<button type="button" class="usecase-nav" onclick="window.location.href=\\'services.html\\'">Danh mục dịch vụ & giá</button>');

    content = content.split('<button type="button" class="usecase-nav" data-route="dich-vu">Danh mục dịch vụ</button>')
        .join('<button type="button" class="usecase-nav" onclick="window.location.href=\\'services.html\\'">Danh mục dịch vụ</button>');

    // Quản lý người dùng is usually an anchor or button
    content = content.replace(/<a href="#" class="nav-item">\\s*<span class="icon"[^>]*>\\s*<svg[^>]*>[\\s\\S]*?<\\/svg>\\s*<\\/span>\\s*Quản lý Người dùng\\s*<\\/a>/,
        '<a href="users.html" class="nav-item"><span class="icon"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></span>Quản lý Người dùng</a>');

    // Make sure 'users.html' link is working if it's already a nav-item active
    content = content.replace(/<button class="nav-item active">/g, '<button class="nav-item" onclick="window.location.href=\\'users.html\\'">');
    content = content.replace(/<button class="nav-item">\\s*<span class="icon"[^>]*>\\s*<svg[^>]*>[\\s\\S]*?<\\/svg>\\s*<\\/span>\\s*Quản lý Người dùng\\s*<\\/button>/,
        '<button class="nav-item" onclick="window.location.href=\\'users.html\\'"><span class="icon"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg></span>Quản lý Người dùng</button>');

    fs.writeFileSync(path.join(destDir, file), content, 'utf8');
});

// Fix dashboard.html
let dash = fs.readFileSync(path.join(destDir, 'dashboard.html'), 'utf8');
dash = dash.replace(/<button class="btn-logout" id="logoutBtn">Đăng xuất<\\/button>/,
    \`<button class="btn-logout" style="background: #2563eb; color: white; border: none; margin-bottom: 10px;" id="adminBtn" onclick="window.location.href='users.html'">Vào Trang Quản Trị (Admin)</button><br><button class="btn-logout" id="logoutBtn">Đăng xuất</button>\`
);
dash = dash.replace(/badge\.classList\.add\\(roleClass\\);/, 
    \`badge.classList.add(roleClass);
    if(user.role !== 'Admin') {
        document.getElementById('adminBtn').style.display = 'none';
    }\`
);
fs.writeFileSync(path.join(destDir, 'dashboard.html'), dash, 'utf8');

console.log('Sidebar fixed');
