package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "receptionist_schedules")
public class ReceptionistSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User receptionist;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Column(name = "shift_type")
    private String shiftType; // "Sáng", "Chiều", "Tối"

    private String status; // "Đề xuất của tôi", "Đã duyệt trực"
}
