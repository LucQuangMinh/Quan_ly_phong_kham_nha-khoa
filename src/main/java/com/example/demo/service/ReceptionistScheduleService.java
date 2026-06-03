package com.example.demo.service;

import com.example.demo.entity.ReceptionistSchedule;
import com.example.demo.entity.User;
import com.example.demo.repository.ReceptionistScheduleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReceptionistScheduleService {

    @Autowired
    private ReceptionistScheduleRepository scheduleRepo;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private HolidayRepository holidayRepo;

    @Autowired
    private com.example.demo.repository.ShiftRepository shiftRepo;

    public List<ReceptionistSchedule> getSchedules(LocalDate start, LocalDate end, Long userId, String role) {
        // Lễ tân và Admin đều được xem tất cả lịch của Lễ tân khác
        return scheduleRepo.findByShiftDateBetween(start, end);
    }

    @Transactional
    public void toggleBulkReceptionists(List<Long> userIds, LocalDate date, String shiftType) {
        if (holidayRepo.findByHolidayDate(date).isPresent()) {
            throw new RuntimeException("Không thể phân công vào ngày nghỉ!");
        }
        
        if (!shiftRepo.findByShiftDateAndShiftType(date, shiftType).isPresent()) {
            throw new RuntimeException("Chưa mở ca khám này!");
        }
        
        for (Long uId : userIds) {
            User user = userRepo.findById(uId).orElse(null);
            if (user == null || !"Lễ tân".equalsIgnoreCase(user.getRole())) continue;

            Optional<ReceptionistSchedule> existing = scheduleRepo.findByReceptionist_IdAndShiftDateAndShiftType(uId, date, shiftType);
            if (existing.isPresent()) {
                ReceptionistSchedule ds = existing.get();
                if ("Đề xuất của tôi".equals(ds.getStatus())) {
                    ds.setStatus("Đã duyệt trực");
                    scheduleRepo.save(ds);
                } else {
                    scheduleRepo.delete(ds);
                }
            } else {
                ReceptionistSchedule s = new ReceptionistSchedule();
                s.setReceptionist(user);
                s.setShiftDate(date);
                s.setShiftType(shiftType);
                s.setStatus("Đã duyệt trực");
                scheduleRepo.save(s);
            }
        }
    }

    @Transactional
    public void toggleReceptionistSelf(Long userId, LocalDate date, String shiftType) {
        if (holidayRepo.findByHolidayDate(date).isPresent()) {
            throw new RuntimeException("Không thể đăng ký vào ngày nghỉ!");
        }
        
        if (!shiftRepo.findByShiftDateAndShiftType(date, shiftType).isPresent()) {
            throw new RuntimeException("Chưa mở ca khám này!");
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Optional<ReceptionistSchedule> existing = scheduleRepo.findByReceptionist_IdAndShiftDateAndShiftType(userId, date, shiftType);
        if (existing.isPresent()) {
            scheduleRepo.delete(existing.get());
        } else {
            ReceptionistSchedule s = new ReceptionistSchedule();
            s.setReceptionist(user);
            s.setShiftDate(date);
            s.setShiftType(shiftType);
            s.setStatus("Đề xuất của tôi");
            scheduleRepo.save(s);
        }
    }

    @Transactional
    public void toggleReceptionistWeek(List<Long> userIds, LocalDate startDate, String shiftType) {
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            if (holidayRepo.findByHolidayDate(date).isPresent()) continue;
            if (!shiftRepo.findByShiftDateAndShiftType(date, shiftType).isPresent()) continue;
            
            for (Long uId : userIds) {
                User user = userRepo.findById(uId).orElse(null);
                if (user == null || !"Lễ tân".equalsIgnoreCase(user.getRole())) continue;
                
                Optional<ReceptionistSchedule> existing = scheduleRepo.findByReceptionist_IdAndShiftDateAndShiftType(uId, date, shiftType);
                if (existing.isPresent()) {
                    ReceptionistSchedule ds = existing.get();
                    if ("Đề xuất của tôi".equals(ds.getStatus())) {
                        ds.setStatus("Đã duyệt trực");
                        scheduleRepo.save(ds);
                    } else {
                        scheduleRepo.delete(ds);
                    }
                } else {
                    ReceptionistSchedule s = new ReceptionistSchedule();
                    s.setReceptionist(user);
                    s.setShiftDate(date);
                    s.setShiftType(shiftType);
                    s.setStatus("Đã duyệt trực");
                    scheduleRepo.save(s);
                }
            }
        }
    }

    @Transactional
    public void approveProposal(Long id) {
        ReceptionistSchedule s = scheduleRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy ca trực"));
        s.setStatus("Đã duyệt trực");
        scheduleRepo.save(s);
    }

    @Transactional
    public void removeReceptionistFromShift(Long id) {
        scheduleRepo.deleteById(id);
    }
}
