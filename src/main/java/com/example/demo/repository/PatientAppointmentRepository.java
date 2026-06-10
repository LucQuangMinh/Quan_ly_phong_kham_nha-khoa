package com.example.demo.repository;

import com.example.demo.entity.PatientAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientAppointmentRepository extends JpaRepository<PatientAppointment, Long> {
    List<PatientAppointment> findByPatientName(String patientName);

    boolean existsByDoctorIdAndExaminationDateAndShiftTypeAndStatusIn(Long doctorId, LocalDate examinationDate, String shiftType, List<String> statuses);

    // Check-in search
    List<PatientAppointment> findByPatientPhoneAndExaminationDateGreaterThanEqualOrderByExaminationDateAsc(String phone, LocalDate date);
    List<PatientAppointment> findByPatientNameContainingIgnoreCaseAndExaminationDateGreaterThanEqualOrderByExaminationDateAsc(String name, LocalDate date);
    List<PatientAppointment> findByExaminationDateAndStatusIn(LocalDate date, List<String> statuses);

    @Query("SELECT SUM(p.quotedPrice) FROM PatientAppointment p WHERE p.examinationDate = :date AND p.status = :status")
    Double sumRevenueByDateAndStatus(@Param("date") LocalDate date, @Param("status") String status);

    @Query("SELECT COUNT(p) FROM PatientAppointment p WHERE p.examinationDate = :date AND p.status = :status")
    Long countByExaminationDateAndStatus(@Param("date") LocalDate date, @Param("status") String status);

    @Query("SELECT d.category, p.serviceName, SUM(p.quotedPrice) FROM PatientAppointment p LEFT JOIN DentalService d ON p.serviceId = d.id WHERE p.examinationDate = :date AND p.status = :status GROUP BY d.category, p.serviceName")
    List<Object[]> sumRevenueByCategoryAndService(@Param("date") LocalDate date, @Param("status") String status);

    // ── Salary calculation queries (UC4.4) ──────────────────────────────────

    /**
     * Tổng bonusCoefficient của các ca đã thanh toán theo bác sĩ + ngày.
     * Đây là Tổng_hệ_số_bệnh_nhân cho 1 ngày cụ thể.
     * Chỉ tính các ca có status = "Đã thanh toán" (ca đã được cashier chốt và admin duyệt).
     */
    @Query("SELECT COALESCE(SUM(d.bonusCoefficient), 0.0) " +
           "FROM PatientAppointment p " +
           "LEFT JOIN DentalService d ON p.serviceId = d.id " +
           "WHERE p.doctorId = :doctorId " +
           "AND p.examinationDate = :date " +
           "AND p.status = '\u0110\u00e3 thanh to\u00e1n'")
    Double sumPatientBonusCoefficientByDoctorAndDate(
        @Param("doctorId") Long doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Tổng bonusCoefficient tích lũy cả tháng theo bác sĩ.
     * Dùng cho phần tóm tắt tháng trong phiếu lương.
     */
    @Query("SELECT COALESCE(SUM(d.bonusCoefficient), 0.0) " +
           "FROM PatientAppointment p " +
           "LEFT JOIN DentalService d ON p.serviceId = d.id " +
           "WHERE p.doctorId = :doctorId " +
           "AND p.examinationDate BETWEEN :startDate AND :endDate " +
           "AND p.status = '\u0110\u00e3 thanh to\u00e1n'")
    Double sumPatientBonusCoefficientByDoctorAndMonth(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Lấy tất cả appointment đã thanh toán theo bác sĩ và khoảng ngày.
     * Dùng để tính chi tiết từng ngày.
     */
    @Query("SELECT p FROM PatientAppointment p " +
           "WHERE p.doctorId = :doctorId " +
           "AND p.examinationDate BETWEEN :startDate AND :endDate " +
           "AND p.status = '\u0110\u00e3 thanh to\u00e1n' " +
           "ORDER BY p.examinationDate ASC")
    List<PatientAppointment> findPaidByDoctorAndDateRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
