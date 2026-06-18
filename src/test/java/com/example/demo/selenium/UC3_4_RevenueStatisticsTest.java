package com.example.demo.selenium;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC3_4_RevenueStatisticsTest extends BaseTest {

    private void loginAndNavigateToRevenue() {
        // Admin truy cập Thống kê doanh thu
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/revenue.html");
        wait.until(ExpectedConditions.urlContains("revenue.html"));
    }

    @Test
    @Order(1)
    public void test_UC3_4_FUNC_001_AccessDashboard() {
        loginAndNavigateToRevenue();
        
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("revenuePageTitle")));
        assertTrue(pageTitle.isDisplayed(), "Màn hình Thống kê doanh thu phải hiển thị");
    }

    @Test
    @Order(2)
    public void test_UC3_4_FUNC_002_ShowKPI() {
        loginAndNavigateToRevenue();
        
        WebElement kpiNetRevenue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("kpiNetRevenue")));
        WebElement kpiCash = driver.findElement(By.id("kpiCash"));
        WebElement kpiInvoices = driver.findElement(By.id("kpiInvoices"));
        
        assertTrue(kpiNetRevenue.isDisplayed(), "Phải hiển thị Doanh thu thuần");
        assertTrue(kpiCash.isDisplayed(), "Phải hiển thị Tiền mặt tại két");
        assertTrue(kpiInvoices.isDisplayed(), "Phải hiển thị Số lượng hóa đơn");
    }

    @Test
    @Order(3)
    public void test_UC3_4_FUNC_007_ApproveShift() {
        loginAndNavigateToRevenue();
        
        WebElement tableBody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shiftTableBody")));
        assertTrue(tableBody.isDisplayed(), "Danh sách ca trực phải được hiển thị");
        
        // Nếu có ca trực chờ duyệt, test thao tác click
        List<WebElement> approveBtns = driver.findElements(By.xpath("//button[contains(@onclick, 'approveShift')]"));
        if (!approveBtns.isEmpty()) {
            jsClick(approveBtns.get(0));
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }
}
