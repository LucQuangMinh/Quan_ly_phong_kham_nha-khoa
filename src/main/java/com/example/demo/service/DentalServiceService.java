package com.example.demo.service;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.entity.DentalService;
import com.example.demo.entity.ServicePrice;
import com.example.demo.repository.DentalServiceRepository;
import com.example.demo.repository.ServicePriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DentalServiceService {

    @Autowired
    private DentalServiceRepository serviceRepository;

    @Autowired
    private ServicePriceRepository priceRepository;

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(Long id) {
        DentalService ds = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        return convertToDTO(ds);
    }

    public ServiceDTO convertToDTO(DentalService ds) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(ds.getId());
        dto.setCode(ds.getCode());
        dto.setName(ds.getName());
        dto.setUnit(ds.getUnit());
        dto.setCategory(ds.getCategory());
        dto.setDescription(ds.getDescription());
        dto.setStatus(ds.getStatus());

        List<ServicePrice> prices = priceRepository.findByDentalServiceId(ds.getId());
        ServicePrice activePrice = prices.stream()
                .filter(p -> "Đang áp dụng".equals(p.getStatus()))
                .findFirst().orElse(null);

        if (activePrice != null) {
            dto.setPrice(activePrice.getPrice());
        }
        return dto;
    }

    @Transactional
    public ServiceDTO createService(ServiceDTO dto) {
        DentalService service = new DentalService();
        service.setName(dto.getName());
        service.setUnit(dto.getUnit());
        service.setCategory(dto.getCategory());
        service.setDescription(dto.getDescription());
        
        // Tự động sinh mã dịch vụ
        serviceRepository.findTopByOrderByIdDesc().ifPresentOrElse(
            latest -> {
                String latestCode = latest.getCode();
                try {
                    int nextNum = Integer.parseInt(latestCode.substring(2)) + 1;
                    service.setCode(String.format("DV%03d", nextNum));
                } catch (Exception e) {
                    service.setCode("DV001");
                }
            },
            () -> service.setCode("DV001")
        );

        validateServiceData(service, null);
        service.setStatus("Áp dụng");
        
        DentalService savedService = serviceRepository.save(service);

        if (dto.getPrice() != null && dto.getPrice() > 0) {
            ServicePrice price = new ServicePrice();
            price.setDentalService(savedService);
            price.setPrice(dto.getPrice());
            price.setEffectiveDate(LocalDate.now());
            price.setStatus("Đang áp dụng");
            priceRepository.save(price);
        }

        return convertToDTO(savedService);
    }

    @Transactional
    public ServiceDTO updateService(Long id, ServiceDTO dto) {
        DentalService existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        
        // Cập nhật thông tin dịch vụ (KHÔNG CẬP NHẬT MÃ CODE)
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setDescription(dto.getDescription());
        existing.setUnit(dto.getUnit());
        
        validateServiceData(existing, id);
        DentalService savedService = serviceRepository.save(existing);

        if (dto.getPrice() != null && dto.getPrice() > 0) {
            List<ServicePrice> prices = priceRepository.findByDentalServiceId(id);
            ServicePrice activePrice = prices.stream()
                    .filter(p -> "Đang áp dụng".equals(p.getStatus()))
                    .findFirst().orElse(null);

            if (activePrice == null || !activePrice.getPrice().equals(dto.getPrice())) {
                if (activePrice != null) {
                    activePrice.setStatus("Ngưng áp dụng");
                    priceRepository.save(activePrice);
                }
                ServicePrice newPrice = new ServicePrice();
                newPrice.setDentalService(savedService);
                newPrice.setPrice(dto.getPrice());
                newPrice.setEffectiveDate(LocalDate.now());
                newPrice.setStatus("Đang áp dụng");
                priceRepository.save(newPrice);
            }
        }

        return convertToDTO(savedService);
    }

    public ServiceDTO changeStatus(Long id, String status) {
        DentalService existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ"));
        existing.setStatus(status);
        return convertToDTO(serviceRepository.save(existing));
    }

    private void validateServiceData(DentalService service, Long id) {
        if (service.getCode() == null || service.getCode().trim().isEmpty()) {
            throw new RuntimeException("Mã dịch vụ không được để trống");
        }
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên dịch vụ không được để trống");
        }
        if (service.getCategory() == null || service.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn nhóm dịch vụ");
        }
    }
}
