package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "salary_multiplier_days")
public class SalaryMultiplierDay {
    
    @Id
    @Column(name = "config_date")
    private LocalDate date;

    @Column(name = "coefficient", nullable = false)
    private Double coefficient;
}
