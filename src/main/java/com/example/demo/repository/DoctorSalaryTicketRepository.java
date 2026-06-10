package com.example.demo.repository;

import com.example.demo.entity.DoctorSalaryTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorSalaryTicketRepository extends JpaRepository<DoctorSalaryTicket, Long> {
    Optional<DoctorSalaryTicket> findByDoctorIdAndMonthAndYear(Long doctorId, int month, int year);
}
