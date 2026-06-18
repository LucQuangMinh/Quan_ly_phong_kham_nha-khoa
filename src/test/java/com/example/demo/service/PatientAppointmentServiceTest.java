package com.example.demo.service;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.Doctor;
import com.example.demo.entity.PatientAppointment;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientAppointmentServiceTest {

    @Mock
    private PatientAppointmentRepository appointmentRepo;

    @Mock
    private AppointmentTrackingRepository trackingRepo;

    @Mock
    private DoctorRepository doctorRepo;

    @InjectMocks
    private PatientAppointmentService appointmentService;

    private PatientAppointment validAppointment;

    @BeforeEach
    void setUp() {
        validAppointment = new PatientAppointment();
        validAppointment.setId(1L);
        validAppointment.setPatientName("Nguyen Van A");
        validAppointment.setExaminationDate(LocalDate.now().plusDays(1));
        validAppointment.setShiftType("Ca sáng");
        validAppointment.setDoctorId(2L);
        validAppointment.setStatus("Đã đặt");
    }

    @Test
    void testCreateAppointment_Success() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(2L);
        mockDoctor.setFullname("Dr. Smith");
        mockDoctor.setRoom("P101");

        when(appointmentRepo.save(any(PatientAppointment.class))).thenReturn(validAppointment);
        when(doctorRepo.findById(2L)).thenReturn(Optional.of(mockDoctor));
        when(trackingRepo.save(any(AppointmentTracking.class))).thenReturn(new AppointmentTracking());

        PatientAppointment created = appointmentService.createAppointment(validAppointment);

        assertNotNull(created);
        assertEquals("Đã đặt", created.getStatus());
        verify(appointmentRepo, times(1)).save(validAppointment);
        verify(trackingRepo, times(1)).save(any(AppointmentTracking.class));
    }

    @Test
    void testUpdateAppointment_InvalidStateTransition() {
        validAppointment.setStatus("Hủy");
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(validAppointment));

        PatientAppointment newData = new PatientAppointment();
        newData.setStatus("Đã xác nhận");
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.updateAppointment(1L, newData));
            
        assertTrue(exception.getMessage().contains("Chuyển trạng thái không hợp lệ"));
        verify(appointmentRepo, never()).save(any(PatientAppointment.class));
    }

    @Test
    void testUpdateAppointment_ValidStateTransition() {
        validAppointment.setStatus("Đã xác nhận");
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(validAppointment));
        when(appointmentRepo.save(any(PatientAppointment.class))).thenReturn(validAppointment);
        when(trackingRepo.findByAppointmentId(1L)).thenReturn(Optional.of(new AppointmentTracking()));

        PatientAppointment newData = new PatientAppointment();
        newData.setStatus("Hủy");

        PatientAppointment updated = appointmentService.updateAppointment(1L, newData);
        
        assertEquals("Hủy", updated.getStatus());
        verify(appointmentRepo, times(1)).save(validAppointment);
        verify(trackingRepo, times(1)).save(any(AppointmentTracking.class));
    }
}
