# Quản Lý Phòng Khám Nha Khoa

Đây là hệ thống phần mềm Quản lý Phòng Khám Nha Khoa. 
Dự án được xây dựng bằng Spring Boot (Backend) và Vanilla HTML/CSS/JS (Frontend).

## Hướng dẫn cài đặt và cấu hình Database cho người mới (Sử dụng XAMPP)

Để các bạn trong nhóm khi clone code về có thể chạy được ngay với đầy đủ dữ liệu (tài khoản, bệnh nhân, lịch hẹn, bảng lương...), vui lòng làm theo các bước sau:

### Bước 1: Khởi động XAMPP và tạo Database
1. Mở phần mềm **XAMPP Control Panel**.
2. Nhấn nút **Start** ở hai mục **Apache** và **MySQL**.
   *(Lưu ý: Đảm bảo MySQL của bạn đang chạy ở cổng mặc định 3306, hoặc 3307 tùy cấu hình)*.
3. Mở trình duyệt, truy cập vào `http://localhost/phpmyadmin/`.
4. Bấm vào nút **Mới (New)** ở cột bên trái để tạo cơ sở dữ liệu mới.
5. Nhập tên cơ sở dữ liệu là: `quan_ly_phong_kham_nha_khoa` và chọn bảng mã `utf8mb4_unicode_ci` (hoặc `utf8mb4_general_ci`).
6. Bấm **Tạo (Create)**.

### Bước 2: Import dữ liệu (Nhập dữ liệu có sẵn)
1. Trong phpMyAdmin, đảm bảo bạn đang chọn Database `quan_ly_phong_kham_nha_khoa` vừa tạo.
2. Nhấn vào tab **Nhập (Import)** ở thanh menu phía trên.
3. Trong phần *File to import*, bấm **Choose File** và trỏ đến file `database.sql` nằm ở thư mục gốc của dự án này (nơi bạn vừa clone code về).
4. Kéo xuống dưới cùng và bấm **Nhập (Import)** / **Thực hiện (Go)**.
5. Chờ vài giây để hệ thống tải toàn bộ các bảng và dữ liệu mẫu lên.

### Bước 3: Kiểm tra cấu hình kết nối trong Spring Boot
Mở file cấu hình `src/main/resources/application.properties` để kiểm tra kết nối:
```properties
# Đảm bảo port (3306 hay 3307) khớp với port MySQL trên XAMPP của bạn
spring.datasource.url=jdbc:mysql://localhost:3307/quan_ly_phong_kham_nha_khoa?useSSL=false&serverTimezone=UTC

# Tài khoản mặc định của XAMPP thường là root và không có mật khẩu
spring.datasource.username=root
spring.datasource.password=

# ddl-auto có thể để là update hoặc none. 
# (Vì đã import database.sql nên để update hay none đều được)
spring.jpa.hibernate.ddl-auto=update
```

### Bước 4: Chạy dự án
1. Mở dự án bằng IDE (IntelliJ IDEA, Eclipse, VS Code...).
2. Chạy file `DemoApplication.java` (chứa hàm `main`).
3. Mở trình duyệt và truy cập `http://localhost:8080` để sử dụng hệ thống.

---
### Ghi chú các tài khoản có sẵn để test
- **Admin**: `admin` / pass: (tuỳ bạn tạo)
- **Lễ tân**: `letan1`
- **Quản lý**: `quanly1`
- **Bác sĩ**: Xem trong bảng `doctors` hoặc `users` ở database.
