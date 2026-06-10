package com.example.demo.repository;

import com.example.demo.entity.CashierShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashierShiftRepository extends JpaRepository<CashierShift, Long> {
    List<CashierShift> findByShiftDateOrderByStartTimeDesc(LocalDate shiftDate);
    List<CashierShift> findByCashierNameAndShiftDate(String cashierName, LocalDate shiftDate);
    Optional<CashierShift> findTopByCashierNameAndShiftDateOrderByStartTimeDesc(String cashierName, LocalDate shiftDate);
    List<CashierShift> findByStatusIn(List<String> statuses);
}
