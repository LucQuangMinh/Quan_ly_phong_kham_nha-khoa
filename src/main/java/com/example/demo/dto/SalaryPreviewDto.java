package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SalaryPreviewDto {
    private Long doctorId;
    private String doctorName;
    private int month;
    private int year;
    
    private Double baseHourlyRate;
    private Double degreeCoefficient;
    
    private int totalShifts;
    private int daysWithCoeff1;
    private int daysWithCoeff15;
    
    private Double totalPatientCoefficient;
    private Double totalSalary;
    
    private List<DailySalaryDetailDto> dailyDetails;
}
