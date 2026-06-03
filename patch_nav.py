import os

dest_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"
files = ["users.html", "doctors.html", "services.html"]

script_to_add = """
<script>
document.addEventListener("DOMContentLoaded", function() {
    let bacSiBtns = document.querySelectorAll('button[data-route="bac-si"]');
    bacSiBtns.forEach(b => b.onclick = () => window.location.href = "doctors.html");

    let dichVuBtns = document.querySelectorAll('button[data-route="dich-vu"]');
    dichVuBtns.forEach(b => b.onclick = () => window.location.href = "services.html");

    // "Quản lý Người dùng" is usually an a tag with class "nav-item" or button
    let qlUsers = Array.from(document.querySelectorAll('.nav-item')).filter(el => el.textContent.includes('Quản lý Người dùng'));
    qlUsers.forEach(el => el.onclick = (e) => { e.preventDefault(); window.location.href = "users.html"; });
});
</script>
</body>
"""

for file in files:
    path = os.path.join(dest_dir, file)
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    
    if "bacSiBtns" not in content:
        content = content.replace("</body>", script_to_add)
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Patched {file}")
