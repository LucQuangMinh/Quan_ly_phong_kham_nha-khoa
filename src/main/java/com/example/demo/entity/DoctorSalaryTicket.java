package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "doctor_salary_tickets")
public class DoctorSalaryTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "salary_month")
    private Integer month;

    @Column(name = "salary_year")
    private Integer year;

    // --- SNAPSHOT HẰNG SỐ ---
    @Column(name = "base_hourly_rate")
    private Double baseHourlyRate;

    @Column(name = "degree_coefficient")
    private Double degreeCoefficient;

    @Column(name = "weekend_coefficient")
    private Double weekendCoefficient;

    // --- DỮ LIỆU CHẤM CÔNG (THEO CA TRỰC) ---
    @Column(name = "total_standard_hours")
    private Double totalStandardHours; // Tổng giờ ca hành chính

    @Column(name = "total_weekend_hours")
    private Double totalWeekendHours; // Tổng giờ ca cuối tuần

    // --- DỮ LIỆU CA BỆNH (KPI THƯỞNG TỰ ĐỘNG THEO NHÓM DỊCH VỤ) ---
    @Column(name = "basic_cases_count")
    private Integer basicCasesCount; // Khám, Điều trị (Hệ số 0)

    @Column(name = "aesthetic_cases_count")
    private Integer aestheticCasesCount; // Thẩm mỹ (Hệ số 0.25)

    @Column(name = "surgery_cases_count")
    private Integer surgeryCasesCount; // Chỉnh nha, Phẫu thuật (Hệ số 0.5)

    @Column(name = "total_bonus")
    private Double totalBonus; // Tổng tiền thưởng từ các ca bệnh

    // --- TỔNG KẾT ---
    @Column(name = "total_salary")
    private Double totalSalary;

    @Column(name = "status")
    private String status; // DRAFT, PAID

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "DRAFT";
        }
    }
}
