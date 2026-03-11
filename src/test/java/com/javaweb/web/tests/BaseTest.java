package com.javaweb.web.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "http://localhost:8080/web/templates";
    protected static final String REGISTER_PAGE ="http://localhost:8080/register";

    @BeforeSuite
    public void setupSuite() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(REGISTER_PAGE);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void enterRegistrationData(String username, String email, String password) {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));

        usernameField.clear();
        emailField.clear();
        passwordField.clear();

        if (username != null) {
            usernameField.sendKeys(username);
        }
        if (email != null) {
            emailField.sendKeys(email);
        }
        if (password != null) {
            passwordField.sendKeys(password);
        }
    }

    protected void submitForm() {
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
    }

    protected String getAlertMessage() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean isRedirectedToLoginPage() {
        try {
            wait.until(ExpectedConditions.urlContains("login.html"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}