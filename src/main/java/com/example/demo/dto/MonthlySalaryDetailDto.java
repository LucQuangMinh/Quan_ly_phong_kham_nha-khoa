package com.example.demo.dto;

import lombok.Data;

@Data
public class MonthlySalaryDetailDto {
    private int month;
    private int totalShifts;
    private Double totalPatientCoefficient;
    private Double monthlySalary;

    public MonthlySalaryDetailDto() {}

    public MonthlySalaryDetailDto(int month, int totalShifts, Double totalPatientCoefficient, Double monthlySalary) {
        this.month = month;
        this.totalShifts = totalShifts;
        this.totalPatientCoefficient = totalPatientCoefficient;
        this.monthlySalary = monthlySalary;
    }
}
