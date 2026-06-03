import re

filepath = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\doctors.html"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add getHeaders function inside the script block
header_func = """
    function getAuthHeaders() {
        const user = JSON.parse(localStorage.getItem('user'));
        const role = user ? user.role : '';
        return {
            'Content-Type': 'application/json',
            'X-Role': encodeURIComponent(role)
        };
    }
"""

# Insert getAuthHeaders at the beginning of the second script block (where const allUsers is defined)
content = content.replace("let allUsers = [];", header_func + "\n    let allUsers = [];")

# Replace fetch calls
content = content.replace("fetch('/api/users/unassigned-doctors')", "fetch('/api/users/unassigned-doctors', {headers: getAuthHeaders()})")
content = content.replace("fetch('/api/users')", "fetch('/api/users', {headers: getAuthHeaders()})")
content = content.replace("fetch('/api/doctors')", "fetch('/api/doctors', {headers: getAuthHeaders()})")
content = content.replace("headers: {'Content-Type': 'application/json'}", "headers: getAuthHeaders()")

# Update onload logic
old_onload = """        // Admin and Lễ tân can access
        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'le-tan')) {"""
new_onload = """        // Admin and Quản lý phòng khám can access
        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {"""
content = content.replace(old_onload, new_onload)

# Update UI hiding logic
old_hide = """            // Hide admin only links if role is Lễ tân
            if (normalizedRole === 'le-tan') {"""
new_hide = """            // Hide admin only links if role is Quản lý phòng khám
            if (normalizedRole === 'quan-ly-phong-kham') {"""
content = content.replace(old_hide, new_hide)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patched doctors.html successfully!")
