package com.example.demo.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Chạy ẩn trình duyệt
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Tối đa hoá cửa sổ cho chắc chắn
        driver.manage().window().maximize();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Hàm tiện ích đăng nhập
    protected void loginAs(String username, String password) {
        driver.get(BASE_URL + "/login.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("submitBtn")).click();
        
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.html")));
        } catch (org.openqa.selenium.TimeoutException e) {
            WebElement errorMsg = driver.findElement(By.id("errorMessage"));
            if (errorMsg.isDisplayed()) {
                throw new RuntimeException("Login failed with message: " + errorMsg.getText());
            }
            throw e;
        }
    }

    protected void jsClick(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
