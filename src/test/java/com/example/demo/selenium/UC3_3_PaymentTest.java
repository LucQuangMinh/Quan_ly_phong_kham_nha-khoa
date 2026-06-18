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
public class UC3_3_PaymentTest extends BaseTest {

    private void loginAndNavigateToCheckin() {
        loginAs("letan1", "123456");
        driver.get(BASE_URL + "/checkin.html");
        wait.until(ExpectedConditions.urlContains("checkin.html"));
    }

    @Test
    @Order(1)
    public void test_UC3_3_FUNC_002_OpenPaymentForm() {
        loginAndNavigateToCheckin();
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<WebElement> payBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'openPaymentModal')]"));
        if (!payBtns.isEmpty()) {
            jsClick(payBtns.get(0));
            
            WebElement payAmountGiven = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payAmountGiven")));
            assertTrue(payAmountGiven.isDisplayed(), "Form thanh toán phải hiển thị");
        }
    }

    @Test
    @Order(2)
    public void test_UC3_3_FUNC_005_PaymentSuccess() {
        loginAndNavigateToCheckin();
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<WebElement> payBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'openPaymentModal')]"));
        if (!payBtns.isEmpty()) {
            jsClick(payBtns.get(0));
            
            WebElement payAmountGiven = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payAmountGiven")));
            payAmountGiven.sendKeys("500000"); // Nhập số tiền
            
            WebElement submitBtn = driver.findElement(By.xpath("//button[@onclick=\"submitPayment()\"]"));
            jsClick(submitBtn);
            
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                driver.switchTo().alert().accept();
            } catch (Exception e) {}
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }
}
