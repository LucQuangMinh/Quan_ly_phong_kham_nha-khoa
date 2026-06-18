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
public class UC2_2_ShiftManagementTest extends BaseTest {

    private void loginAndNavigateToShifts() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/shifts.html");
        wait.until(ExpectedConditions.urlContains("shifts.html"));
    }

    @Test
    @Order(1)
    public void test_UC2_2_FUNC_001_OpenScreen() {
        loginAndNavigateToShifts();
        WebElement monthTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("monthTitle")));
        assertTrue(monthTitle.isDisplayed(), "Màn hình Thiết lập ca làm việc phải hiển thị");
    }

    @Test
    @Order(2)
    public void test_UC2_2_FUNC_002_EnableShift() {
        loginAndNavigateToShifts();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendarBody")));
        
        // Tìm nút ca Sáng đầu tiên chưa bật (giả sử có class btn-shift)
        List<WebElement> shiftBtns = driver.findElements(By.xpath("//button[contains(@onclick, 'toggleShift') and contains(text(), 'Sáng')]"));
        assertTrue(shiftBtns.size() > 0, "Phải có ca làm việc trên lịch");
        
        WebElement firstShift = shiftBtns.get(0);
        jsClick(firstShift);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void test_UC2_2_FUNC_003_DisableShift() {
        loginAndNavigateToShifts();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendarBody")));
        
        List<WebElement> shiftBtns = driver.findElements(By.xpath("//button[contains(@onclick, 'toggleShift') and contains(text(), 'Sáng')]"));
        assertTrue(shiftBtns.size() > 0, "Phải có ca làm việc trên lịch");
        
        // Nhấn lại để tắt
        WebElement firstShift = shiftBtns.get(0);
        jsClick(firstShift);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
