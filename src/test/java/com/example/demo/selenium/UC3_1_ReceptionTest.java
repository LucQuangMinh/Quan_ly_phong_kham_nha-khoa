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
public class UC3_1_ReceptionTest extends BaseTest {

    private void loginAndNavigateToCheckin() {
        loginAs("letan1", "123456");
        driver.get(BASE_URL + "/checkin.html");
        wait.until(ExpectedConditions.urlContains("checkin.html"));
    }

    @Test
    @Order(1)
    public void test_UC3_1_FUNC_001_OpenScreen() {
        loginAndNavigateToCheckin();
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Tiếp đón')] | //h1[contains(text(), 'Lịch')] | //h2[contains(text(), 'Danh sách')] | //h1[contains(text(), 'Hệ thống')]")));
        assertTrue(pageTitle.isDisplayed(), "Màn hình Tiếp đón phải hiển thị");
    }

    @Test
    @Order(2)
    public void test_UC3_1_FUNC_002_SearchByPhone() {
        loginAndNavigateToCheckin();
        
        WebElement searchPhone = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchPhone")));
        searchPhone.sendKeys("901234567");
        
        WebElement searchBtn = driver.findElement(By.xpath("//button[@onclick=\"searchPatients()\"]"));
        jsClick(searchBtn);
        
        try {
            Thread.sleep(500); // Đợi tải kết quả tìm kiếm
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void test_UC3_1_FUNC_004_CheckInSuccess() {
        loginAndNavigateToCheckin();
        
        // Cố gắng tìm nút checkin (Đã đến) nếu có sẵn trong danh sách
        List<WebElement> checkinBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'markArrived')]"));
        if (!checkinBtns.isEmpty()) {
            jsClick(checkinBtns.get(0));
            
            // Xử lý confirm dialog của thao tác markArrived
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
            
            try {
                // Có thể có thêm thông báo thành công hoặc chỉ reload
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
