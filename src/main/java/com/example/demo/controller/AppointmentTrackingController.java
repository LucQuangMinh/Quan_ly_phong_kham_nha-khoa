package com.example.demo.controller;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.service.AppointmentTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trackings")
public class AppointmentTrackingController {

    @Autowired
    private AppointmentTrackingService trackingService;

    @GetMapping
    public ResponseEntity<?> getTrackings(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String headerRole,
            @RequestHeader(value = "X-User-Name", defaultValue = "") String username,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "role", required = false) String queryRole,
            @RequestParam(required = false) String doctorName) {
        
        try {
            headerRole = java.net.URLDecoder.decode(headerRole, java.nio.charset.StandardCharsets.UTF_8);
            username = java.net.URLDecoder.decode(username, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {}
        
        // Tomcat can mangle UTF-8 headers. Prefer queryRole if present (which is ASCII like "bac-si").
        String role = (queryRole != null && !queryRole.trim().isEmpty()) ? queryRole : headerRole;
        boolean isBenhNhan = "Bệnh nhân".equalsIgnoreCase(role) || "benh-nhan".equalsIgnoreCase(role);

        // Data Isolation Security: Do not trust query params for Patient.
        // Extract directly from simulated secure headers (Token equivalent)
        if (isBenhNhan) {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized token"));
            }
            return ResponseEntity.ok(trackingService.getTrackings(role, username, userId));
        }
        
        return ResponseEntity.ok(trackingService.getTrackings(role, doctorName, userId));
    }

    @PostMapping
    public ResponseEntity<?> createTracking(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @RequestBody AppointmentTracking tracking, 
            @RequestParam(required = false) Long doctorId) {
            
        try {
            role = java.net.URLDecoder.decode(role, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {}
        
        // Backend API Protection
        if ("Bệnh nhân".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden: Bệnh nhân không có quyền Thêm bản ghi."));
        }
        
        try {
            return ResponseEntity.ok(trackingService.createTracking(tracking, doctorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTracking(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
            @PathVariable Long id, 
            @RequestBody AppointmentTracking tracking) {
            
        try {
            role = java.net.URLDecoder.decode(role, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {}

        // Backend API Protection
        if ("Bệnh nhân".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden: Bệnh nhân không có quyền Sửa toàn bộ bản ghi."));
        }
        
        try {
            return ResponseEntity.ok(trackingService.updateTracking(id, tracking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            trackingService.updateStatus(id, payload.get("status"));
            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật trạng thái thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTracking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
            
        try {
            role = java.net.URLDecoder.decode(role, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {}
        
        // Backend API Protection
        if ("Bệnh nhân".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden: Bệnh nhân không có quyền Xóa bản ghi."));
        }
        
        try {
            trackingService.deleteTracking(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xóa bản ghi theo dõi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
