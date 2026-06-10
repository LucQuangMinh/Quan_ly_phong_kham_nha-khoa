package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class SalaryYearPreviewDto {
    private Long doctorId;
    private String doctorName;
    private int year;
    
    private Double baseHourlyRate;
    private Double degreeCoefficient;
    
    private int totalYearlyShifts;
    private int totalDaysWithCoeff1;
    private int totalDaysWithCoeff15;
    
    private Double totalYearlyPatientCoefficient;
    private Double totalYearlySalary;
    
    private List<MonthlySalaryDetailDto> monthlyDetails;
}
