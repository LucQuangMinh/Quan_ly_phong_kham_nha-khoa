package com.example.demo.service;

import com.example.demo.dto.DailySalaryDetailDto;
import com.example.demo.dto.SalaryPreviewDto;
import com.example.demo.dto.SalaryYearPreviewDto;
import com.example.demo.dto.MonthlySalaryDetailDto;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.DoctorSchedule;
import com.example.demo.entity.SalaryConfig;
import com.example.demo.entity.SalaryMultiplierDay;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.DoctorScheduleRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import com.example.demo.repository.SalaryConfigRepository;
import com.example.demo.repository.SalaryMultiplierDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private PatientAppointmentRepository patientAppointmentRepository;

    @Autowired
    private SalaryConfigRepository salaryConfigRepository;

    @Autowired
    private SalaryMultiplierDayRepository salaryMultiplierDayRepository;

    private double getDegreeCoefficient(String degree) {
        if (degree == null) return 1.0;
        String d = degree.toLowerCase();
        if (d.contains("phó giáo sư") || d.contains("pgs")) return 2.0;
        if (d.contains("giáo sư") || d.contains("gs")) return 2.5;
        if (d.contains("phó tiến sĩ") || d.contains("phó tiến sỹ") || d.contains("pts")) return 1.5;
        if (d.contains("tiến sỹ") || d.contains("tiến sĩ") || d.equals("ts")) return 1.5;
        if (d.contains("thạc sỹ") || d.contains("thạc sĩ") || d.contains("ck1") || d.contains("ck2") || d.contains("cki")) return 1.2;
        return 1.0; // Đại học or default
    }

    public SalaryPreviewDto calculateSalaryPreview(Long doctorId, int month, int year) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        SalaryConfig config = salaryConfigRepository.findById(1L).orElse(new SalaryConfig());
        double baseHourlyRate = config.getBaseHourlyRate() != null ? config.getBaseHourlyRate() : 0.0;
        double degreeCoeff = getDegreeCoefficient(doctor.getDegree());

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Lấy tất cả lịch trực của bác sĩ trong tháng có trạng thái "Đã duyệt trực" (hoặc cứ có lịch là tính)
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctor_IdAndShiftDateBetween(doctorId, startDate, endDate)
                .stream()
                .filter(s -> "Đã duyệt trực".equals(s.getStatus()))
                .collect(Collectors.toList());
        Map<LocalDate, DoctorSchedule> scheduleMap = schedules.stream()
                .collect(Collectors.toMap(DoctorSchedule::getShiftDate, s -> s, (s1, s2) -> s1));

        // Lấy danh sách các ngày có hệ số 1.5
        List<LocalDate> multiplierDays = salaryMultiplierDayRepository.findByMonthAndYear(month, year)
                .stream()
                .map(SalaryMultiplierDay::getDate)
                .collect(Collectors.toList());

        List<DailySalaryDetailDto> dailyDetails = new ArrayList<>();
        double totalSalary = 0.0;
        double totalPatientCoeff = 0.0;
        int totalShifts = 0;
        int daysWithCoeff1 = 0;
        int daysWithCoeff15 = 0;

        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate date = yearMonth.atDay(i);
            DoctorSchedule schedule = scheduleMap.get(date);
            boolean hasShift = (schedule != null);

            DailySalaryDetailDto detail = new DailySalaryDetailDto(date, hasShift);

            if (hasShift) {
                totalShifts++;
                detail.setShiftType(schedule.getShiftType());

                double shiftCoeff = multiplierDays.contains(date) ? 1.5 : 1.0;
                detail.setShiftCoefficient(shiftCoeff);
                if (shiftCoeff == 1.5) daysWithCoeff15++;
                else daysWithCoeff1++;

                double patientCoeff = patientAppointmentRepository.sumPatientBonusCoefficientByDoctorAndDate(doctorId, date);
                detail.setPatientCoefficient(patientCoeff);
                totalPatientCoeff += patientCoeff;

                // Tiền_một_ca = 6 * (Hệ_số_ca_làm_việc + Tổng_hệ_số_bệnh_nhân) * Hệ_số_bác_sĩ * Số_tiền_một_giờ
                double dailySalary = 6 * (shiftCoeff + patientCoeff) * degreeCoeff * baseHourlyRate;
                detail.setDailySalary(dailySalary);
                totalSalary += dailySalary;
            }

            dailyDetails.add(detail);
        }

        SalaryPreviewDto preview = new SalaryPreviewDto();
        preview.setDoctorId(doctorId);
        preview.setDoctorName(doctor.getFullname());
        preview.setMonth(month);
        preview.setYear(year);
        preview.setBaseHourlyRate(baseHourlyRate);
        preview.setDegreeCoefficient(degreeCoeff);
        preview.setTotalShifts(totalShifts);
        preview.setDaysWithCoeff1(daysWithCoeff1);
        preview.setDaysWithCoeff15(daysWithCoeff15);
        preview.setTotalPatientCoefficient(totalPatientCoeff);
        preview.setTotalSalary(totalSalary);
        preview.setDailyDetails(dailyDetails);

        return preview;
    }
    
    public List<SalaryPreviewDto> calculateAllSalariesPreview(int month, int year) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<SalaryPreviewDto> results = new ArrayList<>();
        for (Doctor d : doctors) {
            results.add(calculateSalaryPreview(d.getId(), month, year));
        }
        return results;
    }

    public SalaryYearPreviewDto calculateYearlySalaryPreview(Long doctorId, int year) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        SalaryConfig config = salaryConfigRepository.findById(1L).orElse(new SalaryConfig());
        double baseHourlyRate = config.getBaseHourlyRate() != null ? config.getBaseHourlyRate() : 0.0;

        SalaryYearPreviewDto yearDto = new SalaryYearPreviewDto();
        yearDto.setDoctorId(doctorId);
        yearDto.setDoctorName(doctor.getFullname());
        yearDto.setYear(year);
        yearDto.setBaseHourlyRate(baseHourlyRate);
        yearDto.setDegreeCoefficient(getDegreeCoefficient(doctor.getDegree()));

        int totalYearlyShifts = 0;
        int totalDaysWithCoeff1 = 0;
        int totalDaysWithCoeff15 = 0;
        double totalYearlyPatientCoefficient = 0.0;
        double totalYearlySalary = 0.0;

        List<MonthlySalaryDetailDto> monthlyDetails = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            SalaryPreviewDto monthDto = calculateSalaryPreview(doctorId, month, year);
            if (monthDto.getTotalSalary() > 0 || monthDto.getTotalShifts() > 0) {
                totalYearlyShifts += monthDto.getTotalShifts();
                totalDaysWithCoeff1 += monthDto.getDaysWithCoeff1();
                totalDaysWithCoeff15 += monthDto.getDaysWithCoeff15();
                totalYearlyPatientCoefficient += monthDto.getTotalPatientCoefficient();
                totalYearlySalary += monthDto.getTotalSalary();

                monthlyDetails.add(new MonthlySalaryDetailDto(
                        month,
                        monthDto.getTotalShifts(),
                        monthDto.getTotalPatientCoefficient(),
                        monthDto.getTotalSalary()
                ));
            }
        }

        yearDto.setTotalYearlyShifts(totalYearlyShifts);
        yearDto.setTotalDaysWithCoeff1(totalDaysWithCoeff1);
        yearDto.setTotalDaysWithCoeff15(totalDaysWithCoeff15);
        yearDto.setTotalYearlyPatientCoefficient(totalYearlyPatientCoefficient);
        yearDto.setTotalYearlySalary(totalYearlySalary);
        yearDto.setMonthlyDetails(monthlyDetails);

        return yearDto;
    }

    public List<SalaryYearPreviewDto> calculateAllYearlySalariesPreview(int year) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<SalaryYearPreviewDto> results = new ArrayList<>();
        for (Doctor d : doctors) {
            results.add(calculateYearlySalaryPreview(d.getId(), year));
        }
        return results;
    }
}
