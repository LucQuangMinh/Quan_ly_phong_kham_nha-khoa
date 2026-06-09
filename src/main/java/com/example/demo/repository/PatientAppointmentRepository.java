package com.example.demo.repository;

import com.example.demo.entity.PatientAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
