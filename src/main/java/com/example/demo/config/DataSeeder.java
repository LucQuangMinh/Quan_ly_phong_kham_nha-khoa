package com.example.demo.config;

import com.example.demo.entity.DentalService;
import com.example.demo.entity.ServicePrice;
import com.example.demo.repository.DentalServiceRepository;
import com.example.demo.repository.ServicePriceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private DentalServiceRepository serviceRepository;

    @Autowired
    private ServicePriceRepository priceRepository;
    
    @Autowired
    private com.example.demo.repository.UserRepository userRepository;
    
    @Autowired
    private com.example.demo.repository.DoctorRepository doctorRepository;
    
    @Autowired
    private com.example.demo.repository.SalaryConfigRepository salaryConfigRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (serviceRepository.count() < 25) {
            System.out.println("Đang xóa dữ liệu cũ và khởi tạo 25 dịch vụ mặc định...");
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE service_prices").executeUpdate();
            entityManager.createNativeQuery("TRUNCATE TABLE dental_services").executeUpdate();
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
            
            seedServices();
            System.out.println("Khởi tạo xong 25 dịch vụ mặc định!");
        }
        
        if (userRepository.count() == 0) {
            System.out.println("Đang khởi tạo tài khoản mẫu (1 Admin, 5 Bác sĩ, 2 Lễ tân)...");
            seedUsers();
            System.out.println("Khởi tạo xong tài khoản mẫu!");
        }

        if (salaryConfigRepository.count() == 0) {
            System.out.println("Đang khởi tạo cấu hình lương mặc định...");
            seedSalaryConfig();
            System.out.println("Khởi tạo xong cấu hình lương!");
        }
        
        // Force update degrees for existing doctors
        List<com.example.demo.entity.Doctor> docs = doctorRepository.findAll();
        for (com.example.demo.entity.Doctor d : docs) {
            if ("BS001".equals(d.getCode())) d.setDegree("Giáo sư");
            if ("BS002".equals(d.getCode())) d.setDegree("Phó giáo sư");
            if ("BS003".equals(d.getCode())) d.setDegree("Tiến sỹ");
            if ("BS004".equals(d.getCode())) d.setDegree("Thạc sỹ");
            if ("BS005".equals(d.getCode())) d.setDegree("Đại học");
            doctorRepository.save(d);
        }
    }

    private void seedUsers() {
        // 1. Admin
        com.example.demo.entity.User admin = new com.example.demo.entity.User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setFullname("Quản trị viên hệ thống");
        admin.setRole("Admin");
        admin.setStatus("Hoạt động");
        userRepository.save(admin);

        // 2. 5 Bác sĩ
        String[] degrees = {"Giáo sư", "Phó giáo sư", "Tiến sỹ", "Thạc sỹ", "Đại học"};
        for (int i = 1; i <= 5; i++) {
            com.example.demo.entity.User userDoc = new com.example.demo.entity.User();
            userDoc.setUsername("bacsi" + i);
            userDoc.setPassword("123456");
            userDoc.setFullname("Bác sĩ Nguyễn Văn " + (char) ('A' + i - 1));
            userDoc.setRole("Bác sĩ");
            userDoc.setStatus("Hoạt động");
            com.example.demo.entity.User savedUser = userRepository.save(userDoc);

            com.example.demo.entity.Doctor doc = new com.example.demo.entity.Doctor();
            doc.setCode(String.format("BS%03d", i));
            doc.setFullname(savedUser.getFullname());
            doc.setDateOfBirth(LocalDate.of(1980 + i, i, i));
            doc.setPhone("090" + i + "123456");
            doc.setEmail("bacsi" + i + "@nhakhoa.com");
            doc.setWorkplace("Phòng khám chính");
            doc.setDegree(degrees[i - 1]);
            doc.setRoom("Phòng " + (100 + i));
            doc.setUserId(savedUser.getId());
            doc.setStatus("Hoạt động");
            doctorRepository.save(doc);
        }

        // 3. 2 Lễ tân
        for (int i = 1; i <= 2; i++) {
            com.example.demo.entity.User userRec = new com.example.demo.entity.User();
            userRec.setUsername("letan" + i);
            userRec.setPassword("123456");
            userRec.setFullname("Lễ tân Trần Thị " + (char) ('A' + i - 1));
            userRec.setRole("Lễ tân");
            userRec.setStatus("Hoạt động");
            userRepository.save(userRec);
        }
    }

    private void seedSalaryConfig() {
        com.example.demo.entity.SalaryConfig config = new com.example.demo.entity.SalaryConfig();
        config.setBaseHourlyRate(300000.0);
        config.setWeekdayCoefficient(1.0);
        config.setWeekendCoefficient(1.5);
        salaryConfigRepository.save(config);
    }

    private void seedServices() {
        List<ServiceSeedData> seedDataList = new ArrayList<>();
        
        // Nhóm Khám
        seedDataList.add(new ServiceSeedData("Khám", "Khám răng miệng tổng quát", 100000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Khám", "Tư vấn nha khoa chuyên sâu", 150000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Khám", "Chụp X-quang cận chóp", 120000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Khám", "Chụp X-quang Panorama", 300000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Khám", "Chụp CT Cone Beam 3D", 800000.0, "Lần"));

        // Nhóm Điều trị
        seedDataList.add(new ServiceSeedData("Điều trị", "Cạo vôi răng cơ bản", 300000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Điều trị", "Trám răng Composite", 500000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Điều trị", "Điều trị tủy răng cửa", 1200000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Điều trị", "Nhổ răng sâu đơn giản", 700000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Điều trị", "Điều trị viêm nướu", 900000.0, "Trọn gói"));

        // Nhóm Thẩm mỹ
        seedDataList.add(new ServiceSeedData("Thẩm mỹ", "Tẩy trắng răng Laser", 2500000.0, "Trọn gói"));
        seedDataList.add(new ServiceSeedData("Thẩm mỹ", "Dán sứ Veneer", 7000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Thẩm mỹ", "Bọc răng sứ Titan", 3000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Thẩm mỹ", "Bọc răng sứ Zirconia", 6000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Thẩm mỹ", "Thiết kế nụ cười kỹ thuật số", 4000000.0, "Trọn gói"));

        // Nhóm Chỉnh nha
        seedDataList.add(new ServiceSeedData("Chỉnh nha", "Khám và lập phác đồ niềng răng", 500000.0, "Lần"));
        seedDataList.add(new ServiceSeedData("Chỉnh nha", "Niềng răng mắc cài kim loại", 30000000.0, "Trọn gói"));
        seedDataList.add(new ServiceSeedData("Chỉnh nha", "Niềng răng mắc cài sứ", 40000000.0, "Trọn gói"));
        seedDataList.add(new ServiceSeedData("Chỉnh nha", "Niềng răng tự buộc", 45000000.0, "Trọn gói"));
        seedDataList.add(new ServiceSeedData("Chỉnh nha", "Niềng răng trong suốt Invisalign", 80000000.0, "Trọn gói"));

        // Nhóm Phẫu thuật
        seedDataList.add(new ServiceSeedData("Phẫu thuật", "Tiểu phẫu nhổ răng khôn mọc thẳng", 2000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Phẫu thuật", "Tiểu phẫu nhổ răng khôn mọc lệch", 4000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Phẫu thuật", "Cấy ghép Implant Hàn Quốc", 15000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Phẫu thuật", "Cấy ghép Implant Mỹ", 25000000.0, "Răng"));
        seedDataList.add(new ServiceSeedData("Phẫu thuật", "Ghép xương hàm hỗ trợ Implant", 8000000.0, "Lần"));

        int codeCounter = 1;
        for (ServiceSeedData data : seedDataList) {
            DentalService service = new DentalService();
            service.setCode(String.format("DV%03d", codeCounter++));
            service.setName(data.name);
            service.setCategory(data.category);
            service.setUnit(data.unit);
            service.setStatus("Áp dụng");
            
            double coeff = 0.0;
            if ("Thẩm mỹ".equals(data.category)) coeff = 0.25;
            else if ("Chỉnh nha".equals(data.category) || "Phẫu thuật".equals(data.category)) coeff = 0.5;
            service.setBonusCoefficient(coeff);
            
            DentalService savedService = serviceRepository.save(service);

            ServicePrice price = new ServicePrice();
            price.setDentalService(savedService);
            price.setPrice(data.price);
            price.setEffectiveDate(LocalDate.now());
            price.setStatus("Đang áp dụng");
            
            priceRepository.save(price);
        }
    }

    private static class ServiceSeedData {
        String category;
        String name;
        Double price;
        String unit;

        public ServiceSeedData(String category, String name, Double price, String unit) {
            this.category = category;
            this.name = name;
            this.price = price;
            this.unit = unit;
        }
    }
}
