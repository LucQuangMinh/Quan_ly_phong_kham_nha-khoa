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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC2_1_HolidayManagementTest extends BaseTest {

    private void loginAndNavigateToHolidays() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/holidays.html");
        wait.until(ExpectedConditions.urlContains("holidays.html"));
    }

    @Test
    @Order(1)
    public void test_UC2_1_FUNC_001_OpenScreen() {
        loginAndNavigateToHolidays();
        WebElement monthTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("monthTitle")));
        assertTrue(monthTitle.isDisplayed(), "Màn hình Thiết lập ngày nghỉ phải hiển thị tiêu đề tháng");
    }

    @Test
    @Order(2)
    public void test_UC2_1_FUNC_002_SetDayOff() {
        loginAndNavigateToHolidays();
        
        // Chờ lịch tải
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendarBody")));
        
        // Tìm các ô ngày có thể click (không phải thứ 7, CN vì có thể nó đã nghỉ mặc định)
        List<WebElement> dayCells = driver.findElements(By.xpath("//div[contains(@onclick, 'toggleHoliday')]"));
        assertTrue(dayCells.size() > 0, "Phải có ngày trên lịch");
        
        // Click vào ngày đầu tiên để thiết lập/hủy nghỉ
        WebElement firstDay = dayCells.get(0);
        jsClick(firstDay);
        
        // Chờ xử lý JS (hệ thống không hiển thị alert nếu thành công)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void test_UC2_1_FUNC_007_ChangeMonth() {
        loginAndNavigateToHolidays();
        
        WebElement monthTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("monthTitle")));
        String currentMonthText = monthTitle.getText();
        
        // Click tháng sau
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"nextMonth()\"]")));
        jsClick(nextBtn);
        
        try {
            Thread.sleep(1000); // Đợi js render
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        String newMonthText = driver.findElement(By.id("monthTitle")).getText();
        assertNotEquals(currentMonthText, newMonthText, "Tiêu đề tháng phải thay đổi khi bấm Tháng sau");
    }
}
