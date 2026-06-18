package com.example.demo.selenium;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UC1_1_UserManagementTest extends BaseTest {

    private void loginAndNavigateToUsers() {
        loginAs("admin", "admin123");
        // Kiểm tra xem đã ở trang users.html chưa
        wait.until(ExpectedConditions.urlContains("users.html"));
    }

    @Test
    @Order(1)
    void test_UC1_1_FUNC_001_and_002_DisplayUserList() {
        loginAndNavigateToUsers();
        
        // Kiểm tra danh sách được hiển thị
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usersTable")));
        assertTrue(table.isDisplayed(), "Bảng người dùng phải được hiển thị");
    }

    @Test
    @Order(2)
    void test_UC1_1_FUNC_003_AddNewUser_Success() {
        loginAndNavigateToUsers();
        
        // Click nút Thêm
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addUserModal')\"]")
        ));
        addBtn.click();
        
        // Chờ modal mở
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addUserModal")));
        
        // Nhập thông tin
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        driver.findElement(By.id("username")).sendKeys(uniqueUsername);
        driver.findElement(By.id("password")).sendKeys("Test@1234");
        driver.findElement(By.id("fullname")).sendKeys("Nguyễn Văn Test");
        driver.findElement(By.id("email")).sendKeys(uniqueUsername + "@test.com");
        driver.findElement(By.id("phone")).sendKeys("0123456789");
        
        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByVisibleText("Lễ tân");
        
        // Click Lưu (giả sử có nút submit trong form hoặc nút Lưu trong modal actions)
        // Dựa vào HTML modal actions
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveUser()\"]")));
        jsClick(saveBtn);
        
        // Chờ alert báo thành công
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Phải có thông báo thành công");
        alert.accept();
    }

    @Test
    @Order(3)
    void test_UC1_1_EXC_001_AddUser_EmptyRequiredFields() {
        loginAndNavigateToUsers();
        
        // Click nút Thêm
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@onclick=\"openModal('addUserModal')\"]")
        ));
        addBtn.click();
        
        // Chờ modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addUserModal")));
        
        // Để trống các trường bắt buộc và bấm Lưu
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@onclick=\"saveUser()\"]")));
        jsClick(saveBtn);
        
        // Chờ alert báo lỗi validation
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().length() > 0, "Phải có thông báo yêu cầu nhập đầy đủ");
        alert.accept();
    }
}
