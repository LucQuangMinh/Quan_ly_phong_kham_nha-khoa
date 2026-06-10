package com.example.demo.controller;

import com.example.demo.entity.AppointmentTracking;
import com.example.demo.entity.PatientAppointment;
import com.example.demo.repository.AppointmentTrackingRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private PatientAppointmentRepository appointmentRepo;

    @Autowired
    private AppointmentTrackingRepository trackingRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    /**
     * Search appointments by patient phone or name (today and future, active statuses)
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name) {

        List<String> activeStatuses = Arrays.asList("Đã đặt", "Đã xác nhận", "Khám xong");
        LocalDate today = LocalDate.now();

        List<PatientAppointment> results;

        if (phone != null && !phone.trim().isEmpty()) {
            results = appointmentRepo.findByPatientPhoneAndExaminationDateGreaterThanEqualOrderByExaminationDateAsc(
                    phone.trim(), today);
        } else if (name != null && !name.trim().isEmpty()) {
            results = appointmentRepo.findByPatientNameContainingIgnoreCaseAndExaminationDateGreaterThanEqualOrderByExaminationDateAsc(
                    name.trim(), today);
        } else {
            // Default: all active appointments today
            results = appointmentRepo.findByExaminationDateAndStatusIn(today, activeStatuses);
        }

        // Enrich with doctor name
        List<Map<String, Object>> enriched = results.stream().map(a -> {
            String doctorName = a.getDoctorId() != null
                    ? doctorRepo.findById(a.getDoctorId()).map(d -> "BS. " + d.getFullname()).orElse("—")
                    : "—";
            // Get tracking status
            String trackingStatus = trackingRepo.findByAppointmentId(a.getId())
                    .map(AppointmentTracking::getStatus).orElse(a.getStatus());

            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", a.getId());
            map.put("code", a.getCode() != null ? a.getCode() : "");
            map.put("patientName", a.getPatientName() != null ? a.getPatientName() : "");
            map.put("patientPhone", a.getPatientPhone() != null ? a.getPatientPhone() : "");
            map.put("examinationDate", a.getExaminationDate().toString());
            map.put("shiftType", a.getShiftType() != null ? a.getShiftType() : "");
            map.put("doctorName", doctorName);
            map.put("appointmentStatus", a.getStatus() != null ? a.getStatus() : "");
            map.put("trackingStatus", trackingStatus);
            map.put("serviceName", a.getServiceName() != null ? a.getServiceName() : "");
            map.put("room", a.getRoom() != null ? a.getRoom() : "");
            return map;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(enriched);
    }

    /**
     * Mark patient as arrived (Check-in): tracking "Chưa đến" -> "Đã đến"
     */
    @PutMapping("/{appointmentId}/arrived")
    public ResponseEntity<?> markArrived(@PathVariable Long appointmentId) {
        AppointmentTracking tracking = trackingRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi theo dõi cho lịch hẹn này"));

        if (!"Chưa đến".equals(tracking.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Lịch hẹn này không ở trạng thái 'Chưa đến' (hiện tại: " + tracking.getStatus() + ")"));
        }

        tracking.setStatus("Đã đến");
        tracking.setUpdatedAt(LocalDateTime.now());
        trackingRepo.save(tracking);

        // Sync PatientAppointment -> Đã xác nhận
        appointmentRepo.findById(appointmentId).ifPresent(app -> {
            app.setStatus("Đã xác nhận");
            appointmentRepo.save(app);
        });

        return ResponseEntity.ok(Map.of("success", true, "message", "Check-in thành công! Bệnh nhân đã vào hàng đợi."));
    }

    @Autowired
    private com.example.demo.repository.CashierShiftRepository cashierShiftRepo;

    /**
     * Mark appointment as paid (by receptionist after examination)
     */
    @PutMapping("/{appointmentId}/paid")
    public ResponseEntity<?> markPaid(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) Double givenAmount,
            @RequestHeader(value = "X-User-Name", defaultValue = "Lễ tân") String cashierName) {
            
        try { cashierName = java.net.URLDecoder.decode(cashierName, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) {}

        // State Machine Middleware: Check if shift is locked
        java.util.Optional<com.example.demo.entity.CashierShift> shiftOpt = 
            cashierShiftRepo.findTopByCashierNameAndShiftDateOrderByStartTimeDesc(cashierName, LocalDate.now());
            
        if (shiftOpt.isPresent()) {
            String status = shiftOpt.get().getStatus();
            if ("PENDING".equals(status)) {
                return ResponseEntity.status(400).body(Map.of(
                    "message", "Giao dịch bị từ chối: Quầy của bạn đang chờ duyệt chốt ca. Hãy chờ quản lý duyệt rồi mới có thể thu tiền tiếp."
                ));
            }
        }

        PatientAppointment app = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        if (!"Khám xong".equals(app.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Lịch hẹn chưa hoàn tất khám (trạng thái hiện tại: " + app.getStatus() + ")"));
        }
        
        if (app.getQuotedPrice() != null && givenAmount != null) {
            if (givenAmount < app.getQuotedPrice()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số tiền khách đưa (" + givenAmount + ") nhỏ hơn số tiền cần thanh toán (" + app.getQuotedPrice() + ")."));
            }
        }

        app.setStatus("Đã thanh toán");
        appointmentRepo.save(app);

        return ResponseEntity.ok(Map.of("success", true, "message", "Xác nhận thanh toán thành công!"));
    }
}
