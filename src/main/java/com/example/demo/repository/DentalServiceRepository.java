package com.example.demo.repository;

import com.example.demo.entity.DentalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<DentalService> findTopByOrderByIdDesc();
}
