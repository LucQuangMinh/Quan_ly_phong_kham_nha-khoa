package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabasePatcher implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE patient_appointments ADD COLUMN service_id BIGINT");
            System.out.println("Added service_id column.");
        } catch (Exception e) {}
        
        try {
            jdbcTemplate.execute("ALTER TABLE patient_appointments ADD COLUMN service_name VARCHAR(255)");
            System.out.println("Added service_name column.");
        } catch (Exception e) {}
        
        try {
            jdbcTemplate.execute("ALTER TABLE patient_appointments ADD COLUMN quoted_price DOUBLE");
            System.out.println("Added quoted_price column.");
        } catch (Exception e) {}
    }
}
