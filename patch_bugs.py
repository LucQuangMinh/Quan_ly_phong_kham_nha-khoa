import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

# 1. Fix doctors.html
doctors_file = os.path.join(base_dir, "doctors.html")
with open(doctors_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace the broken window.onload
broken_onload_start = "    window.onload = function() {"
broken_script_end = """        } else {
            // Hide admin only links if role is Quản lý phòng khám
            
  </script>"""

if broken_onload_start in content and "loadDocs();" not in content.split(broken_onload_start)[1]:
    # We need to rewrite the window.onload
    new_onload = """    window.onload = function() {
        setupDynamicUI();
        const user = JSON.parse(localStorage.getItem('user'));
        
        let normalizedRole = '';
        if (user && user.role) {
            normalizedRole = user.role.toLowerCase()
                .replace(/đ/g, 'd')
                .normalize('NFD').replace(/[\\u0300-\\u036f]/g, '')
                .replace(/\\s+/g, '-');
        }

        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            loadDocs();
        }
    }
  </script>"""
    
    # We will use regex to replace everything from window.onload to </script>
    pattern = r"window\.onload = function\(\) \{.*?</script>"
    content = re.sub(pattern, new_onload, content, flags=re.DOTALL)
    
    with open(doctors_file, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Fixed doctors.html")

# 2. Fix services.html
services_file = os.path.join(base_dir, "services.html")
with open(services_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix saveService missing auth headers
old_fetch = """        const res = await fetch(url, { 
            method: method, 
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data) 
        });"""
new_fetch = """        const res = await fetch(url, { 
            method: method, 
            headers: getAuthHeaders(),
            body: JSON.stringify(data) 
        });"""
content = content.replace(old_fetch, new_fetch)

# Ensure window.onload is intact
broken_onload_services = """        } else {
            // Hide admin only links if role is Quản lý phòng khám
            
  </script>"""
if broken_onload_services in content:
    new_onload_services = """    window.onload = function() {
        setupDynamicUI();
        const user = JSON.parse(localStorage.getItem('user'));
        
        let normalizedRole = '';
        if (user && user.role) {
            normalizedRole = user.role.toLowerCase()
                .replace(/đ/g, 'd')
                .normalize('NFD').replace(/[\\u0300-\\u036f]/g, '')
                .replace(/\\s+/g, '-');
        }

        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            loadServices();
        }
    }
  </script>"""
    pattern = r"window\.onload = function\(\) \{.*?</script>"
    content = re.sub(pattern, new_onload_services, content, flags=re.DOTALL)

with open(services_file, 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed services.html")

# 3. Check prices.html to ensure window.onload is intact
prices_file = os.path.join(base_dir, "prices.html")
with open(prices_file, 'r', encoding='utf-8') as f:
    content = f.read()

if broken_onload_services in content:
    new_onload_prices = """    window.onload = function() {
        setupDynamicUI();
        const user = JSON.parse(localStorage.getItem('user'));
        
        let normalizedRole = '';
        if (user && user.role) {
            normalizedRole = user.role.toLowerCase()
                .replace(/đ/g, 'd')
                .normalize('NFD').replace(/[\\u0300-\\u036f]/g, '')
                .replace(/\\s+/g, '-');
        }

        if(!user || (normalizedRole !== 'admin' && normalizedRole !== 'quan-ly-phong-kham')) {
            alert('Bạn không có quyền truy cập trang này!');
            window.location.href = 'login.html';
        } else {
            loadServices().then(loadPrices);
        }
    }
  </script>"""
    pattern = r"window\.onload = function\(\) \{.*?</script>"
    content = re.sub(pattern, new_onload_prices, content, flags=re.DOTALL)
    
    with open(prices_file, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Fixed prices.html")

print("All bugs patched successfully!")
