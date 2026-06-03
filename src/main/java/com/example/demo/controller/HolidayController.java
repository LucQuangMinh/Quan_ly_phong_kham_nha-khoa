package com.example.demo.controller;

import com.example.demo.entity.Holiday;
import com.example.demo.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public List<Holiday> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHolidayById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(holidayService.getHolidayById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createHoliday(@RequestBody Holiday holiday) {
        try {
            return ResponseEntity.ok(holidayService.createHoliday(holiday));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHoliday(@PathVariable Long id, @RequestBody Holiday details) {
        try {
            return ResponseEntity.ok(holidayService.updateHoliday(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoliday(@PathVariable Long id) {
        try {
            holidayService.deleteHoliday(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleHoliday(@RequestBody Map<String, String> body) {
        try {
            LocalDate date = LocalDate.parse(body.get("date"));
            Holiday result = holidayService.toggleHoliday(date);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/toggle-week")
    public ResponseEntity<?> toggleWeek(@RequestBody Map<String, String> body) {
        try {
            LocalDate startDate = LocalDate.parse(body.get("startDate"));
            holidayService.toggleWeek(startDate);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
