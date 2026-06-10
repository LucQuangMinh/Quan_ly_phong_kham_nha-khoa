package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salary_configs")
public class SalaryConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_hourly_rate")
    private Double baseHourlyRate;

    @Column(name = "weekday_coefficient")
    private Double weekdayCoefficient;

    @Column(name = "weekend_coefficient")
    private Double weekendCoefficient;
}
