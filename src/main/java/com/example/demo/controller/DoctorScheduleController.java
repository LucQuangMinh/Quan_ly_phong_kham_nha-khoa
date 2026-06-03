package com.example.demo.controller;

import com.example.demo.entity.DoctorSchedule;
import com.example.demo.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class DoctorScheduleController {

    @Autowired
    private DoctorScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<?> getSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long doctorId,
            @RequestParam String role) {
        return ResponseEntity.ok(scheduleService.getSchedules(start, end, doctorId, role));
    }

    @PostMapping("/toggle-bulk")
    public ResponseEntity<?> toggleBulkDoctors(@RequestBody Map<String, Object> body) {
        try {
            List<Number> rawIds = (List<Number>) body.get("doctorIds");
            if (rawIds == null || rawIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tích chọn ít nhất một bác sĩ từ danh sách bên phải để xếp ca trực!"));
            }
            List<Long> doctorIds = rawIds.stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
            LocalDate date = LocalDate.parse((String) body.get("date"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleBulkDoctors(doctorIds, date, shiftType);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/toggle-self")
    public ResponseEntity<?> toggleDoctorSelf(@RequestBody Map<String, Object> body) {
        try {
            Long doctorId = Long.valueOf(body.get("doctorId").toString());
            LocalDate date = LocalDate.parse((String) body.get("date"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleDoctorSelf(doctorId, date, shiftType);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/toggle-week")
    public ResponseEntity<?> toggleWeek(@RequestBody Map<String, Object> body) {
        try {
            List<Number> rawIds = (List<Number>) body.get("doctorIds");
            if (rawIds == null || rawIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tích chọn ít nhất một bác sĩ từ danh sách bên phải để xếp ca trực!"));
            }
            List<Long> doctorIds = rawIds.stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
            LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleDoctorWeek(doctorIds, startDate, shiftType);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveProposal(@PathVariable Long id) {
        try {
            scheduleService.approveProposal(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeDoctorFromShift(@PathVariable Long id) {
        try {
            scheduleService.removeDoctorFromShift(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
