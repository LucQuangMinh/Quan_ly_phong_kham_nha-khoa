package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailySalaryDetailDto {
    private LocalDate date;
    private boolean hasShift;
    private String shiftType;
    private Double shiftCoefficient;
    private Double patientCoefficient;
    private Double dailySalary;

    public DailySalaryDetailDto(LocalDate date, boolean hasShift) {
        this.date = date;
        this.hasShift = hasShift;
        this.shiftType = null;
        this.shiftCoefficient = 0.0;
        this.patientCoefficient = 0.0;
        this.dailySalary = 0.0;
    }
}
