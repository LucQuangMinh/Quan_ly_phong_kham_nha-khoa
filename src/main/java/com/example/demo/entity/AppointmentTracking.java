package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "appointment_trackings")
public class AppointmentTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_code")
    private String trackingCode;
    
    @Column(name = "appointment_id")
    private Long appointmentId;
    
    @Column(name = "patient_name")
    private String patientName;
    
    @Column(name = "examination_date")
    private LocalDate examinationDate;
    
    private String room;
    
    @Column(name = "doctor_name")
    private String doctorName;
    
    private String status;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
    
    private String note;
}
