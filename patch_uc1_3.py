import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo"

# 1. Update DentalService.java
ds_file = os.path.join(base_dir, "src/main/java/com/example/demo/entity/DentalService.java")
with open(ds_file, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('@Column(name = "service_code")', '@Column(name = "code")')
content = content.replace('private String serviceCode;', 'private String code;')
content = content.replace('@Column(name = "service_name")', '@Column(name = "name")')
content = content.replace('private String serviceName;', 'private String name;')

with open(ds_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update DentalServiceRepository.java
repo_file = os.path.join(base_dir, "src/main/java/com/example/demo/repository/DentalServiceRepository.java")
with open(repo_file, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('existsByServiceCode', 'existsByCode')
content = content.replace('existsByServiceCodeAndIdNot', 'existsByCodeAndIdNot')

with open(repo_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 3. Update DentalServiceService.java
srv_file = os.path.join(base_dir, "src/main/java/com/example/demo/service/DentalServiceService.java")
with open(srv_file, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('getServiceCode', 'getCode')
content = content.replace('setServiceCode', 'setCode')
content = content.replace('getServiceName', 'getName')
content = content.replace('setServiceName', 'setName')
content = content.replace('existsByServiceCode', 'existsByCode')
content = content.replace('existsByServiceCodeAndIdNot', 'existsByCodeAndIdNot')

with open(srv_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 4. Update DentalServiceController.java
ctrl_file = os.path.join(base_dir, "src/main/java/com/example/demo/controller/DentalServiceController.java")
with open(ctrl_file, 'r', encoding='utf-8') as f:
    content = f.read()

if 'import com.example.demo.security.PreAuthorize;' not in content:
    content = content.replace('import org.springframework.web.bind.annotation.*;', 'import org.springframework.web.bind.annotation.*;\nimport com.example.demo.security.PreAuthorize;')

if '@PreAuthorize' not in content:
    content = content.replace('@RestController\n@RequestMapping("/api/services")', '@RestController\n@RequestMapping("/api/services")\n@PreAuthorize(roles = {"Admin", "Quản lý phòng khám"})')

with open(ctrl_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 5. Update services.html
html_file = os.path.join(base_dir, "src/main/resources/static/services.html")
with open(html_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace serviceCode -> code, serviceName -> name (in JS only)
# Be careful not to break IDs, but actually IDs can be left as serviceCode or changed. Let's change the object access.
content = content.replace('s.serviceCode', 's.code')
content = content.replace('s.serviceName', 's.name')
content = content.replace('serviceCode:', 'code:')
content = content.replace('serviceName:', 'name:')

# Add getAuthHeaders
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

if 'function getAuthHeaders()' not in content:
    # Insert it at the top of the <script> block
    content = content.replace('async function loadServices() {', header_func + '\n    async function loadServices() {')

# Replace fetch calls
content = content.replace("fetch('/api/services')", "fetch('/api/services', {headers: getAuthHeaders()})")
content = content.replace("headers: {'Content-Type': 'application/json'}", "headers: getAuthHeaders()")

# Fix window.onload
old_onload = """    window.onload = function() {
        const user = JSON.parse(localStorage.getItem('user'));
        if(!user || user.role !== 'Admin') {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            loadServices();
        }
    }"""

new_onload = """    window.onload = function() {
        const user = JSON.parse(localStorage.getItem('user'));
        
        let normalizedRole = '';
        if (user && user.role) {
            normalizedRole = user.role.toLowerCase()
                .replace(/đ/g, 'd')
                .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
                .replace(/\s+/g, '-');
        }

        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            if (normalizedRole === 'quan-ly-phong-kham') {
                document.querySelectorAll('.admin-only').forEach(el => {
                    // Giữ lại menu Danh mục Dịch vụ và Hồ sơ Bác sĩ
                    if (el.textContent.includes('Hồ sơ Bác sĩ') || el.textContent.includes('Danh mục Dịch vụ')) return;
                    el.style.display = 'none';
                });
            }
            loadServices();
        }
    }"""

content = content.replace(old_onload, new_onload)

with open(html_file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Patch applied successfully!")
