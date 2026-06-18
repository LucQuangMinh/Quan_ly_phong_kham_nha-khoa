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
public class UC1_2_DoctorManagementTest extends BaseTest {

    private void loginAndNavigateToDoctors() {
        loginAs("admin", "admin123");
        // Giả sử có menu Bác sĩ để click hoặc navigate thẳng
        driver.get(BASE_URL + "/doctors.html");
        wait.until(ExpectedConditions.urlContains("doctors.html"));
    }

    @Test
    @Order(1)
    void test_UC1_2_FUNC_001_and_002_DisplayDoctorList() {
        loginAndNavigateToDoctors();
        
        // Kiểm tra danh sách được hiển thị
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(table.isDisplayed(), "Bảng bác sĩ phải được hiển thị");
    }

    @Test
    @Order(2)
    void test_UC1_2_FUNC_003_AddNewDoctor_Success() {
        loginAndNavigateToDoctors();
        
        // Click nút Thêm (tìm theo text)
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addDocModal')\"]")
        ));
        addBtn.click();
        
        // Đợi modal
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
        
        // Nhập thông tin
        String uniqueDoc = "Doc_" + System.currentTimeMillis();
        driver.findElement(By.id("docName")).sendKeys("Bác sĩ " + uniqueDoc);
        driver.findElement(By.id("docEmail")).sendKeys(uniqueDoc + "@clinic.com");
        driver.findElement(By.id("docPhone")).sendKeys("0999888777");
        driver.findElement(By.id("docWorkplace")).sendKeys("Phòng khám");
        
        // Nhấn Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveDoc()\"]")));
        jsClick(saveBtn);
        
        // Kiểm tra thông báo
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Phải có thông báo thành công");
        alert.accept();
    }

    @Test
    @Order(3)
    void test_UC1_2_EXC_001_AddDoctor_EmptyFields() {
        loginAndNavigateToDoctors();
        
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addDocModal')\"]")
        ));
        addBtn.click();
        
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal")));
        
        // Để trống các trường và bấm Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveDoc()\"]")));
        jsClick(saveBtn);
        
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, 
            "Phải cảnh báo thiếu dữ liệu");
        alert.accept();
    }
}
