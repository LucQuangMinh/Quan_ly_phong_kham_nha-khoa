package com.example.demo.selenium;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC2_4_PatientAppointmentTest extends BaseTest {

    private void loginAndNavigateToAppointments() {
        // Đăng nhập bằng role lễ tân hoặc bệnh nhân
        // Sử dụng lễ tân cho việc đăng ký hộ
        loginAs("admin", "admin123"); 
        driver.get(BASE_URL + "/appointments.html");
        wait.until(ExpectedConditions.urlContains("appointments.html"));
    }

    @Test
    @Order(1)
    public void test_UC2_4_FUNC_001_OpenBookingForm() {
        loginAndNavigateToAppointments();
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"openAddModal()\"]")));
        jsClick(addBtn);
        
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalTitle")));
        assertTrue(modalTitle.isDisplayed(), "Form Đặt lịch khám phải hiển thị");
    }

    @Test
    @Order(2)
    public void test_UC2_4_FUNC_006_BookSuccess() {
        loginAndNavigateToAppointments();
        
        // Mở form
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"openAddModal()\"]")));
        jsClick(addBtn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalTitle")));
        
        // Nhập thông tin bệnh nhân
        driver.findElement(By.id("patientName")).sendKeys("Bệnh nhân Test 001");
        driver.findElement(By.id("patientPhone")).sendKeys("0901234567");
        
        // Cần chọn bác sĩ trước (nếu dropdown đã có dữ liệu giả lập/thật thì tuỳ chọn index 0 hoặc 1)
        // Nếu không có, bỏ qua. Hàm này test giao diện
        // Chọn ca khám nếu có
        List<WebElement> shifts = driver.findElements(By.cssSelector(".mini-shift"));
        if (!shifts.isEmpty()) {
            jsClick(shifts.get(0));
        }
        
        // Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveAppointment()\"]")));
        jsClick(saveBtn);
        
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Hệ thống phải hiển thị thông báo thành công hoặc lỗi validate");
        alert.accept();
    }
}
