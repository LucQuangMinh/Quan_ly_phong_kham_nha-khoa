package com.example.demo.selenium;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC4_4_YearlySalaryTest extends BaseTest {

    private void loginAndNavigateToSalaryCalculator() {
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/revenue.html");
        wait.until(ExpectedConditions.urlContains("revenue.html"));
        
        WebElement tabBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("tab-btn-salary-calculator")));
        jsClick(tabBtn);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }

    @Test
    @Order(1)
    public void test_UC4_6_FUNC_003_SingleDoctorYearly() {
        loginAndNavigateToSalaryCalculator();
        
        Select modeSelect = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("salaryViewModeSelect"))));
        modeSelect.selectByValue("YEAR");
        
        Select doctorSelect = new Select(driver.findElement(By.id("salaryDoctorSelect")));
        if (doctorSelect.getOptions().size() > 1) {
            doctorSelect.selectByIndex(1);
        }
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        WebElement singleMode = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("salarySingleMode")));
        assertTrue(singleMode.isDisplayed(), "Phải hiển thị giao diện báo cáo cho một bác sĩ");
    }

    @Test
    @Order(2)
    public void test_UC4_7_FUNC_003_AllDoctorsYearly() {
        loginAndNavigateToSalaryCalculator();
        
        Select modeSelect = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("salaryViewModeSelect"))));
        modeSelect.selectByValue("YEAR");
        
        Select doctorSelect = new Select(driver.findElement(By.id("salaryDoctorSelect")));
        doctorSelect.selectByValue("ALL");
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        WebElement allMode = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("salaryAllMode")));
        assertTrue(allMode.isDisplayed(), "Phải hiển thị giao diện báo cáo tổng hợp năm");
    }
}
