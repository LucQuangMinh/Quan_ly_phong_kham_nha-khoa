package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String fullname;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String workplace;
    private String degree;
    private String room;

    @Column(name = "user_id")
    private Long userId;

    private String status;
}
