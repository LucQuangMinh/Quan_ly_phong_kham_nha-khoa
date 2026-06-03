package com.example.demo.repository;

import com.example.demo.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByShiftDateBetween(LocalDate start, LocalDate end);
    
    List<DoctorSchedule> findByDoctor_IdAndShiftDateBetween(Long doctorId, LocalDate start, LocalDate end);
    
    Optional<DoctorSchedule> findByDoctor_IdAndShiftDateAndShiftType(Long doctorId, LocalDate date, String type);
    
    void deleteByDoctor_IdAndShiftDateAndShiftType(Long doctorId, LocalDate date, String type);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM doctor_schedules WHERE shift_date = ?1 AND shift_type = ?2", nativeQuery = true)
    void deleteByShiftDateAndShiftType(LocalDate date, String type);
}
