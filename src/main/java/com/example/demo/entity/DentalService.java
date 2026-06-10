package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dental_services")
public class DentalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    private String unit;
    
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String status;

    @Column(name = "bonus_coefficient", columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double bonusCoefficient;
}
