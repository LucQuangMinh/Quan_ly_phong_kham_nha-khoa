package com.example.demo.controller;

import com.example.demo.entity.PatientAppointment;
import com.example.demo.service.PatientAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patient-appointments")
public class PatientAppointmentController {

    @Autowired
    private PatientAppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<?> getAllAppointments(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String headerRole,
            @RequestParam(value = "role", required = false) String queryRole,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String patientName) {
            
        String role = (queryRole != null && !queryRole.trim().isEmpty()) ? queryRole : headerRole;
        boolean isAdminOrLeTan = "Admin".equalsIgnoreCase(role) || "Lễ tân".equalsIgnoreCase(role) || "le-tan".equalsIgnoreCase(role);
        boolean isBacSi = "Bác sĩ".equalsIgnoreCase(role) || "bac-si".equalsIgnoreCase(role);
        boolean isBenhNhan = "Bệnh nhân".equalsIgnoreCase(role) || "benh-nhan".equalsIgnoreCase(role);

        if (isAdminOrLeTan || isBacSi) {
            return ResponseEntity.ok(appointmentService.getAppointments(role, patientName));
        }
        return ResponseEntity.ok(appointmentService.getAppointments(role, patientName));
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String headerRole,
            @RequestParam(value = "role", required = false) String queryRole,
            @RequestBody PatientAppointment appointment) {
        
        String role = (queryRole != null && !queryRole.trim().isEmpty()) ? queryRole : headerRole;
        
        if ("Admin".equalsIgnoreCase(role) || "Lễ tân".equalsIgnoreCase(role) || "le-tan".equalsIgnoreCase(role)) {
            appointment.setSource("Tại quầy");
        } else {
            appointment.setSource("Trực tuyến");
        }
        try {
            return ResponseEntity.ok(appointmentService.createAppointment(appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody PatientAppointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.updateAppointment(id, appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hủy lịch khám"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
