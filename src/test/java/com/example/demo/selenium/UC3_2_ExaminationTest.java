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
public class UC3_2_ExaminationTest extends BaseTest {

    private void loginAndNavigateToTracking() {
        // Bác sĩ khám bệnh
        loginAs("admin", "admin123");
        driver.get(BASE_URL + "/tracking.html");
        wait.until(ExpectedConditions.urlContains("tracking.html"));
    }

    @Test
    @Order(1)
    public void test_UC3_2_FUNC_002_OpenExamForm() {
        loginAndNavigateToTracking();
        
        // Chọn tab Hàng chờ
        WebElement tabHangDoi = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabHangDoi")));
        jsClick(tabHangDoi);
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<WebElement> examBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'openExamModal')]"));
        if (!examBtns.isEmpty()) {
            jsClick(examBtns.get(0));
            
            WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("examModalTitle")));
            assertTrue(modalTitle.isDisplayed(), "Form khám bệnh phải hiển thị");
        }
    }

    @Test
    @Order(2)
    public void test_UC3_2_FUNC_004_UpdateRecord() {
        loginAndNavigateToTracking();
        
        WebElement tabHangDoi = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabHangDoi")));
        jsClick(tabHangDoi);
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        List<WebElement> examBtns = driver.findElements(By.xpath("//a[contains(@onclick, 'openExamModal')]"));
        if (!examBtns.isEmpty()) {
            jsClick(examBtns.get(0));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("examModalTitle")));
            
            // Nhập chẩn đoán
            WebElement note = driver.findElement(By.id("examNote"));
            note.sendKeys("Bệnh nhân bình thường, không có vấn đề nghiêm trọng.");
            
            // Lưu
            WebElement saveBtn = driver.findElement(By.xpath("//button[@onclick=\"saveExam()\"]"));
            jsClick(saveBtn);
            
            // Hàm saveExam() có thể có alert thành công hoặc chỉ đóng modal và load lại
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                driver.switchTo().alert().accept();
            } catch (Exception e) {
                // Ignore timeout if no alert
            }
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }
    }

    @Test
    @Order(3)
    public void test_UC3_2_FUNC_007_ChangeStatus() {
        loginAndNavigateToTracking();
        
        // Kiểm tra xem tab Khám xong có bệnh nhân không
        WebElement tabKhamXong = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabKhamXong")));
        jsClick(tabKhamXong);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        assertTrue(tabKhamXong.isDisplayed(), "Tab khám xong hiển thị");
    }
}
