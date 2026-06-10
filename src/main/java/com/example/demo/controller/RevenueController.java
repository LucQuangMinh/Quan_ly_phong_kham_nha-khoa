package com.example.demo.controller;

import com.example.demo.entity.CashierShift;
import com.example.demo.repository.CashierShiftRepository;
import com.example.demo.repository.PatientAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private PatientAppointmentRepository appointmentRepo;

    @Autowired
    private CashierShiftRepository shiftRepo;

    /**
     * KPI Dashboard API
     */
    @GetMapping("/kpi")
    public ResponseEntity<?> getKPI(
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        
        try { role = java.net.URLDecoder.decode(role, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) {}

        if (!"Admin".equalsIgnoreCase(role) && !role.toLowerCase().contains("quản lý") && !role.toLowerCase().contains("kế toán")) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        LocalDate today = LocalDate.now();

        List<CashierShift> shifts = shiftRepo.findByShiftDateOrderByStartTimeDesc(today).stream()
                .filter(s -> "CLOSED".equals(s.getStatus()))
                .toList();

        Double netRevenue = shifts.stream().mapToDouble(s -> s.getTheoreticalCash() != null ? s.getTheoreticalCash() : 0.0).sum();
        Double cashAtCounter = shifts.stream().mapToDouble(s -> s.getCountedCash() != null ? s.getCountedCash() : 0.0).sum();

        Long totalInvoices = appointmentRepo.countByExaminationDateAndStatus(today, "Đã thanh toán");

        List<Object[]> categoryAndServices = appointmentRepo.sumRevenueByCategoryAndService(today, "Đã thanh toán");
        
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, List<Map<String, Object>>> categoryDetails = new HashMap<>();

        for (Object[] row : categoryAndServices) {
            String category = row[0] != null ? row[0].toString() : "Khác";
            String serviceName = row[1] != null ? row[1].toString() : "Dịch vụ khác";
            Double val = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + val);
            
            categoryDetails.putIfAbsent(category, new ArrayList<>());
            Map<String, Object> serviceItem = new HashMap<>();
            serviceItem.put("label", serviceName);
            serviceItem.put("value", val);
            categoryDetails.get(category).add(serviceItem);
        }

        List<Map<String, Object>> categoriesList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            Map<String, Object> catMap = new HashMap<>();
            catMap.put("label", entry.getKey());
            catMap.put("value", entry.getValue());
            categoriesList.add(catMap);
        }

        Map<String, Object> drillDownData = new HashMap<>();
        drillDownData.put("categories", categoriesList);
        drillDownData.put("details", categoryDetails);

        Map<String, Object> data = new HashMap<>();
        data.put("netRevenue", netRevenue);
        data.put("cashAtCounter", cashAtCounter);
        data.put("totalInvoices", totalInvoices);
        data.put("donutChart", drillDownData);

        return ResponseEntity.ok(data);
    }

    /**
     * List Shift Reconciliations
     */
    @GetMapping("/shifts")
    public ResponseEntity<?> getShifts() {
        return ResponseEntity.ok(shiftRepo.findByShiftDateOrderByStartTimeDesc(LocalDate.now()));
    }

    /**
     * Get Theoretical Cash for current shift
     */
    @GetMapping("/shifts/theoretical-cash")
    public ResponseEntity<?> getTheoreticalCash(
            @RequestHeader(value = "X-User-Name", defaultValue = "Lễ tân") String cashierName) {
            
        try { cashierName = java.net.URLDecoder.decode(cashierName, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) {}
        
        Double totalTheoreticalCash = appointmentRepo.sumRevenueByDateAndStatus(LocalDate.now(), "Đã thanh toán");
        if (totalTheoreticalCash == null) totalTheoreticalCash = 0.0;
        
        List<CashierShift> closedShifts = shiftRepo.findByCashierNameAndShiftDate(cashierName, LocalDate.now()).stream()
                .filter(s -> "CLOSED".equals(s.getStatus()))
                .toList();
        Double previouslyClosedCash = closedShifts.stream().mapToDouble(s -> s.getTheoreticalCash() != null ? s.getTheoreticalCash() : 0.0).sum();
        
        Double theoreticalCash = totalTheoreticalCash - previouslyClosedCash;
        
        return ResponseEntity.ok(Map.of("theoreticalCash", theoreticalCash));
    }

    /**
     * Request Close Shift (Cashier)
     */
    @PostMapping("/shifts/close-request")
    public ResponseEntity<?> requestCloseShift(
            @RequestHeader(value = "X-User-Name", defaultValue = "Lễ tân") String cashierName,
            @RequestBody Map<String, Object> payload) {
            
        try { cashierName = java.net.URLDecoder.decode(cashierName, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) {}

        Double countedCash = Double.valueOf(payload.get("countedCash").toString());
        String reason = payload.containsKey("reason") ? payload.get("reason").toString() : "";

        LocalDate today = LocalDate.now();
        
        // Find existing ACTIVE or create new
        CashierShift shift = shiftRepo.findTopByCashierNameAndShiftDateOrderByStartTimeDesc(cashierName, today)
                .orElse(new CashierShift());

        if ("PENDING".equals(shift.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ca trực đang chờ duyệt, không thể gửi yêu cầu liên tục!"));
        }
        
        if ("CLOSED".equals(shift.getStatus())) {
            // Start a new shift for the new transactions
            shift = new CashierShift();
        }

        Double totalTheoreticalCash = appointmentRepo.sumRevenueByDateAndStatus(today, "Đã thanh toán");
        if (totalTheoreticalCash == null) totalTheoreticalCash = 0.0;
        
        List<CashierShift> closedShifts = shiftRepo.findByCashierNameAndShiftDate(cashierName, today).stream()
                .filter(s -> "CLOSED".equals(s.getStatus()))
                .toList();
        Double previouslyClosedCash = closedShifts.stream().mapToDouble(s -> s.getTheoreticalCash() != null ? s.getTheoreticalCash() : 0.0).sum();
        
        Double theoreticalCash = totalTheoreticalCash - previouslyClosedCash;

        shift.setCashierName(cashierName);
        shift.setShiftDate(today);
        if (shift.getStartTime() == null) shift.setStartTime(LocalDateTime.now().minusHours(4)); // Mock start time
        shift.setEndTime(LocalDateTime.now());
        shift.setTheoreticalCash(theoreticalCash);
        shift.setCountedCash(countedCash);
        shift.setDiscrepancyReason(reason);
        shift.setStatus("PENDING");

        shiftRepo.save(shift);

        return ResponseEntity.ok(Map.of("message", "Đã gửi yêu cầu chốt ca. Đang chờ quản lý duyệt."));
    }

    /**
     * Approve shift
     */
    @PostMapping("/shifts/{id}/approve")
    public ResponseEntity<?> approveShift(@PathVariable Long id) {
        CashierShift shift = shiftRepo.findById(id).orElseThrow();
        shift.setStatus("CLOSED");
        shiftRepo.save(shift);
        return ResponseEntity.ok(Map.of("message", "Đã phê duyệt chốt ca thành công."));
    }

    @GetMapping("/shifts/status")
    public ResponseEntity<?> getShiftStatus(@RequestHeader(value = "X-User-Name", defaultValue = "Lễ tân") String cashierName) {
        try { cashierName = java.net.URLDecoder.decode(cashierName, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) {}
        
        java.util.Optional<CashierShift> shiftOpt = shiftRepo.findTopByCashierNameAndShiftDateOrderByStartTimeDesc(cashierName, LocalDate.now());
        if (shiftOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("status", shiftOpt.get().getStatus()));
        }
        return ResponseEntity.ok(Map.of("status", "ACTIVE"));
    }

    /**
     * Reject shift
     */
    @PostMapping("/shifts/{id}/reject")
    public ResponseEntity<?> rejectShift(@PathVariable Long id) {
        CashierShift shift = shiftRepo.findById(id).orElseThrow();
        shift.setStatus("REJECTED"); // Back to REJECTED to notify cashier
        shift.setCountedCash(null);
        shiftRepo.save(shift);
        return ResponseEntity.ok(Map.of("message", "Đã từ chối chốt ca. Yêu cầu thu ngân đếm lại."));
    }
}
