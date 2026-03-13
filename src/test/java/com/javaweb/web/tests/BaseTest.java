package com.javaweb.web.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "http://localhost:8080/";
    protected static final String REGISTER_PAGE ="http://localhost:8080/register";

    @BeforeMethod
    public void setup() {

        System.setProperty("webdriver.gecko.driver", "D:\\geckodriver-v0.36.0-win64\\geckodriver.exe");

        FirefoxOptions options = new FirefoxOptions();

        driver = new FirefoxDriver(options);

        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    }
    protected void step(int number, String message) {
        System.out.println("STEP " + number + ": " + message);
    }
    protected void logUrl() {
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }

    protected void logElement(String name, By locator) {
        if (driver.findElements(locator).size() > 0) {
            System.out.println("Element found: " + name);
        } else {
            System.out.println("Element NOT found: " + name);
        }
    }

    protected void logLocalStorage(String key) {
        String value = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem(arguments[0]);", key);

        System.out.println("LocalStorage [" + key + "] = " + value);
    }
//    @AfterMethod
//    public void tearDown() {
//        if (driver != null) {
//
//            driver.quit();
//        }
//    }
@AfterMethod
public void tearDown() throws InterruptedException {
    Thread.sleep(8000);

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
            wait.until(ExpectedConditions.urlContains("login"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}