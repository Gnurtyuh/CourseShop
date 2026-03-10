package com.javaweb.web.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    WebDriver driver;

    @BeforeEach
    void setup() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.get("http://localhost:63342/CourseShop/web/fe/login.html");
    }

    @AfterEach
    void teardown() {

        driver.quit();
    }

    // LOG-01
    @Test
    void loginSuccess() {

        driver.findElement(By.id("name")).sendKeys("Use");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.tagName("button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        assertEquals("Đăng nhập thành công!", alert.getText());

        alert.accept();
    }

    // LOG-02
    @Test
    void wrongPassword() {

        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");

        driver.findElement(By.tagName("button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        assertTrue(alert.getText().contains("Sai"));

        alert.accept();
    }

    // LOG-04
    @Test
    void emptyName() {

        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.tagName("button")).click();

        String msg = driver.findElement(By.id("name"))
                .getAttribute("validationMessage");

        assertTrue(msg.length() > 0);
    }

    // LOG-05
    @Test
    void emptyPassword() {

        driver.findElement(By.id("name")).sendKeys("testuser");

        driver.findElement(By.tagName("button")).click();

        String msg = driver.findElement(By.id("password"))
                .getAttribute("validationMessage");

        assertTrue(msg.length() > 0);
    }

    // LOG-07
    @Test
    void usernameWithSpaces() {

        driver.findElement(By.id("name")).sendKeys(" testuser ");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.tagName("button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        assertTrue(alert.getText().contains("thành công"));

        alert.accept();
    }

    // LOG-08
    @Test
    void sqlInjectionLogin() {

        driver.findElement(By.id("name")).sendKeys("' OR '1'='1");
        driver.findElement(By.id("password")).sendKeys("anything");

        driver.findElement(By.tagName("button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        assertTrue(alert.getText().contains("Sai"));

        alert.accept();
    }
}