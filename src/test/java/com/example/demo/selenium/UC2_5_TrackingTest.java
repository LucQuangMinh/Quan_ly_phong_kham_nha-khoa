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
public class UC2_5_TrackingTest extends BaseTest {

    private void loginAndNavigateToTracking() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/tracking.html");
        wait.until(ExpectedConditions.urlContains("tracking.html"));
    }

    @Test
    @Order(1)
    public void test_UC2_5_FUNC_002_CheckStatusTabs() {
        loginAndNavigateToTracking();
        
        // Kiểm tra hiển thị 4 tab trạng thái
        WebElement tabDaDat = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tabDaDat")));
        WebElement tabHangDoi = driver.findElement(By.id("tabHangDoi"));
        WebElement tabKhamXong = driver.findElement(By.id("tabKhamXong"));
        WebElement tabHuy = driver.findElement(By.id("tabHuy"));
        
        assertTrue(tabDaDat.isDisplayed(), "Phải hiển thị tab Đã đặt lịch");
        assertTrue(tabHangDoi.isDisplayed(), "Phải hiển thị tab Hàng chờ");
        assertTrue(tabKhamXong.isDisplayed(), "Phải hiển thị tab Khám xong");
        assertTrue(tabHuy.isDisplayed(), "Phải hiển thị tab Đã hủy");
    }

    @Test
    @Order(2)
    public void test_UC2_5_FUNC_009_CancelAppointment() {
        loginAndNavigateToTracking();
        
        // Chuyển sang tab Đã đặt lịch
        WebElement tabDaDat = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabDaDat")));
        jsClick(tabDaDat);
        
        // Cố gắng tìm nút hủy lịch
        List<WebElement> cancelBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'Bệnh nhân hủy')]"));
        if (!cancelBtns.isEmpty()) {
            jsClick(cancelBtns.get(0));
            
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            // Xác nhận popup confirm (Hủy)
            alert.accept();
            
            // Có thể có thông báo thành công tiếp theo
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert2 = driver.switchTo().alert();
                assertTrue(alert2.getText().length() > 0, "Hệ thống thông báo hủy thành công");
                alert2.accept();
            } catch (Exception e) {
                // Ignore if no second alert
            }
        }
    }
}
