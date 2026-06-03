const fs = require('fs');
const destDir = 'd:\\\\Đánh giá và kiểm định\\\\demo\\\\demo\\\\src\\\\main\\\\resources\\\\static\\\\';

const files = ['users.html', 'doctors.html', 'services.html'];
files.forEach(f => {
    let text = fs.readFileSync(destDir + f, 'utf8');
    text = text.replace(/data-route="bac-si"/g, 'onclick="window.location.href=\\'doctors.html\\'"');
    text = text.replace(/data-route="dich-vu"/g, 'onclick="window.location.href=\\'services.html\\'"');
    text = text.replace(/<button class="nav-item active">/g, '<button class="nav-item" onclick="window.location.href=\\'users.html\\'">');
    fs.writeFileSync(destDir + f, text, 'utf8');
});

let dash = fs.readFileSync(destDir + 'dashboard.html', 'utf8');
const newBtn = '<button class="btn-logout" style="background:#2563eb;color:white;border:none;margin-bottom:10px;" id="adminBtn" onclick="window.location.href=\\'users.html\\'">Vào Trang Quản Trị</button><br><button class="btn-logout" id="logoutBtn">Đăng xuất</button>';
dash = dash.replace('<button class="btn-logout" id="logoutBtn">Đăng xuất</button>', newBtn);
dash = dash.replace('badge.classList.add(roleClass);', "badge.classList.add(roleClass); if(user.role !== 'Admin') { let ab = document.getElementById('adminBtn'); if(ab) ab.style.display = 'none'; }");
fs.writeFileSync(destDir + 'dashboard.html', dash, 'utf8');
console.log('Fixed!');
