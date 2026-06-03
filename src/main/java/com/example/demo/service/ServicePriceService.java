package com.example.demo.service;

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
