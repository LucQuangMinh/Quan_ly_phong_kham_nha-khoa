package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.id NOT IN (SELECT d.userId FROM Doctor d WHERE d.userId IS NOT NULL)")
    List<User> findUsersByRoleAndNotAssignedToDoctor(@Param("role") String role);
}
