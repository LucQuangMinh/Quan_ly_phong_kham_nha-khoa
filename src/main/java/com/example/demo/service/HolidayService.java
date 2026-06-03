package com.example.demo.service;

import com.example.demo.entity.Holiday;
import com.example.demo.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }
    
    public Holiday getHolidayById(Long id) {
        return holidayRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ nghỉ"));
    }

    public List<Holiday> getHolidaysBetween(LocalDate start, LocalDate end) {
        return holidayRepository.findByHolidayDateBetween(start, end);
    }

    public Holiday toggleHoliday(LocalDate date) {
        // Ràng buộc bảo vệ: Tuyệt đối không được xóa một kỳ nghỉ đang diễn ra (hôm nay)
        Optional<Holiday> existingOpt = holidayRepository.findByHolidayDate(date);
        
        if (existingOpt.isPresent()) {
            if (date.isEqual(LocalDate.now())) {
                throw new RuntimeException("Không xóa kỳ nghỉ đang áp dụng trong khoảng ngày hiện tại");
            }
            holidayRepository.delete(existingOpt.get());
            return null;
        } else {
            Holiday holiday = new Holiday();
            holiday.setHolidayDate(date);
            holiday.setStatus("Hiệu lực");
            Holiday saved = holidayRepository.save(holiday);
            
            // Xóa toàn bộ các ca làm việc (ca trực) đang được thiết lập trong ngày này
            holidayRepository.deleteShiftsByDate(date);
            holidayRepository.deleteDoctorSchedulesByDate(date);
            
            return saved;
        }
    }

    public void toggleWeek(LocalDate startDate) {
        boolean allHolidays = true;
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            if (!holidayRepository.findByHolidayDate(currentDate).isPresent()) {
                allHolidays = false;
                break;
            }
        }

        if (allHolidays) {
            // Unset all holidays for the week
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                Optional<Holiday> existingOpt = holidayRepository.findByHolidayDate(currentDate);
                if (existingOpt.isPresent()) {
                    if (currentDate.isEqual(LocalDate.now())) {
                        throw new RuntimeException("Không xóa kỳ nghỉ đang áp dụng trong khoảng ngày hiện tại");
                    }
                    holidayRepository.delete(existingOpt.get());
                }
            }
        } else {
            // Set all missing holidays for the week
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                if (!holidayRepository.findByHolidayDate(currentDate).isPresent()) {
                    Holiday holiday = new Holiday();
                    holiday.setHolidayDate(currentDate);
                    holiday.setStatus("Hiệu lực");
                    holidayRepository.save(holiday);
                    
                    holidayRepository.deleteShiftsByDate(currentDate);
                    holidayRepository.deleteDoctorSchedulesByDate(currentDate);
                }
            }
        }
    }

    // CRUD truyền thống
    public Holiday createHoliday(Holiday holiday) {
        if (holiday.getStatus() == null) {
            holiday.setStatus("Hiệu lực");
        }
        Holiday saved = holidayRepository.save(holiday);
        
        holidayRepository.deleteShiftsByDate(holiday.getHolidayDate());
        holidayRepository.deleteDoctorSchedulesByDate(holiday.getHolidayDate());
        
        return saved;
    }

    public Holiday updateHoliday(Long id, Holiday details) {
        Holiday existing = getHolidayById(id);
        existing.setHolidayDate(details.getHolidayDate());
        existing.setStatus(details.getStatus());
        
        Holiday saved = holidayRepository.save(existing);
        
        if ("Hiệu lực".equals(saved.getStatus())) {
            holidayRepository.deleteShiftsByDate(saved.getHolidayDate());
            holidayRepository.deleteDoctorSchedulesByDate(saved.getHolidayDate());
        }
        
        return saved;
    }

    public void deleteHoliday(Long id) {
        Holiday existing = getHolidayById(id);
        if (existing.getHolidayDate().isEqual(LocalDate.now())) {
            throw new RuntimeException("Không xóa kỳ nghỉ đang áp dụng trong khoảng ngày hiện tại");
        }
        holidayRepository.delete(existing);
    }
}
