package com.example.demo.service;

import com.example.demo.entity.Doctor;
import com.example.demo.entity.DoctorSchedule;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.DoctorScheduleRepository;
import com.example.demo.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorScheduleService {

    @Autowired
    private DoctorScheduleRepository scheduleRepo;
    
    @Autowired
    private DoctorRepository doctorRepo;
    
    @Autowired
    private HolidayRepository holidayRepo;

    @Autowired
    private com.example.demo.repository.ShiftRepository shiftRepo;

    public List<DoctorSchedule> getSchedules(LocalDate start, LocalDate end, Long userId, String role) {
        // Cho phép tất cả các role (Admin, Bác sĩ, Lễ tân) xem toàn bộ lịch trực
        // Việc cấp quyền thêm/xóa/sửa (nhấp vào) sẽ do frontend kiểm soát (isMine)
        return scheduleRepo.findByShiftDateBetween(start, end);
    }

    @Transactional
    public void toggleBulkDoctors(List<Long> doctorIds, LocalDate date, String shiftType) {
        if (holidayRepo.findByHolidayDate(date).isPresent()) {
            throw new RuntimeException("Không thể phân công vào ngày nghỉ!");
        }
        
        if (!shiftRepo.findByShiftDateAndShiftType(date, shiftType).isPresent()) {
            throw new RuntimeException("Ca làm việc này chưa được thiết lập (hoặc đang đóng). Vui lòng cấu hình ở phần Thiết lập ca làm việc trước!");
        }
        
        for (Long docId : doctorIds) {
            Optional<DoctorSchedule> existing = scheduleRepo.findByDoctor_IdAndShiftDateAndShiftType(docId, date, shiftType);
            if (existing.isPresent()) {
                DoctorSchedule ds = existing.get();
                if ("Đề xuất của tôi".equals(ds.getStatus())) {
                    ds.setStatus("Đã duyệt trực");
                    scheduleRepo.save(ds);
                } else {
                    scheduleRepo.delete(ds);
                }
            } else {
                Doctor doc = doctorRepo.findById(docId).orElse(null);
                if (doc != null) {
                    DoctorSchedule ds = new DoctorSchedule();
                    ds.setDoctor(doc);
                    ds.setShiftDate(date);
                    ds.setShiftType(shiftType);
                    ds.setStatus("Đã duyệt trực");
                    scheduleRepo.save(ds);
                }
            }
        }
    }

    @Transactional
    public void toggleDoctorSelf(Long userId, LocalDate date, String shiftType) {
        if (holidayRepo.findByHolidayDate(date).isPresent()) {
            throw new RuntimeException("Không thể đăng ký trực vào ngày nghỉ!");
        }
        
        if (!shiftRepo.findByShiftDateAndShiftType(date, shiftType).isPresent()) {
            throw new RuntimeException("Ca làm việc này chưa được thiết lập (hoặc đang đóng). Không thể đăng ký trực!");
        }

        Doctor doc = doctorRepo.findByUserId(userId).orElseThrow(() -> new RuntimeException("Chưa thiết lập hồ sơ Bác sĩ cho tài khoản này!"));
        Long realDoctorId = doc.getId();

        Optional<DoctorSchedule> existing = scheduleRepo.findByDoctor_IdAndShiftDateAndShiftType(realDoctorId, date, shiftType);
        if (existing.isPresent()) {
            if ("Đã duyệt trực".equals(existing.get().getStatus())) {
                throw new RuntimeException("Lịch đã được Admin duyệt, bạn không thể tự ý hủy!");
            }
            scheduleRepo.delete(existing.get());
        } else {
            DoctorSchedule ds = new DoctorSchedule();
            ds.setDoctor(doc);
            ds.setShiftDate(date);
            ds.setShiftType(shiftType);
            ds.setStatus("Đề xuất của tôi");
            scheduleRepo.save(ds);
        }
    }

    @Transactional
    public void toggleDoctorWeek(List<Long> doctorIds, LocalDate startDate, String shiftType) {
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            if (!holidayRepo.findByHolidayDate(currentDate).isPresent()) {
                if (shiftRepo.findByShiftDateAndShiftType(currentDate, shiftType).isPresent()) {
                    for (Long docId : doctorIds) {
                        Optional<DoctorSchedule> existing = scheduleRepo.findByDoctor_IdAndShiftDateAndShiftType(docId, currentDate, shiftType);
                        if (existing.isPresent()) {
                            DoctorSchedule ds = existing.get();
                            if ("Đề xuất của tôi".equals(ds.getStatus())) {
                                ds.setStatus("Đã duyệt trực");
                                scheduleRepo.save(ds);
                            } else {
                                scheduleRepo.delete(ds);
                            }
                        } else {
                            Doctor doc = doctorRepo.findById(docId).orElse(null);
                            if (doc != null) {
                                DoctorSchedule ds = new DoctorSchedule();
                                ds.setDoctor(doc);
                                ds.setShiftDate(currentDate);
                                ds.setShiftType(shiftType);
                                ds.setStatus("Đã duyệt trực");
                                scheduleRepo.save(ds);
                            }
                        }
                    }
                }
            }
        }
    }

    public void approveProposal(Long scheduleId) {
        DoctorSchedule ds = scheduleRepo.findById(scheduleId).orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trực"));
        ds.setStatus("Đã duyệt trực");
        scheduleRepo.save(ds);
    }

    @Transactional
    public void removeDoctorFromShift(Long scheduleId) {
        scheduleRepo.deleteById(scheduleId);
    }
}
