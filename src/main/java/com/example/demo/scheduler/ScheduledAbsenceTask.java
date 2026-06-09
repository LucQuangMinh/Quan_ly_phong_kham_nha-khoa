package com.example.demo.scheduler;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.PatientAppointment;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class ScheduledAbsenceTask {

    @Autowired
    private AppointmentTrackingRepository trackingRepo;

    @Autowired
    private PatientAppointmentRepository appointmentRepo;

    /**
     * Chạy mỗi 10 phút một lần để kiểm tra các lịch hẹn "Chưa đến"
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void checkAndMarkAbsentAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Lấy tất cả các tracking có trạng thái "Chưa đến" của ngày hôm nay
        List<AppointmentTracking> unarrived = trackingRepo.findByStatusAndExaminationDate("Chưa đến", today);

        for (AppointmentTracking tracking : unarrived) {
            if (tracking.getAppointmentId() == null) continue;

            PatientAppointment app = appointmentRepo.findById(tracking.getAppointmentId()).orElse(null);
            if (app == null) continue;

            boolean shouldMarkAbsent = false;

            if ("Sáng".equalsIgnoreCase(app.getShiftType()) && now.isAfter(LocalTime.of(12, 0))) {
                shouldMarkAbsent = true;
            } else if ("Chiều".equalsIgnoreCase(app.getShiftType()) && now.isAfter(LocalTime.of(17, 0))) {
                shouldMarkAbsent = true;
            } else if ("Tối".equalsIgnoreCase(app.getShiftType()) && now.isAfter(LocalTime.of(21, 0))) {
                shouldMarkAbsent = true;
            }

            if (shouldMarkAbsent) {
                // Update tracking
                tracking.setStatus("Vắng");
                tracking.setUpdatedAt(LocalDateTime.now());
                trackingRepo.save(tracking);

                // Update appointment status to "Hủy" due to absence
                app.setStatus("Hủy");
                appointmentRepo.save(app);
                
                System.out.println("Scheduler: Marked appointment " + app.getCode() + " as Vắng mặt (Absent).");
            }
        }
    }
}
