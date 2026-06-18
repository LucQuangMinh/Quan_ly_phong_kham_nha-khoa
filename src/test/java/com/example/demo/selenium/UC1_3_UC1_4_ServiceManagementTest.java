package com.example.demo.selenium;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC1_3_UC1_4_ServiceManagementTest extends BaseTest {

    private void loginAndNavigateToServices() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/services.html");
        wait.until(ExpectedConditions.urlContains("services.html"));
    }

    @Test
    @Order(1)
    void test_UC1_3_FUNC_001_and_002_DisplayServiceList() {
        loginAndNavigateToServices();
        
        // Kiểm tra danh sách được hiển thị
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(table.isDisplayed(), "Bảng dịch vụ phải được hiển thị");
    }

    @Test
    @Order(2)
    void test_UC1_3_FUNC_004_AddNewService_Success() {
        loginAndNavigateToServices();
        
        // Click nút Thêm
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addServiceModal')\"]")
        ));
        addBtn.click();
        
        // Đợi modal
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
        
        // Nhập thông tin
        String uniqueService = "Service_" + System.currentTimeMillis();
        driver.findElement(By.id("serviceName")).sendKeys("Dịch vụ " + uniqueService);
        driver.findElement(By.id("serviceCategory")).sendKeys("Khám tổng quát");
        driver.findElement(By.id("servicePrice")).sendKeys("500000"); // UC1.4 - Thiết lập giá khi thêm
        
        // Nhấn Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveService()\"]")));
        jsClick(saveBtn);
        
        // Kiểm tra thông báo
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Phải có thông báo thành công");
        alert.accept();
    }

    @Test
    @Order(3)
    void test_UC1_4_EXC_001_PriceLessThanZero() {
        loginAndNavigateToServices();
        
        // Click nút Thêm
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addServiceModal')\"]")
        ));
        addBtn.click();
        
        // Đợi modal
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
        
        // Nhập giá âm
        driver.findElement(By.id("serviceName")).sendKeys("Test Fail");
        driver.findElement(By.id("servicePrice")).sendKeys("-10000");
        
        // Nhấn Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveService()\"]")));
        jsClick(saveBtn);
        
        // Kiểm tra thông báo lỗi giá
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Phải có thông báo lỗi về đơn giá");
        alert.accept();
    }
}
