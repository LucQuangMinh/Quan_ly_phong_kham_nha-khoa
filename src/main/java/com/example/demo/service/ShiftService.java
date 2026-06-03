package com.example.demo.service;

import com.example.demo.entity.Shift;
import com.example.demo.repository.HolidayRepository;
import com.example.demo.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepo;

    @Autowired
    private HolidayRepository holidayRepo;
    
    @Autowired
    private com.example.demo.repository.DoctorScheduleRepository docScheduleRepo;

    public List<Shift> getShifts(LocalDate start, LocalDate end) {
        return shiftRepo.findByShiftDateBetween(start, end);
    }

    @Transactional
    public void toggleShift(LocalDate date, String shiftType) {
        if (holidayRepo.findByHolidayDate(date).isPresent()) {
            throw new RuntimeException("Ngày này nằm trong kỳ nghỉ hiệu lực của hệ thống. Không thể thiết lập ca làm việc!");
        }

        Optional<Shift> existing = shiftRepo.findByShiftDateAndShiftType(date, shiftType);
        if (existing.isPresent()) {
            shiftRepo.delete(existing.get());
            // Cascade delete: Xóa lịch bác sĩ nếu ca bị đóng
            docScheduleRepo.deleteByShiftDateAndShiftType(date, shiftType);
        } else {
            Shift s = new Shift();
            s.setShiftDate(date);
            s.setShiftType(shiftType);
            s.setStatus("Mở");
            shiftRepo.save(s);
        }
    }

    @Transactional
    public Map<String, Object> toggleWeeklyShift(LocalDate startDate, String shiftType) {
        Map<String, Object> response = new HashMap<>();
        boolean hasHoliday = false;
        
        // First pass: Check if the shift is enabled for ALL non-holiday days in the week
        boolean allEnabled = true;
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDate.plusDays(i);
            if (holidayRepo.findByHolidayDate(d).isPresent()) {
                hasHoliday = true;
                continue;
            }
            if (!shiftRepo.findByShiftDateAndShiftType(d, shiftType).isPresent()) {
                allEnabled = false;
            }
        }

        // Second pass: Toggle
        for (int i = 0; i < 7; i++) {
            LocalDate d = startDate.plusDays(i);
            if (holidayRepo.findByHolidayDate(d).isPresent()) {
                continue;
            }
            
            Optional<Shift> existing = shiftRepo.findByShiftDateAndShiftType(d, shiftType);
            if (allEnabled) {
                // If all were enabled, turn them all off
                if (existing.isPresent()) {
                    shiftRepo.delete(existing.get());
                    // Cascade delete: Xóa lịch bác sĩ nếu ca bị đóng
                    docScheduleRepo.deleteByShiftDateAndShiftType(d, shiftType);
                }
            } else {
                // If not all were enabled, turn them all on
                if (!existing.isPresent()) {
                    Shift s = new Shift();
                    s.setShiftDate(d);
                    s.setShiftType(shiftType);
                    s.setStatus("Mở");
                    shiftRepo.save(s);
                }
            }
        }

        response.put("success", true);
        if (hasHoliday) {
            response.put("warning", "Một số ngày trong tuần trùng lịch nghỉ lễ nên không được thiết lập ca!");
        }
        return response;
    }
}
