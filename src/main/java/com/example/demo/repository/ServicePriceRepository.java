package com.example.demo.repository;

import com.example.demo.entity.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, Long> {
    List<ServicePrice> findByDentalServiceId(Long serviceId);
    boolean existsByDentalServiceIdAndEffectiveDate(Long serviceId, LocalDate effectiveDate);
}
