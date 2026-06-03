package com.example.demo.repository;

import com.example.demo.entity.AppointmentTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentTrackingRepository extends JpaRepository<AppointmentTracking, Long> {
    List<AppointmentTracking> findAllByOrderByExaminationDateAsc();
    List<AppointmentTracking> findByRoomContainingOrderByExaminationDateAsc(String room);
    List<AppointmentTracking> findByPatientNameOrderByExaminationDateAsc(String patientName);
    java.util.Optional<AppointmentTracking> findByAppointmentId(Long appointmentId);
}
