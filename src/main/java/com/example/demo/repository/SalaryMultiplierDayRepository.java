package com.example.demo.repository;

import com.example.demo.entity.SalaryMultiplierDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryMultiplierDayRepository extends JpaRepository<SalaryMultiplierDay, LocalDate> {
    
    @Query("SELECT d FROM SalaryMultiplierDay d WHERE MONTH(d.date) = :month AND YEAR(d.date) = :year")
    List<SalaryMultiplierDay> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
