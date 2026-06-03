import os
import re

static_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

# Remove dashboard.html file
dashboard_path = os.path.join(static_dir, "dashboard.html")
if os.path.exists(dashboard_path):
    os.remove(dashboard_path)
    print("Deleted dashboard.html")

# Define the new redirect block
new_redirect = """                    // Chuyển hướng dựa trên Role
                    if (data.role === 'Admin') {
                        window.location.href = 'users.html';
                    } else if (data.role === 'Bác sĩ') {
                        window.location.href = 'schedules.html';
                    } else if (data.role === 'Lễ tân') {
                        window.location.href = 'tracking.html';
                    } else if (data.role === 'Quản lý phòng khám') {
                        window.location.href = 'doctors.html';
                    } else {
                        window.location.href = 'appointments.html';
                    }"""

new_redirect_index = """        const user = JSON.parse(localStorage.getItem("user"));
        if (user) {
            if (user.role === 'Admin') {
                window.location.href = 'users.html';
            } else if (user.role === 'Bác sĩ') {
                window.location.href = 'schedules.html';
            } else if (user.role === 'Lễ tân') {
                window.location.href = 'tracking.html';
            } else if (user.role === 'Quản lý phòng khám') {
                window.location.href = 'doctors.html';
            } else {
                window.location.href = 'appointments.html';
            }
        }"""

for filename in os.listdir(static_dir):
    if not filename.endswith(".html"):
        continue
    filepath = os.path.join(static_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    modified = False

    # Remove dashboard link from sidebar
    # We will look for <a href="dashboard.html"... </a> and remove it.
    # Typically it's multiple lines like:
    # <a href="dashboard.html" class="nav-item staff-only">
    #   <svg ...</svg>
    #   Dashboard
    # </a>
    # Actually, we can just use regex to remove the whole <a> tag containing dashboard.html
    pattern = r'\s*<a href="dashboard\.html"[^>]*>.*?</a>'
    new_content, count = re.subn(pattern, '', content, flags=re.DOTALL)
    if count > 0:
        content = new_content
        modified = True

    # Update index.html
    if filename == 'index.html':
        old_index = """        const user = JSON.parse(localStorage.getItem("user"));
        if (user) {
            if (user.role === 'Admin') {
                window.location.href = 'users.html';
            } else if (user.role === 'Bác sĩ') {
                window.location.href = 'schedules.html';
            } else if (user.role === 'Lễ tân') {
                window.location.href = 'tracking.html';
            } else {
                window.location.href = 'dashboard.html';
            }
        }"""
        if old_index in content:
            content = content.replace(old_index, new_redirect_index)
            modified = True

    # Update login.html
    if filename == 'login.html':
        old_login = """                    // Chuyển hướng dựa trên Role
                    if (data.role === 'Admin') {
                        window.location.href = 'users.html';
                    } else if (data.role === 'Bác sĩ') {
                        window.location.href = 'schedules.html';
                    } else if (data.role === 'Lễ tân') {
                        window.location.href = 'tracking.html';
                    } else if (data.role === 'Bệnh nhân') {
                        window.location.href = 'appointments.html';
                    } else {
                        window.location.href = 'dashboard.html';
                    }"""
        if old_login in content:
            content = content.replace(old_login, new_redirect)
            modified = True

    # Update shifts.html which has a manual check
    if filename == 'shifts.html':
        if "window.location.href = 'dashboard.html';" in content:
            content = content.replace("window.location.href = 'dashboard.html';", "window.location.href = 'doctors.html';")
            modified = True

    if modified:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filename}")

print("Done")
