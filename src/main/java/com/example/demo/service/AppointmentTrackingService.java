package com.example.demo.service;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.PatientAppointment;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AppointmentTrackingService {

    @Autowired
    private AppointmentTrackingRepository trackingRepo;
    
    @Autowired
    private PatientAppointmentRepository appointmentRepo;
    
    @Autowired
    private DoctorRepository doctorRepo;

    public List<AppointmentTracking> getTrackings(String role, String filterName, Long userId) {
        // Tomcat might mangle Vietnamese headers, so check URL-safe variants too.
        boolean isBacSi = "Bác sĩ".equalsIgnoreCase(role) || "bac-si".equalsIgnoreCase(role) || "Bác sỹ".equalsIgnoreCase(role);
        boolean isAdminOrLeTan = "Admin".equalsIgnoreCase(role) || "Lễ tân".equalsIgnoreCase(role) || "le-tan".equalsIgnoreCase(role);

        if (isBacSi) {
            Doctor targetDoc = null;
            if (userId != null) {
                targetDoc = doctorRepo.findByUserId(userId).orElse(null);
            }
            
            // Fallback for missing userId linkage or older sessions
            if (targetDoc == null) {
                List<Doctor> allDocs = doctorRepo.findAll();
                for (Doctor d : allDocs) {
                    if (d.getFullname() != null && filterName != null) {
                        String dName = d.getFullname().toLowerCase().replace("bs. ", "").replace("bác sĩ ", "").trim();
                        String fName = filterName.toLowerCase().replace("bs. ", "").replace("bác sĩ ", "").trim();
                        if (dName.equals(fName) || dName.contains(fName) || fName.contains(dName)) {
                            targetDoc = d;
                            break;
                        }
                    }
                }
            }
            
            if (targetDoc != null && targetDoc.getRoom() != null && !targetDoc.getRoom().isEmpty()) {
                return trackingRepo.findByRoomContainingOrderByExaminationDateAsc(targetDoc.getRoom());
            }
            return new java.util.ArrayList<>();
        }
        
        if (isAdminOrLeTan) {
            return trackingRepo.findAllByOrderByExaminationDateAsc();
        }
        if ("Bệnh nhân".equalsIgnoreCase(role) || "benh-nhan".equalsIgnoreCase(role)) {
            return trackingRepo.findByPatientNameOrderByExaminationDateAsc(filterName);
        }
        return trackingRepo.findAllByOrderByExaminationDateAsc();
    }

    public AppointmentTracking createTracking(AppointmentTracking tracking, Long doctorId) {
        tracking.setStatus("Đang chờ");
        tracking.setTrackingCode("TD-2026-" + String.format("%03d", new Random().nextInt(1000)));
        if (tracking.getUpdatedAt() == null) {
            tracking.setUpdatedAt(LocalDateTime.now());
        }
        
        if (doctorId != null) {
            Optional<Doctor> doc = doctorRepo.findById(doctorId);
            if (doc.isPresent() && doc.get().getRoom() != null) {
                tracking.setRoom(doc.get().getRoom());
            }
        }
        
        return trackingRepo.save(tracking);
    }

    @org.springframework.transaction.annotation.Transactional
    public AppointmentTracking updateTracking(Long id, AppointmentTracking newTracking) {
        AppointmentTracking existing = trackingRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi theo dõi"));
            
        // THEODOI_NEXT State Machine validation
        String oldStatus = existing.getStatus();
        String newStatus = newTracking.getStatus();
        
        if (!oldStatus.equals(newStatus)) {
            List<String> allowedNext = getAllowedNextStates(oldStatus);
            if (!allowedNext.contains(newStatus)) {
                throw new RuntimeException("Chuyển trạng thái không hợp lệ từ '" + oldStatus + "' sang '" + newStatus + "'");
            }
            existing.setStatus(newStatus);
            existing.setUpdatedAt(LocalDateTime.now());
            
            // Sync with PatientAppointment (UC2.4)
            if (existing.getAppointmentId() != null) {
                appointmentRepo.findById(existing.getAppointmentId()).ifPresent(app -> {
                    if ("Đã khám".equals(newStatus)) {
                        app.setStatus("Khám xong");
                    } else if ("Đang chờ".equals(newStatus)) {
                        app.setStatus("Đã đặt");
                    } else if ("Đang khám".equals(newStatus)) {
                        app.setStatus("Đã xác nhận");
                    } else if ("Đã hủy".equals(newStatus) || "Bác sĩ từ chối".equals(newStatus)) {
                        app.setStatus("Hủy");
                    }
                    appointmentRepo.save(app);
                });
            }
        }
        
        existing.setPatientName(newTracking.getPatientName());
        existing.setExaminationDate(newTracking.getExaminationDate());
        if (newTracking.getRoom() != null) {
            existing.setRoom(newTracking.getRoom());
        }
        existing.setNote(newTracking.getNote());
        
        return trackingRepo.save(existing);
    }
    
    // Add specific function for quick status update by Doctor
    public void updateStatus(Long id, String newStatus) {
        AppointmentTracking tracking = trackingRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi"));
        AppointmentTracking dummy = new AppointmentTracking();
        dummy.setStatus(newStatus);
        dummy.setPatientName(tracking.getPatientName());
        dummy.setExaminationDate(tracking.getExaminationDate());
        dummy.setRoom(tracking.getRoom());
        dummy.setNote(tracking.getNote());
        updateTracking(id, dummy);
    }
    
    private List<String> getAllowedNextStates(String currentStatus) {
        switch (currentStatus) {
            case "Đang chờ": return Arrays.asList("Đang chờ", "Đang khám", "Bác sĩ từ chối");
            case "Đang khám": return Arrays.asList("Đang khám", "Đã khám", "Vắng");
            case "Đã khám": return Arrays.asList("Đã khám");
            case "Vắng": return Arrays.asList("Vắng");
            default: return Arrays.asList(currentStatus); // Fallback
        }
    }

    public void deleteTracking(Long id) {
        AppointmentTracking existing = trackingRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi"));
            
        // Validation to protect medical records
        if ("Đang khám".equals(existing.getStatus())) {
            throw new RuntimeException("Không xóa lượt đang khám.");
        }
        if ("Đã khám".equals(existing.getStatus())) {
            throw new RuntimeException("Không xóa lượt đã khám (hồ sơ đã khóa).");
        }
        
        // Sync with PatientAppointment (UC2.4) to mark as Canceled
        if (existing.getAppointmentId() != null) {
            appointmentRepo.findById(existing.getAppointmentId()).ifPresent(app -> {
                app.setStatus("Hủy");
                appointmentRepo.save(app);
            });
        }
        
        trackingRepo.deleteById(id);
    }
}
