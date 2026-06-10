package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cashier_shifts")
public class CashierShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cashier_name")
    private String cashierName;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // ACTIVE, PENDING, CLOSED
    private String status;

    @Column(name = "theoretical_cash")
    private Double theoreticalCash;

    @Column(name = "counted_cash")
    private Double countedCash;

    @Column(name = "discrepancy_reason", columnDefinition = "TEXT")
    private String discrepancyReason;
}
