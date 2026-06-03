import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo\src\main\resources\static"

# 1. Update services.html
services_file = os.path.join(base_dir, "services.html")
with open(services_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Make serviceCode input disabled and add style to show it's read-only
old_code_input = '<input type="text" id="serviceCode" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" />'
new_code_input = '<input type="text" id="serviceCode" disabled style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px; background-color: #f1f5f9; color: #64748b;" />'
content = content.replace(old_code_input, new_code_input)

# Update closeModal for addServiceModal to set default text
content = content.replace("document.getElementById('serviceCode').value = '';", "document.getElementById('serviceCode').value = 'Tự động sinh';")

with open(services_file, 'w', encoding='utf-8') as f:
    f.write(content)


# 2. Update prices.html
prices_file = os.path.join(base_dir, "prices.html")
with open(prices_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Add serviceCategory dropdown before dentalService
old_service_div = """        <div style="margin-bottom: 12px;">
          <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Dịch vụ (*)</label>
          <select id="dentalService" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;"></select>
        </div>"""

new_service_div = """        <div style="margin-bottom: 12px; display: flex; gap: 10px;">
          <div style="flex: 1;">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Nhóm dịch vụ (*)</label>
            <select id="serviceCategory" style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px;" onchange="filterServicesByCategory()">
              <option value="">-- Chọn nhóm --</option>
              <option value="Khám">Khám</option>
              <option value="Điều trị">Điều trị</option>
              <option value="Thẩm mỹ">Thẩm mỹ</option>
              <option value="Phẫu thuật">Phẫu thuật</option>
            </select>
          </div>
          <div style="flex: 2;">
            <label style="display: block; font-size: 0.75rem; font-weight: 600; color: #64748b; margin-bottom: 6px;">Dịch vụ chi tiết (*)</label>
            <select id="dentalService" disabled style="width: 100%; padding: 10px; border: 1px solid #e2e8f0; border-radius: 8px; background-color: #f1f5f9;"></select>
          </div>
        </div>"""

if "Nhóm dịch vụ" not in content:
    content = content.replace(old_service_div, new_service_div)

# Add filterServicesByCategory logic
filter_js = """    function filterServicesByCategory() {
        const category = document.getElementById('serviceCategory').value;
        const select = document.getElementById('dentalService');
        
        if (!category) {
            select.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
            select.disabled = true;
            select.style.backgroundColor = '#f1f5f9';
            return;
        }
        
        select.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
        const filtered = allServices.filter(s => s.status === 'Áp dụng' && s.category === category);
        filtered.forEach(s => {
            select.innerHTML += `<option value="${s.id}">${s.name} (${s.code})</option>`;
        });
        
        select.disabled = false;
        select.style.backgroundColor = '#fff';
    }
"""

if "function filterServicesByCategory()" not in content:
    content = content.replace("async function loadServices() {", filter_js + "\n    async function loadServices() {")

# Modify loadServices to not populate dentalService immediately, just store allServices
load_services_old = """            // Populate dropdown with active services only
            const select = document.getElementById('dentalService');
            select.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
            allServices.filter(s => s.status === 'Áp dụng').forEach(s => {
                select.innerHTML += `<option value="${s.id}">${s.name} (${s.code})</option>`;
            });"""

load_services_new = """            // Data is stored in allServices. Dropdowns are populated by filterServicesByCategory."""
content = content.replace(load_services_old, load_services_new)

# Modify editPrice to set category and manually add the service to dropdown
edit_price_old = """            // Allow selecting the service even if it's inactive by re-adding it temporarily if needed
            let select = document.getElementById('dentalService');
            let optionExists = Array.from(select.options).some(opt => opt.value == p.dentalService.id);
            if (!optionExists) {
                select.innerHTML += `<option value="${p.dentalService.id}">${p.dentalService.name}</option>`;
            }
            select.value = p.dentalService.id;
            select.disabled = true; // Don't allow changing service when editing history"""

edit_price_new = """            // Auto-select category and service
            document.getElementById('serviceCategory').value = p.dentalService.category || '';
            document.getElementById('serviceCategory').disabled = true; // Disable category change
            
            let select = document.getElementById('dentalService');
            select.innerHTML = `<option value="${p.dentalService.id}">${p.dentalService.name} (${p.dentalService.code})</option>`;
            select.value = p.dentalService.id;
            select.disabled = true; // Don't allow changing service when editing history"""

content = content.replace(edit_price_old, edit_price_new)

# Modify closeModal to reset category
close_modal_old = "document.getElementById('dentalService').value = '';"
close_modal_new = "document.getElementById('dentalService').value = '';\n        document.getElementById('serviceCategory').value = '';\n        document.getElementById('serviceCategory').disabled = false;"
content = content.replace(close_modal_old, close_modal_new)

with open(prices_file, 'w', encoding='utf-8') as f:
    f.write(content)


# 3. Patch all HTML files to hide "Đăng ký khám" for Admin
for filename in os.listdir(base_dir):
    if not filename.endswith('.html'):
        continue
    
    filepath = os.path.join(base_dir, filename)
    with open(filepath, 'r', encoding='utf-8') as f:
        file_content = f.read()

    # Find the role checking logic in window.onload
    # Most files have: `if(!user || (normalizedRole !== 'admin' && ...))`
    # Or something similar. We will look for normalizedRole check and append logic.
    if "normalizedRole === 'admin'" in file_content or "normalizedRole === 'Admin'" in file_content or "user.role === 'Admin'" in file_content:
        # Let's just inject a small script to hide the link if role is admin
        hide_script = """
        if (normalizedRole === 'admin' || (user && user.role === 'Admin')) {
            document.querySelectorAll('.everyone').forEach(el => {
                if (el.textContent.includes('Đăng ký khám')) el.style.display = 'none';
            });
        }
        """
        # Inject just before `loadServices` or at the end of the `if/else` block
        if "loadServices()" in file_content and "hide_script" not in file_content: # simplistic check to avoid double insert
            # It's better to just regex replace `window.onload = function() { ... }` but that's hard to generalize.
            # I will inject right before `if(!user ||`
            if "if(!user ||" in file_content and "Đăng ký khám" not in file_content.split("if(!user ||")[0]:
                file_content = file_content.replace("if(!user ||", hide_script + "\n        if(!user ||")
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(file_content)

print("Frontend patched successfully")
