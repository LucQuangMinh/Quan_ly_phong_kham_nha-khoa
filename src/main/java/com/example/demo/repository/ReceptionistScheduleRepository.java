package com.example.demo.repository;

import com.example.demo.entity.ReceptionistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceptionistScheduleRepository extends JpaRepository<ReceptionistSchedule, Long> {
    List<ReceptionistSchedule> findByShiftDateBetween(LocalDate start, LocalDate end);
    List<ReceptionistSchedule> findByReceptionist_IdAndShiftDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<ReceptionistSchedule> findByReceptionist_IdAndShiftDateAndShiftType(Long userId, LocalDate date, String type);
    List<ReceptionistSchedule> findByShiftDateAndShiftType(LocalDate date, String type);
}
