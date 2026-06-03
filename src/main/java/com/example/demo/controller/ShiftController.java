package com.example.demo.controller;

import com.example.demo.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public ResponseEntity<?> getShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(shiftService.getShifts(start, end));
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleShift(@RequestBody Map<String, Object> body) {
        try {
            LocalDate date = LocalDate.parse((String) body.get("date"));
            String shiftType = (String) body.get("shiftType");
            
            shiftService.toggleShift(date, shiftType);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/toggle-week")
    public ResponseEntity<?> toggleWeeklyShift(@RequestBody Map<String, Object> body) {
        try {
            LocalDate startDate = LocalDate.parse((String) body.get("startDate"));
            String shiftType = (String) body.get("shiftType");
            
            Map<String, Object> response = shiftService.toggleWeeklyShift(startDate, shiftType);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
