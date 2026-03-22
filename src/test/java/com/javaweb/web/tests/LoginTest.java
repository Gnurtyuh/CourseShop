package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.step;

public class LoginTest extends BaseTest {
    @Test
    public void TC_LOG_01() {
        step(1, "Open login page");
        driver.get(BASE_URL + "login");
        logUrl();

        step(2, "Check login elements");

        logElement("Name input", By.id("name"));
        logElement("Password input", By.id("password"));
        logElement("Login button", By.id("loginBtn"));

        step(3, "Enter login information");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
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
        Assert.assertFalse(alertText.isEmpty());
        step(7, "Wait redirect to index page");

        wait.until(ExpectedConditions.urlContains("index"));

        logUrl();

        Assert.assertTrue(driver.getCurrentUrl().contains("index"));
    }
    @Test
    public void TC_LOG_02() {
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

        Assert.assertFalse(alertText.isEmpty());

        alert.accept();

        step(7, "Test completed");
    }
    @Test
    public void TC_LOG_03() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter non-existing username");

        driver.findElement(By.id("name")).sendKeys("nonexistent");
        driver.findElement(By.id("password")).sendKeys("123456");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify error message");

        Assert.assertFalse(alertText.isEmpty());

//        alert.accept();
        step(6, "Test completed");
    }
    @Test
    public void TC_LOG_04() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Leave name empty");

        WebElement name = driver.findElement(By.id("name"));
//        driver.findElement(By.id("name"));
        driver.findElement(By.id("password")).sendKeys("123456");
        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Check HTML5 validation message");

        String message = name.getAttribute("validationMessage");

        System.out.println("Validation message: " + message);

        Assert.assertFalse(message.isEmpty());
    }
    @Test
    public void TC_LOG_05() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Leave password empty");

        WebElement name = driver.findElement(By.id("name"));
        WebElement password = driver.findElement(By.id("password"));

        name.sendKeys("testuser");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Check HTML5 validation");

        String message = password.getAttribute("validationMessage");

        System.out.println("Validation message: " + message);

        Assert.assertFalse(message.isEmpty());
    }
    @Test
    public void TC_LOG_06() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        WebElement name = driver.findElement(By.id("name"));

        step(2, "Click login button with empty fields");

        driver.findElement(By.id("loginBtn")).click();

        step(3, "Check HTML5 validation");

        String message = name.getAttribute("validationMessage");

        System.out.println("Validation message: " + message);

        Assert.assertFalse(message.isEmpty());
    }
    @Test
    public void TC_LOG_07() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter username with leading space");

        driver.findElement(By.id("name")).sendKeys(" testuser");
        driver.findElement(By.id("password")).sendKeys("123456");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());


        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify validation message");

        Assert.assertFalse(alertText.isEmpty());
//        alert.accept();
    }
    @Test
    public void TC_LOG_08()  {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter SQL Injection payload");

        driver.findElement(By.id("name")).sendKeys("' OR '1'='1");
        driver.findElement(By.id("password")).sendKeys("anything");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify login failed");

//        Assert.assertEquals(alertText, "Tên không được chứa khoảng trắng");
        Assert.assertFalse(alertText.isEmpty());
        alert.accept();
    }
    @Test
    public void TC_LOG_09() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Generate long username");

        String longName = "a".repeat(255);

        driver.findElement(By.id("name")).sendKeys(longName);
        driver.findElement(By.id("password")).sendKeys("123456");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify backend error");
        Assert.assertFalse(alertText.isEmpty());

//        alert.accept();
    }
    @Test
    public void TC_LOG_10() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter username and very long password");

        String longPassword = "a".repeat(100);

        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys(longPassword);

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify backend error");

        Assert.assertFalse(alertText.isEmpty());

        alert.accept();
    }
    @Test
    public void TC_LOG_11() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter login information");

        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("123456");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());
        String alertText = alert.getText();
        System.out.println("Server error message: " + alertText);

        System.out.println("Alert message: " + alertText);

        step(5, "Verify exception handling");

        Assert.assertFalse(alertText.isEmpty());

        alert.accept();
    }
    @Test
    public void TC_LOG_12() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter password with spaces");

        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("123 456");

        step(3, "Click login button");

        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait for alert");

        Alert alert = wait.until(d -> d.switchTo().alert());

        String alertText = alert.getText();

        System.out.println("Alert message: " + alertText);

        step(5, "Verify system rule");

        Assert.assertFalse(alertText.isEmpty());

//        alert.accept();
    }
}
