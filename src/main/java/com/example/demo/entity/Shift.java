package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Column(name = "shift_type")
    private String shiftType; // "Sáng", "Chiều", "Tối"

    private String status; // "Mở"
}
