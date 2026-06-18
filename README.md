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
Có 2 cách để chạy dự án:

**Cách 1: Chạy bằng dòng lệnh (Terminal / Command Prompt)**
Mở Terminal tại thư mục gốc của dự án và gõ 2 lệnh sau:
- Bước 1 (Build): `.\mvnw clean package -DskipTests` (trên Mac/Linux: `./mvnw clean package -DskipTests`)
- Bước 2 (Chạy): `java -jar target\demo-0.0.1-SNAPSHOT.jar` (trên Mac/Linux: `java -jar target/demo-0.0.1-SNAPSHOT.jar`)

**Cách 2: Chạy bằng IDE (IntelliJ IDEA, Eclipse, VS Code...)**
1. Mở thư mục dự án bằng IDE.
2. Tìm và chạy file `src/main/java/com/example/demo/DemoApplication.java` (chứa hàm `main`).

Sau khi ứng dụng báo chạy thành công, mở trình duyệt và truy cập `http://localhost:8080` để sử dụng hệ thống.

---
### Ghi chú các tài khoản có sẵn để test
Vì file `database.sql` đã sao chép y nguyên trạng thái database của bạn, nên các tài khoản và mật khẩu sẽ **giống hệt như những gì bạn đang dùng**.

Dưới đây là danh sách các tài khoản có trong hệ thống (Mật khẩu chung cho tất cả là: `12345678`):
- **Bác sĩ**: `bs.a@nhakhoa.com`
- **Bệnh nhân**: `bn1@gmail.com`
- **Quản lý**: `quanly@nhakhoa.com`
- **Lễ tân**: `letan1@nhakhoa.com`


### TEST: Chạy toàn bộ các bài kiểm thử (Unit Test & UI Test)
Lệnh: mvn test

### Chạy một class kiểm thử cụ thể
Lệnh Ví Dụ: mvn test -Dtest=SalaryServiceTest
            mvn test -Dtest=UC4_3_MonthlySalaryTest

### Chạy các bài kiểm thử trong một package cụ thể
Lệnh: mvn test -Dtest="com.example.demo.service.*Test"

### Bỏ qua kiểm thử khi build (Đóng gói dự án)
Lệnh: mvn clean package -DskipTests
