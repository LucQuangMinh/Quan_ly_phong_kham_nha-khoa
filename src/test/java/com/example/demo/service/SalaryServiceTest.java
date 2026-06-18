package com.example.demo.service;

import com.example.demo.dto.SalaryPreviewDto;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.DoctorSchedule;
import com.example.demo.entity.SalaryConfig;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.DoctorScheduleRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import com.example.demo.repository.SalaryConfigRepository;
import com.example.demo.repository.SalaryMultiplierDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalaryServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private PatientAppointmentRepository patientAppointmentRepository;

    @Mock
    private SalaryConfigRepository salaryConfigRepository;

    @Mock
    private SalaryMultiplierDayRepository salaryMultiplierDayRepository;

    @InjectMocks
    private SalaryService salaryService;

    private Doctor validDoctor;
    private SalaryConfig validConfig;

    @BeforeEach
    void setUp() {
        validDoctor = new Doctor();
        validDoctor.setId(1L);
        validDoctor.setFullname("Dr. Test");
        validDoctor.setDegree("Tiến sĩ"); // coeff = 1.5

        validConfig = new SalaryConfig();
        validConfig.setBaseHourlyRate(100000.0); // 100k/h -> 400k/ca 4h
    }

    @Test
    void testCalculateSalaryPreview_NoShifts() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor));
        when(salaryConfigRepository.findById(1L)).thenReturn(Optional.of(validConfig));
        when(doctorScheduleRepository.findByDoctor_IdAndShiftDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());
        when(salaryMultiplierDayRepository.findByMonthAndYear(6, 2026)).thenReturn(new ArrayList<>());

        SalaryPreviewDto preview = salaryService.calculateSalaryPreview(1L, 6, 2026);

        assertNotNull(preview);
        assertEquals(0, preview.getTotalShifts());
        assertEquals(0.0, preview.getTotalSalary());
    }

    @Test
    void testCalculateSalaryPreview_WithShifts() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(validDoctor));
        when(salaryConfigRepository.findById(1L)).thenReturn(Optional.of(validConfig));

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setShiftDate(LocalDate.of(2026, 6, 15)); // Giả sử là ngày thường coeff 1.0
        schedule.setShiftType("Sáng"); // 4 giờ
        schedule.setStatus("Đã duyệt trực");
        
        when(doctorScheduleRepository.findByDoctor_IdAndShiftDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(schedule));
                
        when(salaryMultiplierDayRepository.findByMonthAndYear(6, 2026)).thenReturn(new ArrayList<>());
        
        when(patientAppointmentRepository.sumPatientBonusCoefficientByDoctorAndDate(eq(1L), eq(LocalDate.of(2026, 6, 15))))
                .thenReturn(0.0); // 0 bệnh nhân

        SalaryPreviewDto preview = salaryService.calculateSalaryPreview(1L, 6, 2026);

        assertNotNull(preview);
        assertEquals(1, preview.getTotalShifts());
        
        // Tính nhẩm: 6 * (1.0 + 0) * 1.5 * 100k = 900k.
        assertEquals(900000.0, preview.getTotalSalary());
    }

    @Test
    void testCalculateSalaryPreview_DoctorNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> salaryService.calculateSalaryPreview(99L, 6, 2026));
        assertEquals("Không tìm thấy bác sĩ", exception.getMessage());
    }
}
