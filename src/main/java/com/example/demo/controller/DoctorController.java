package com.example.demo.controller;

import com.example.demo.entity.Doctor;
import com.example.demo.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.demo.security.PreAuthorize;

@RestController
@RequestMapping("/api/doctors")
@PreAuthorize(roles = {"Admin", "Quản lý phòng khám", "Kế toán"})
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    @PreAuthorize(roles = {"Admin", "Quản lý phòng khám", "Kế toán", "Lễ tân", "Bác sĩ", "Bệnh nhân"})
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @PostMapping
    public ResponseEntity<?> createDoctor(@RequestBody Doctor doctor) {
        try {
            return ResponseEntity.ok(doctorService.createDoctor(doctor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor details) {
        try {
            return ResponseEntity.ok(doctorService.updateDoctor(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateDoctor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(doctorService.changeStatus(id, "Ngưng hoạt động"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateDoctor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(doctorService.changeStatus(id, "Đang làm việc"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/assign-user")
    public ResponseEntity<?> assignUser(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            return ResponseEntity.ok(doctorService.assignUser(id, body.get("userId")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
