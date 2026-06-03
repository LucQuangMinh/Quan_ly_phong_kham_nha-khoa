package com.example.demo.controller;

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
