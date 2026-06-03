package com.example.demo.controller;

import com.example.demo.dto.ServiceDTO;
import com.example.demo.service.DentalServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.security.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@PreAuthorize(roles = {"Admin", "Quản lý phòng khám"})
public class DentalServiceController {

    @Autowired
    private DentalServiceService dentalServiceService;

    @GetMapping
    @PreAuthorize(roles = {"Admin", "Quản lý phòng khám", "Lễ tân", "Bác sĩ", "Bệnh nhân"})
    public List<ServiceDTO> getAllServices() {
        return dentalServiceService.getAllServices();
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceDTO dto) {
        try {
            return ResponseEntity.ok(dentalServiceService.createService(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServiceDTO dto) {
        try {
            return ResponseEntity.ok(dentalServiceService.updateService(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateService(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dentalServiceService.changeStatus(id, "Ngưng áp dụng"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateService(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dentalServiceService.changeStatus(id, "Áp dụng"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}