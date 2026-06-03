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
