package com.example.demo.repository;

import com.example.demo.entity.SalaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, Long> {
}
