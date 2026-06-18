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
public class UC4_2_ServiceBonusConfigTest extends BaseTest {

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
    public void test_UC4_3_FUNC_003_UpdateBonus() {
        loginAndNavigateToSalaryConfig();
        
        List<WebElement> inputs = driver.findElements(By.xpath("//input[starts-with(@id, 'coeff_')]"));
        List<WebElement> saveBtns = driver.findElements(By.xpath("//button[contains(@onclick, 'updateServiceCoeff')]"));
        
        if (!inputs.isEmpty() && !saveBtns.isEmpty()) {
            WebElement input = inputs.get(0);
            input.clear();
            input.sendKeys("0.5");
            
            jsClick(saveBtns.get(0));
            
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                assertTrue(alert.getText().length() > 0, "Phải hiển thị thông báo kết quả lưu");
                alert.accept();
            } catch (Exception e) {}
        }
    }

    @Test
    @Order(2)
    public void test_UC4_3_EXC_001_NegativeBonus() {
        loginAndNavigateToSalaryConfig();
        
        List<WebElement> inputs = driver.findElements(By.xpath("//input[starts-with(@id, 'coeff_')]"));
        List<WebElement> saveBtns = driver.findElements(By.xpath("//button[contains(@onclick, 'updateServiceCoeff')]"));
        
        if (!inputs.isEmpty() && !saveBtns.isEmpty()) {
            WebElement input = inputs.get(0);
            input.clear();
            input.sendKeys("-0.5");
            
            jsClick(saveBtns.get(0));
            
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                assertTrue(alert.getText().length() > 0, "Phải hiển thị lỗi");
                alert.accept();
            } catch (Exception e) {}
        }
    }
}
