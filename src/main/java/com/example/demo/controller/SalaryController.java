package com.example.demo.controller;

import com.example.demo.dto.SalaryPreviewDto;
import com.example.demo.dto.SalaryYearPreviewDto;
import com.example.demo.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @GetMapping("/preview")
    public ResponseEntity<SalaryPreviewDto> getSalaryPreview(
            @RequestParam Long doctorId,
            @RequestParam int month,
            @RequestParam int year) {
        try {
            SalaryPreviewDto result = salaryService.calculateSalaryPreview(doctorId, month, year);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/all")
    public ResponseEntity<List<SalaryPreviewDto>> getAllSalariesPreview(
            @RequestParam int month,
            @RequestParam int year) {
        try {
            List<SalaryPreviewDto> results = salaryService.calculateAllSalariesPreview(month, year);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/year")
    public ResponseEntity<SalaryYearPreviewDto> getYearlySalaryPreview(
            @RequestParam Long doctorId,
            @RequestParam int year) {
        try {
            SalaryYearPreviewDto result = salaryService.calculateYearlySalaryPreview(doctorId, year);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/year/all")
    public ResponseEntity<List<SalaryYearPreviewDto>> getAllYearlySalariesPreview(
            @RequestParam int year) {
        try {
            List<SalaryYearPreviewDto> results = salaryService.calculateAllYearlySalariesPreview(year);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
