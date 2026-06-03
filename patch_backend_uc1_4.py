import os
import re

base_dir = r"d:\Đánh giá và kiểm định\demo\demo"

# 1. Update ServicePrice.java
sp_file = os.path.join(base_dir, "src/main/java/com/example/demo/entity/ServicePrice.java")
with open(sp_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove BigDecimal import if unused
content = content.replace("import java.math.BigDecimal;", "")
content = content.replace("@Column(name = \"service_id\")", "@ManyToOne\n    @JoinColumn(name = \"service_id\")")
content = content.replace("private Long serviceId;", "private DentalService dentalService;")
content = content.replace("private BigDecimal price;", "private Double price;")
content = content.replace("@Column(name = \"start_date\")\n    private LocalDate startDate;\n\n    @Column(name = \"end_date\")\n    private LocalDate endDate;", "@Column(name = \"effective_date\")\n    private LocalDate effectiveDate;")

with open(sp_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 2. Update ServicePriceRepository.java
spr_file = os.path.join(base_dir, "src/main/java/com/example/demo/repository/ServicePriceRepository.java")
with open(spr_file, 'r', encoding='utf-8') as f:
    content = f.read()

if "import java.time.LocalDate;" not in content:
    content = content.replace("import java.util.List;", "import java.util.List;\nimport java.time.LocalDate;")

content = content.replace("findByServiceId(Long serviceId)", "findByDentalServiceId(Long serviceId)")
if "existsByDentalServiceIdAndEffectiveDate" not in content:
    content = content.replace("List<ServicePrice> findByDentalServiceId(Long serviceId);", "List<ServicePrice> findByDentalServiceId(Long serviceId);\n    boolean existsByDentalServiceIdAndEffectiveDate(Long serviceId, LocalDate effectiveDate);")

with open(spr_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 3. Create ServicePriceService.java
sps_file = os.path.join(base_dir, "src/main/java/com/example/demo/service/ServicePriceService.java")
sps_content = """package com.example.demo.service;

import com.example.demo.entity.ServicePrice;
import com.example.demo.entity.DentalService;
import com.example.demo.repository.ServicePriceRepository;
import com.example.demo.repository.DentalServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ServicePriceService {

    @Autowired
    private ServicePriceRepository priceRepository;

    @Autowired
    private DentalServiceRepository dentalServiceRepository;

    public List<ServicePrice> getAllPrices() {
        return priceRepository.findAll();
    }
    
    public List<ServicePrice> getPricesByService(Long serviceId) {
        return priceRepository.findByDentalServiceId(serviceId);
    }

    public ServicePrice createPrice(ServicePrice price) {
        validatePrice(price);
        
        if (priceRepository.existsByDentalServiceIdAndEffectiveDate(price.getDentalService().getId(), price.getEffectiveDate())) {
            throw new RuntimeException("Dịch vụ đã có giá áp dụng tại ngày này");
        }
        
        price.setStatus("Đang áp dụng");
        return priceRepository.save(price);
    }

    @Transactional
    public ServicePrice updatePrice(Long id, ServicePrice details) {
        ServicePrice oldPrice = priceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giá"));
                
        validatePrice(details);
        
        // Cập nhật bản ghi giá cũ thành Ngưng áp dụng
        oldPrice.setStatus("Ngưng áp dụng");
        priceRepository.save(oldPrice);
        
        // Tạo bản ghi hoàn toàn mới
        ServicePrice newPrice = new ServicePrice();
        newPrice.setDentalService(oldPrice.getDentalService()); // Giữ nguyên dịch vụ gốc
        newPrice.setPrice(details.getPrice());
        newPrice.setEffectiveDate(details.getEffectiveDate());
        newPrice.setStatus("Đang áp dụng");
        
        return priceRepository.save(newPrice);
    }

    public ServicePrice deactivatePrice(Long id) {
        ServicePrice price = priceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giá"));
        price.setStatus("Ngưng áp dụng");
        return priceRepository.save(price);
    }

    private void validatePrice(ServicePrice price) {
        if (price.getDentalService() == null || price.getDentalService().getId() == null) {
            throw new RuntimeException("Vui lòng chọn dịch vụ");
        }
        
        DentalService dentalService = dentalServiceRepository.findById(price.getDentalService().getId())
                .orElseThrow(() -> new RuntimeException("Dịch vụ gốc không tồn tại"));
                
        if (!"Áp dụng".equals(dentalService.getStatus())) {
            throw new RuntimeException("Không thể thiết lập giá cho dịch vụ ngưng áp dụng");
        }
        
        // Đảm bảo entity map đúng
        price.setDentalService(dentalService);
        
        if (price.getPrice() == null || price.getPrice() <= 0) {
            throw new RuntimeException("Đơn giá phải lớn hơn 0");
        }
        
        if (price.getEffectiveDate() == null) {
            throw new RuntimeException("Ngày áp dụng không được để trống");
        }
    }
}
"""
with open(sps_file, 'w', encoding='utf-8') as f:
    f.write(sps_content)

# 4. Update DentalServiceController.java (remove prices endpoints)
dsc_file = os.path.join(base_dir, "src/main/java/com/example/demo/controller/DentalServiceController.java")
with open(dsc_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove everything from // --- PRICES --- to the end of the class (before the last closing brace)
if "// --- PRICES ---" in content:
    # Use regex to strip it
    content = re.sub(r"// --- PRICES ---.*", "}", content, flags=re.DOTALL)
    # Also clean up ServicePriceRepository and ServicePrice imports
    content = content.replace("import com.example.demo.entity.ServicePrice;", "")
    content = content.replace("import com.example.demo.repository.ServicePriceRepository;", "")
    content = re.sub(r"@Autowired\s*private ServicePriceRepository priceRepository;", "", content)

with open(dsc_file, 'w', encoding='utf-8') as f:
    f.write(content)

# 5. Create ServicePriceController.java
spc_file = os.path.join(base_dir, "src/main/java/com/example/demo/controller/ServicePriceController.java")
spc_content = """package com.example.demo.controller;

import com.example.demo.entity.ServicePrice;
import com.example.demo.service.ServicePriceService;
import com.example.demo.security.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prices")
@PreAuthorize(roles = {"Admin", "Quản lý phòng khám"})
public class ServicePriceController {

    @Autowired
    private ServicePriceService servicePriceService;

    @GetMapping
    public List<ServicePrice> getAllPrices() {
        return servicePriceService.getAllPrices();
    }
    
    @GetMapping("/service/{serviceId}")
    public List<ServicePrice> getPricesByService(@PathVariable Long serviceId) {
        return servicePriceService.getPricesByService(serviceId);
    }

    @PostMapping
    public ResponseEntity<?> createPrice(@RequestBody ServicePrice price) {
        try {
            return ResponseEntity.ok(servicePriceService.createPrice(price));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody ServicePrice details) {
        try {
            return ResponseEntity.ok(servicePriceService.updatePrice(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivatePrice(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicePriceService.deactivatePrice(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
"""
with open(spc_file, 'w', encoding='utf-8') as f:
    f.write(spc_content)

print("Backend patch completed successfully!")
