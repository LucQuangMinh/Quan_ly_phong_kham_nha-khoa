package com.example.demo.controller;

import com.example.demo.entity.ReceptionistSchedule;
import com.example.demo.service.ReceptionistScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules/receptionists")
public class ReceptionistScheduleController {

    @Autowired
    private ReceptionistScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<?> getSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long userId,
            @RequestParam String role) {
        return ResponseEntity.ok(scheduleService.getSchedules(start, end, userId, role));
    }

    @PostMapping("/toggle-bulk")
    public ResponseEntity<?> toggleBulkReceptionists(@RequestBody Map<String, Object> body) {
        try {
            List<Number> rawIds = (List<Number>) body.get("userIds");
            if (rawIds == null || rawIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tích chọn ít nhất một Lễ tân từ danh sách bên phải để xếp ca trực!"));
            }
            List<Long> userIds = rawIds.stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
            LocalDate date = LocalDate.parse((String) body.get("date"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleBulkReceptionists(userIds, date, shiftType);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống: " + e.toString();
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @PostMapping("/toggle-self")
    public ResponseEntity<?> toggleReceptionistSelf(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            LocalDate date = LocalDate.parse((String) body.get("date"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleReceptionistSelf(userId, date, shiftType);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống: " + e.toString();
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @PostMapping("/toggle-week")
    public ResponseEntity<?> toggleWeek(@RequestBody Map<String, Object> body) {
        try {
            List<Number> rawIds = (List<Number>) body.get("userIds");
            if (rawIds == null || rawIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tích chọn ít nhất một Lễ tân từ danh sách bên phải để xếp ca trực!"));
            }
            List<Long> userIds = rawIds.stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
            LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
            String shiftType = (String) body.get("shiftType");
            
            scheduleService.toggleReceptionistWeek(userIds, startDate, shiftType);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống: " + e.toString();
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveProposal(@PathVariable Long id) {
        try {
            scheduleService.approveProposal(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống: " + e.toString();
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeReceptionistFromShift(@PathVariable Long id) {
        try {
            scheduleService.removeReceptionistFromShift(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống: " + e.toString();
            return ResponseEntity.badRequest().body(Map.of("message", msg));
        }
    }
}
