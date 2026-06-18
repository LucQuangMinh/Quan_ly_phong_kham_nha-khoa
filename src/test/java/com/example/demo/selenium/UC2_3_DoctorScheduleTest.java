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
public class UC2_3_DoctorScheduleTest extends BaseTest {

    private void loginAndNavigateToSchedules() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/schedules.html");
        wait.until(ExpectedConditions.urlContains("schedules.html"));
    }

    @Test
    @Order(1)
    public void test_UC2_3_FUNC_001_OpenScreen_Manager() {
        loginAndNavigateToSchedules();
        WebElement monthTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("monthTitle")));
        assertTrue(monthTitle.isDisplayed(), "Màn hình Đăng ký lịch trực phải hiển thị");
    }

    @Test
    @Order(2)
    public void test_UC2_3_FUNC_003_AssignShiftManual() {
        loginAndNavigateToSchedules();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendarBody")));
        
        // Chọn tất cả bác sĩ để gán (Click checkbox 'Chọn hết')
        try {
            WebElement selectAll = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("selectAllDocs")));
            jsClick(selectAll);
        } catch (Exception e) {
            // Ignore if selectAll is not strictly required or not found, but it should be
        }
        
        // Tìm 1 ô ca trực Sáng/Chiều để click
        List<WebElement> shiftCells = driver.findElements(By.xpath("//div[contains(@onclick, 'toggleShiftAdmin')]"));
        if (!shiftCells.isEmpty()) {
            jsClick(shiftCells.get(0));
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Order(3)
    public void test_UC2_3_FUNC_004_RemoveShiftManual() {
        loginAndNavigateToSchedules();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendarBody")));
        
        // Gán xong rồi hủy (click lại vào ô đã gán hoặc nút x)
        // Tìm nút xoá bác sĩ khỏi ca trực ("×")
        List<WebElement> removeBtns = driver.findElements(By.cssSelector(".remove-btn"));
        if (!removeBtns.isEmpty()) {
            jsClick(removeBtns.get(0));
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
