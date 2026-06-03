package com.example.demo.service;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.PatientAppointment;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PatientAppointmentService {

    @Autowired
    private PatientAppointmentRepository appointmentRepo;

    @Autowired
    private AppointmentTrackingRepository trackingRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public List<PatientAppointment> getAppointments(String role, String patientName) {
        if ("Bệnh nhân".equalsIgnoreCase(role) && patientName != null) {
            return appointmentRepo.findByPatientName(patientName);
        }
        return appointmentRepo.findAll();
    }

    @Transactional
    public PatientAppointment createAppointment(PatientAppointment appointment) {
        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("Đã đặt");
        }
        
        // Clash Validation
        if (Arrays.asList("Đã đặt", "Đã xác nhận").contains(appointment.getStatus()) && appointment.getDoctorId() != null) {
            boolean clash = appointmentRepo.existsByDoctorIdAndExaminationDateAndShiftTypeAndStatusIn(
                appointment.getDoctorId(), 
                appointment.getExaminationDate(), 
                appointment.getShiftType(), 
                Arrays.asList("Đã đặt", "Đã xác nhận")
            );
            if (clash) {
                throw new RuntimeException("Bác sĩ đã có lượt khám trùng ca trong ngày (lượt còn hiệu lực).");
            }
        }

        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setCode("PA-" + System.currentTimeMillis() % 100000);
        
        PatientAppointment saved = appointmentRepo.save(appointment);
        
        // Auto sync to UC2.5
        AppointmentTracking tracking = new AppointmentTracking();
        tracking.setAppointmentId(saved.getId());
        tracking.setTrackingCode("TD-" + saved.getCode());
        tracking.setPatientName(saved.getPatientName());
        tracking.setExaminationDate(saved.getExaminationDate());
        
        // Trạng thái khởi tạo đồng bộ
        if ("Đã đặt".equals(saved.getStatus())) {
            tracking.setStatus("Đang chờ");
        } else if ("Đã xác nhận".equals(saved.getStatus())) {
            tracking.setStatus("Đã tiếp nhận");
        } else if ("Hủy".equals(saved.getStatus())) {
            tracking.setStatus("Đã hủy");
        } else {
            tracking.setStatus("Đang chờ");
        }
        
        tracking.setUpdatedAt(LocalDateTime.now());
        
        if (saved.getDoctorId() != null) {
            Optional<Doctor> doc = doctorRepo.findById(saved.getDoctorId());
            if (doc.isPresent()) {
                tracking.setRoom(doc.get().getRoom());
            }
        }
        
        trackingRepo.save(tracking);
        return saved;
    }

    @Transactional
    public PatientAppointment updateAppointment(Long id, PatientAppointment newData) {
        PatientAppointment existing = appointmentRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));
            
        // State Machine validation
        String oldStatus = existing.getStatus();
        String newStatus = newData.getStatus() != null ? newData.getStatus() : oldStatus;
        
        boolean validStatus = false;
        if ("Đã đặt".equals(oldStatus)) {
            if (Arrays.asList("Đã đặt", "Đã xác nhận", "Hủy").contains(newStatus)) validStatus = true;
        } else if ("Đã xác nhận".equals(oldStatus)) {
            if (Arrays.asList("Đã xác nhận", "Hủy").contains(newStatus)) validStatus = true;
        } else if ("Hủy".equals(oldStatus)) {
            if ("Hủy".equals(newStatus)) validStatus = true;
        } else {
            if (oldStatus.equals(newStatus)) validStatus = true;
        }
        
        if (!validStatus) {
            throw new RuntimeException("Chuyển trạng thái không hợp lệ...");
        }
        
        // Clash validation if changing to active status or changing time/doctor
        if (Arrays.asList("Đã đặt", "Đã xác nhận").contains(newStatus) && newData.getDoctorId() != null) {
            boolean timeOrDoctorChanged = !newData.getDoctorId().equals(existing.getDoctorId()) ||
                                          !newData.getExaminationDate().equals(existing.getExaminationDate()) ||
                                          !newData.getShiftType().equals(existing.getShiftType());
            if (timeOrDoctorChanged) {
                boolean clash = appointmentRepo.existsByDoctorIdAndExaminationDateAndShiftTypeAndStatusIn(
                    newData.getDoctorId(), 
                    newData.getExaminationDate(), 
                    newData.getShiftType(), 
                    Arrays.asList("Đã đặt", "Đã xác nhận")
                );
                if (clash) {
                    throw new RuntimeException("Bác sĩ đã có lượt khám trùng ca trong ngày (lượt còn hiệu lực).");
                }
            }
        }
        
        existing.setDoctorId(newData.getDoctorId());
        existing.setRoom(newData.getRoom());
        existing.setExaminationDate(newData.getExaminationDate());
        existing.setShiftType(newData.getShiftType());
        existing.setNote(newData.getNote());
        existing.setStatus(newStatus);
        
        PatientAppointment saved = appointmentRepo.save(existing);
        
        // Sync update to UC2.5
        trackingRepo.findByAppointmentId(id).ifPresent(tracking -> {
            tracking.setExaminationDate(saved.getExaminationDate());
            if (saved.getDoctorId() != null) {
                Optional<Doctor> doc = doctorRepo.findById(saved.getDoctorId());
                if (doc.isPresent()) {
                    tracking.setRoom(doc.get().getRoom());
                }
            }
            if ("Hủy".equals(saved.getStatus())) {
                tracking.setStatus("Đã hủy");
            }
            tracking.setUpdatedAt(LocalDateTime.now());
            trackingRepo.save(tracking);
        });
        
        return saved;
    }

    @Transactional
    public void deleteAppointment(Long id) {
        PatientAppointment existing = appointmentRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));
            
        if ("Đã xác nhận".equals(existing.getStatus())) {
            throw new RuntimeException("Không xóa lượt đã xác nhận — dùng trạng thái Hủy theo quy trình.");
        }
        
        // Physical delete cascade to appointment_trackings
        trackingRepo.findByAppointmentId(id).ifPresent(tracking -> {
            trackingRepo.delete(tracking);
        });
        
        appointmentRepo.delete(existing);
    }
}
