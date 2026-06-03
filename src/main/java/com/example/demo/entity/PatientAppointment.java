package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "patient_appointments")
public class PatientAppointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    
    @Column(name = "patient_name")
    private String patientName;
    
    @Column(name = "patient_phone")
    private String patientPhone;
    
    @Column(name = "examination_date")
    private LocalDate examinationDate;
    
    @Column(name = "shift_type")
    private String shiftType;
    
    @Column(name = "doctor_id")
    private Long doctorId;
    
    private String room;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column(name = "service_name")
    private String serviceName;
    
    @Column(name = "quoted_price")
    private Double quotedPrice;
    
    @Column(name = "expected_order")
    private Integer expectedOrder;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    private String source;
    private String priority;
    private String status;
    private String note;
}
