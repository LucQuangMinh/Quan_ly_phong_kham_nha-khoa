package com.example.demo.service;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentTrackingServiceTest {

    @Mock
    private AppointmentTrackingRepository trackingRepo;

    @Mock
    private PatientAppointmentRepository appointmentRepo;

    @Mock
    private DoctorRepository doctorRepo;

    @InjectMocks
    private AppointmentTrackingService trackingService;

    private AppointmentTracking validTracking;

    @BeforeEach
    void setUp() {
        validTracking = new AppointmentTracking();
        validTracking.setId(1L);
        validTracking.setPatientName("Nguyen Van B");
        validTracking.setStatus("Chưa đến");
        validTracking.setExaminationDate(LocalDateTime.now().toLocalDate());
    }

    @Test
    void testCreateTracking_Success() {
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(3L);
        mockDoctor.setFullname("Dr. Lee");
        mockDoctor.setRoom("P202");

        when(doctorRepo.findById(3L)).thenReturn(Optional.of(mockDoctor));
        when(trackingRepo.save(any(AppointmentTracking.class))).thenReturn(validTracking);

        AppointmentTracking created = trackingService.createTracking(validTracking, 3L);

        assertNotNull(created);
        assertEquals("Đang chờ", validTracking.getStatus());
        assertEquals("Dr. Lee", validTracking.getDoctorName());
        assertEquals("P202", validTracking.getRoom());
        verify(trackingRepo, times(1)).save(validTracking);
    }

    @Test
    void testUpdateTracking_ValidStateTransition() {
        validTracking.setStatus("Chưa đến");
        when(trackingRepo.findById(1L)).thenReturn(Optional.of(validTracking));
        when(trackingRepo.save(any(AppointmentTracking.class))).thenReturn(validTracking);

        AppointmentTracking newData = new AppointmentTracking();
        newData.setStatus("Đã đến"); // Check-in

        AppointmentTracking updated = trackingService.updateTracking(1L, newData);

        assertEquals("Đã đến", updated.getStatus());
        verify(trackingRepo, times(1)).save(validTracking);
    }

    @Test
    void testUpdateTracking_InvalidStateTransition() {
        validTracking.setStatus("Khám xong");
        when(trackingRepo.findById(1L)).thenReturn(Optional.of(validTracking));

        AppointmentTracking newData = new AppointmentTracking();
        newData.setStatus("Chưa đến"); // Cannot go backward

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trackingService.updateTracking(1L, newData));
        assertTrue(exception.getMessage().contains("Chuyển trạng thái không hợp lệ"));
        verify(trackingRepo, never()).save(any(AppointmentTracking.class));
    }
}
