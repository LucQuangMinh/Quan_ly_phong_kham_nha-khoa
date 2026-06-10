package com.example.demo.controller;

import com.example.demo.entity.SalaryConfig;
import com.example.demo.repository.SalaryConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salary-config")
public class SalaryConfigController {

    @Autowired
    private SalaryConfigRepository configRepo;

    @Autowired
    private com.example.demo.repository.SalaryMultiplierDayRepository multiplierRepo;

    @GetMapping
    public ResponseEntity<?> getConfig() {
        List<SalaryConfig> configs = configRepo.findAll();
        if (configs.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Chưa có cấu hình"));
        }
        return ResponseEntity.ok(configs.get(0));
    }

    @PutMapping
    public ResponseEntity<?> updateConfig(@RequestBody SalaryConfig newConfig) {
        if (newConfig.getBaseHourlyRate() == null || newConfig.getBaseHourlyRate() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số tiền một giờ phải lớn hơn 0"));
        }

        List<SalaryConfig> configs = configRepo.findAll();
        SalaryConfig config = configs.isEmpty() ? new SalaryConfig() : configs.get(0);
        
        config.setBaseHourlyRate(newConfig.getBaseHourlyRate());
        config.setWeekdayCoefficient(1.0); // Cố định
        // config.setWeekendCoefficient(newConfig.getWeekendCoefficient()); // No longer used, handled by MultiplierDay
        
        configRepo.save(config);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật cấu hình lương thành công"));
    }

    @GetMapping("/multiplier-days")
    public ResponseEntity<?> getMultiplierDays(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(multiplierRepo.findByMonthAndYear(month, year));
    }

    @PostMapping("/multiplier-days/toggle")
    public ResponseEntity<?> toggleMultiplierDay(@RequestBody Map<String, String> payload) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(payload.get("date"));
            java.util.Optional<com.example.demo.entity.SalaryMultiplierDay> existing = multiplierRepo.findById(date);
            if (existing.isPresent()) {
                multiplierRepo.deleteById(date);
                return ResponseEntity.ok(Map.of("message", "Đã xóa hệ số cho ngày " + date));
            } else {
                com.example.demo.entity.SalaryMultiplierDay d = new com.example.demo.entity.SalaryMultiplierDay();
                d.setDate(date);
                d.setCoefficient(1.5);
                multiplierRepo.save(d);
                return ResponseEntity.ok(Map.of("message", "Đã thêm hệ số 1.5 cho ngày " + date));
            }
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
        }
    }
}
