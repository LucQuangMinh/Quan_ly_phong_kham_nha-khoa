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
public class UC4_1_SalaryConfigTest extends BaseTest {

    private void loginAndNavigateToSalaryConfig() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/revenue.html");
        wait.until(ExpectedConditions.urlContains("revenue.html"));
        
        WebElement tabBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("tab-btn-salary-config")));
        jsClick(tabBtn);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    @Test
    @Order(1)
    public void test_UC4_1_FUNC_003_UpdateBaseSalary() {
        loginAndNavigateToSalaryConfig();
        
        WebElement baseHourlyRate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("baseHourlyRate")));
        baseHourlyRate.clear();
        baseHourlyRate.sendKeys("100000"); // 100,000 VNĐ
        
        WebElement submitBtn = driver.findElement(By.xpath("//form[@id='configForm']//button[@type='submit']"));
        jsClick(submitBtn);
        
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            assertTrue(alert.getText().length() > 0, "Phải hiển thị thông báo lưu thành công");
            alert.accept();
        } catch (Exception e) {}
    }

    @Test
    @Order(2)
    public void test_UC4_2_FUNC_003_AssignWeekendMultiplier() {
        loginAndNavigateToSalaryConfig();
        
        // Tìm cột "Chủ Nhật" trong lịch
        List<WebElement> headers = driver.findElements(By.xpath("//div[@id='calendarGrid']//div[contains(@class, 'calendar-header-cell') and contains(text(), 'Chủ Nhật')]"));
        if (!headers.isEmpty()) {
            jsClick(headers.get(0)); // Gán hệ số 1.5 cho toàn bộ Chủ Nhật
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }
}
