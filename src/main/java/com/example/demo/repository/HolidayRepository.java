package com.example.demo.repository;

import com.example.demo.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    
    Optional<Holiday> findByHolidayDate(LocalDate date);
    
    List<Holiday> findByHolidayDateBetween(LocalDate start, LocalDate end);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM shifts WHERE shift_date = ?1", nativeQuery = true)
    void deleteShiftsByDate(LocalDate date);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM doctor_schedules WHERE shift_date = ?1", nativeQuery = true)
    void deleteDoctorSchedulesByDate(LocalDate date);
}
