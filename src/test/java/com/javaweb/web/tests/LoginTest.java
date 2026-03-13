package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.step;

public class LoginTest extends BaseTest {
    @Test
    public void TC_LOG_01_LoginSuccess() {
        step(1, "Open login page");
        driver.get(BASE_URL + "login");
        logUrl();

        step(2, "Check login elements");

        logElement("Name input", By.id("name"));
        logElement("Password input", By.id("password"));
        logElement("Login button", By.id("loginBtn"));

        step(3, "Enter login information");

        driver.findElement(By.id("name")).sendKeys("Thynh");
        driver.findElement(By.id("password")).sendKeys("123456");

        step(4, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(5, "Wait for alert");

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        System.out.println("Alert message: " + alertText);

        Assert.assertEquals(alertText, "Đăng nhập thành công!");

        alert.accept();

        step(6, "Check token in localStorage");

        logLocalStorage("userToken");

        String token = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('userToken');");

        Assert.assertNotNull(token);

        step(7, "Wait redirect to index page");

        wait.until(ExpectedConditions.urlContains("index"));

        logUrl();

        Assert.assertTrue(driver.getCurrentUrl().contains("index"));
    }
    @Test
    public void TC_LOG_02_WrongPassword() {
        step(1, "Open login page");
        driver.get(BASE_URL + "login");
        System.out.println("Current URL: " + driver.getCurrentUrl());

        step(2, "Check login elements");

        if(driver.findElements(By.id("name")).size() > 0)
            System.out.println("Element found: Name input");
        else
            System.out.println("Element NOT found: Name input");

        if(driver.findElements(By.id("password")).size() > 0)
            System.out.println("Element found: Password input");
        else
            System.out.println("Element NOT found: Password input");

        if(driver.findElements(By.id("loginBtn")).size() > 0)
            System.out.println("Element found: Login button");
        else
            System.out.println("Element NOT found:Login button");

        step(3, "Enter wrong login information");

        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");

        step(4, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(5, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(6, "Verify error message");

        Assert.assertEquals(alertText, "Sai email hoặc mật khẩu!");

        alert.accept();

        step(7, "Test completed");
    }
}
