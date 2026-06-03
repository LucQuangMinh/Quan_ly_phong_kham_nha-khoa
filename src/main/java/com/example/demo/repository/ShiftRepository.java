package com.example.demo.repository;

import com.example.demo.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByShiftDateBetween(LocalDate start, LocalDate end);
    
    Optional<Shift> findByShiftDateAndShiftType(LocalDate date, String type);
    
    void deleteByShiftDateAndShiftType(LocalDate date, String type);
}
