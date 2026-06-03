package com.example.demo.service;

import com.example.demo.entity.Doctor;
import com.example.demo.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));
    }

    public Doctor createDoctor(Doctor doctor) {
        validateDoctorData(doctor, null);
        
        if (doctor.getStatus() == null) {
            doctor.setStatus("Đang làm việc"); // Use exactly this based on user requirement
        }
        
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor details) {
        Doctor existing = getDoctorById(id);
        
        validateDoctorData(details, id);
        
        existing.setCode(details.getCode());
        existing.setFullname(details.getFullname());
        existing.setDateOfBirth(details.getDateOfBirth());
        existing.setPhone(details.getPhone());
        existing.setEmail(details.getEmail());
        existing.setWorkplace(details.getWorkplace());
        existing.setDegree(details.getDegree());
        
        // Also update userId if provided in the update form
        if (details.getUserId() != null) {
            existing.setUserId(details.getUserId());
        }
        
        return doctorRepository.save(existing);
    }

    public Doctor assignUser(Long doctorId, Long userId) {
        Doctor existing = getDoctorById(doctorId);
        
        if (userId != null && doctorRepository.existsByUserIdAndIdNot(userId, doctorId)) {
            throw new RuntimeException("Tài khoản này đã được liên kết với bác sĩ khác");
        }
        
        existing.setUserId(userId);
        return doctorRepository.save(existing);
    }

    public Doctor changeStatus(Long id, String status) {
        Doctor existing = getDoctorById(id);
        existing.setStatus(status);
        return doctorRepository.save(existing);
    }

    private void validateDoctorData(Doctor doctor, Long id) {
        if (doctor.getCode() == null || doctor.getCode().trim().isEmpty()) {
            throw new RuntimeException("Mã bác sĩ không được để trống");
        }
        
        if (doctor.getFullname() == null || doctor.getFullname().trim().isEmpty()) {
            throw new RuntimeException("Họ tên bác sĩ không được để trống");
        }
        
        if (id == null) {
            if (doctorRepository.existsByCode(doctor.getCode())) {
                throw new RuntimeException("Mã bác sĩ đã tồn tại");
            }
            if (doctor.getUserId() != null && doctorRepository.existsByUserId(doctor.getUserId())) {
                throw new RuntimeException("Tài khoản này đã được liên kết với bác sĩ khác");
            }
        } else {
            if (doctorRepository.existsByCodeAndIdNot(doctor.getCode(), id)) {
                throw new RuntimeException("Mã bác sĩ đã tồn tại");
            }
            if (doctor.getUserId() != null && doctorRepository.existsByUserIdAndIdNot(doctor.getUserId(), id)) {
                throw new RuntimeException("Tài khoản này đã được liên kết với bác sĩ khác");
            }
        }

        if (doctor.getEmail() != null && !doctor.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(doctor.getEmail()).matches()) {
                throw new RuntimeException("Email không hợp lệ");
            }
        }

        if (doctor.getPhone() != null && !doctor.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(doctor.getPhone()).matches()) {
                throw new RuntimeException("Số điện thoại không hợp lệ");
            }
        }
    }
}
