import os
import re

file_path = r'd:\Đánh giá và kiểm định\demo\demo\src\main\resources\static\tracking.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Extract modal
modal_match = re.search(r'<!-- Modal -->(.*?)<script>', content, re.DOTALL)
modal_html = modal_match.group(1).strip() if modal_match else ''

# Extract script
script_match = re.search(r'<script>(.*?)</script>', content, re.DOTALL)
script_js = script_match.group(1).strip() if script_match else ''

new_content = f'''<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Theo dõi lịch khám</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
    <style>
        :root {{
          --sidebar-bg: #f8fafc;
          --sidebar-border: #e2e8f0;
          --text-primary: #1e293b;
          --text-muted: #64748b;
          --active-bg: #e8f1fe;
          --active-text: #2563eb;
          --hover-bg: #f1f5f9;
          --radius-item: 10px;
          --primary: #2563eb;
          --primary-hover: #1d4ed8;
          --surface: #ffffff;
          --table-head: #f1f5f9;
          --danger: #dc2626;
          --danger-bg: #fef2f2;
        }}

        * {{ box-sizing: border-box; }}

        body {{
          margin: 0; min-height: 100vh; font-family: "Inter", system-ui, sans-serif;
          background: #fff; color: var(--text-primary); display: flex;
        }}

        .sidebar {{ width: 288px; min-height: 100vh; background: var(--sidebar-bg); border-right: 1px solid var(--sidebar-border); padding: 20px 12px 24px; flex-shrink: 0; }}
        .sidebar-header {{ padding: 4px 10px 16px; border-bottom: 1px solid var(--sidebar-border); margin-bottom: 12px; }}
        .sidebar-header h1 {{ margin: 0; font-size: 1rem; font-weight: 700; letter-spacing: -0.02em; }}
        .sidebar-header p {{ margin: 4px 0 0; font-size: 0.8125rem; color: var(--text-muted); }}
        .nav {{ display: flex; flex-direction: column; gap: 2px; }}
        .nav-item {{ display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: var(--radius-item); font-size: 0.875rem; font-weight: 500; color: var(--text-primary); text-decoration: none; border: none; background: transparent; width: 100%; text-align: left; cursor: pointer; transition: background 0.15s ease, color 0.15s ease; }}
        .nav-item:hover {{ background: var(--hover-bg); }}
        .nav-item .icon {{ flex-shrink: 0; width: 22px; height: 22px; color: var(--text-muted); }}
        .nav-item.active {{ background: var(--active-bg); color: var(--active-text); }}
        .nav-item.active .icon {{ color: var(--active-text); }}
        
        main {{ flex: 1; min-width: 0; padding: 24px 32px 40px; background: #fafbfc; display: flex; flex-direction: column; height: 100vh; overflow: hidden; }}
        
        .content-shell {{ max-width: 1400px; margin: 0 auto; width: 100%; height: 100%; display: flex; flex-direction: column; }}
        
        .page-head {{ display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 14px; margin-bottom: 18px; flex-shrink: 0; }}
        .page-head h1 {{ margin: 0; font-size: 1.25rem; font-weight: 600; }}
        .crumb {{ font-size: 0.8125rem; color: var(--text-muted); margin: 0 0 16px; flex-shrink: 0; }}
        .crumb strong {{ color: var(--text-primary); font-weight: 600; }}

        /* Tracking specific CSS */
        .btn {{ padding: 8px 16px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.875rem; font-weight: 500; transition: all 0.2s; }}
        .btn-primary {{ background: var(--primary); color: white; }}
        .btn-primary:hover {{ background: var(--primary-hover); }}
        .btn-danger {{ background: var(--danger); color: white; }}
        .btn-danger:hover {{ background: #b91c1c; }}
        
        .tabs {{ display: flex; gap: 8px; border-bottom: 2px solid #e2e8f0; margin-bottom: 20px; padding-bottom: 0; flex-shrink: 0; }}
        .tab-btn {{ background: none; border: none; padding: 10px 16px; cursor: pointer; color: #64748b; font-weight: 500; border-bottom: 2px solid transparent; margin-bottom: -2px; }}
        .tab-btn.active {{ color: #2563eb; border-bottom-color: #2563eb; }}
        .tab-btn:hover:not(.active) {{ color: #334155; border-bottom-color: #cbd5e1; }}
        
        .table-container {{ flex: 1; overflow-y: auto; border: 1px solid #e2e8f0; border-radius: 8px; background: white; }}
        table {{ width: 100%; border-collapse: collapse; }}
        th, td {{ padding: 12px 16px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 0.875rem; }}
        th {{ background: #f8fafc; font-weight: 600; color: #475569; position: sticky; top: 0; z-index: 10; }}
        tr:last-child td {{ border-bottom: none; }}
        tr:hover {{ background: #f1f5f9; }}
        
        .status-badge {{ padding: 4px 8px; border-radius: 999px; font-size: 0.75rem; font-weight: 500; display: inline-block; }}
        .status-chờ {{ background: #fef9c3; color: #854d0e; }}
        .status-đang {{ background: #dbeafe; color: #1e40af; }}
        .status-đã {{ background: #dcfce3; color: #166534; }}
        .status-hủy, .status-vắng {{ background: #fee2e2; color: #991b1b; }}
        
        /* Modal styles */
        .modal-backdrop {{ display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 50; }}
        .modal {{ position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 24px; border-radius: 8px; width: 100%; max-width: 500px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }}
        .modal h2 {{ margin-top: 0; margin-bottom: 16px; font-size: 1.25rem; }}
        .form-group {{ margin-bottom: 16px; }}
        .form-group label {{ display: block; margin-bottom: 4px; font-weight: 500; font-size: 0.875rem; }}
        .form-control {{ width: 100%; padding: 8px; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; font-family: inherit; }}
        .modal-actions {{ display: flex; justify-content: flex-end; gap: 8px; margin-top: 24px; }}
        .btn-secondary {{ background: #f1f5f9; color: #475569; }}
        .btn-secondary:hover {{ background: #e2e8f0; }}
        
        .action-link {{ color: #2563eb; text-decoration: none; cursor: pointer; font-weight: 500; margin-right: 8px; }}
        .action-link:hover {{ text-decoration: underline; }}
        .action-delete {{ color: #ef4444; }}
    </style>
</head>
<body>
    <aside class="sidebar" aria-label="Điều hướng chính">
        <header class="sidebar-header">
            <h1>Hệ thống Nha Khoa</h1>
            <p id="userRoleDisplay">Đang tải...</p>
        </header>
        <nav class="nav" aria-label="Menu quản trị">
            <a href="dashboard.html" class="nav-item">Bảng điều khiển</a>
            <a href="users.html" class="nav-item admin-only">Quản lý Người dùng</a>
            <a href="doctors.html" class="nav-item admin-only">Hồ sơ Bác sĩ</a>
            <a href="services.html" class="nav-item admin-only">Danh mục Dịch vụ</a>
            <a href="holidays.html" class="nav-item admin-only">Thiết lập ngày nghỉ</a>
            <a href="shifts.html" class="nav-item admin-only">Thiết lập ca làm việc</a>
            <a href="schedules.html" class="nav-item admin-only">Đăng ký lịch trực</a>
            <a href="tracking.html" class="nav-item active">
                <span class="icon" aria-hidden="true">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M13.8 12H3"/>
                    </svg>
                </span>
                Theo dõi lịch khám
            </a>
        </nav>
    </aside>

    <main>
        <div class="content-shell">
            <div class="crumb">Hệ thống / <strong>Theo dõi lịch khám</strong></div>
            <div class="page-head">
                <h1 id="pageTitle">Theo dõi lịch khám</h1>
                <button id="btnAdd" class="btn btn-primary" onclick="openAddModal()" style="display: none;">+ Thêm bản ghi</button>
            </div>
            
            <div class="tabs" id="tabsContainer">
                <button class="tab-btn active" onclick="switchTab('Đang chờ')">Chờ tiếp nhận</button>
                <button class="tab-btn" onclick="switchTab('Đang khám')">Hàng đợi</button>
                <button class="tab-btn" onclick="switchTab('Đã khám')">Đã khám</button>
                <button class="tab-btn" onclick="switchTab('Đã hủy')">Đã hủy</button>
                <button class="tab-btn" onclick="switchTab('Vắng')">Vắng</button>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Mã lượt</th>
                            <th>Bệnh nhân</th>
                            <th>Ngày khám</th>
                            <th>Phòng khám</th>
                            <th>Trạng thái</th>
                            <th>Cập nhật lúc</th>
                            <th id="thActions" style="display: none;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody id="tableBody">
                        <!-- Data goes here -->
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <!-- Modal -->
    {{modal_html}}

    <script>
        {{script_js}}
        
        // Cập nhật lại UI header dựa trên role
        const oldCheckLogin = checkLogin;
        checkLogin = async function() {{
            const userStr = localStorage.getItem('user');
            if (userStr) {{
                const data = JSON.parse(userStr);
                document.getElementById('userRoleDisplay').innerText = data.role;
                
                // Ẩn menu admin-only nếu không phải Admin
                if (data.role !== 'Admin') {{
                    document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
                }}
            }}
            
            // Gọi lại hàm gốc để tải dữ liệu
            await oldCheckLogin();
        }};
        
        // Ghi đè initUI để set up role
        const oldInitUI = initUI;
        initUI = function() {{
            if (currentRole === 'bac-si') {{
                document.getElementById('pageTitle').textContent = `Danh sách khám bệnh nhân (BS. ${{currentUser.fullname}})`;
            }} else if (currentRole === 'admin' || currentRole === 'le-tan') {{
                document.getElementById('pageTitle').textContent = 'Theo dõi lịch khám';
                document.getElementById('btnAdd').style.display = 'block';
                document.getElementById('thActions').style.display = 'table-cell';
            }}
            loadDoctors();
            loadData();
        }};
    </script>
</body>
</html>
'''

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(new_content)

print('Updated tracking.html')
