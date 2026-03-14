package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class EnrollmentTest extends BaseTest {
    @Test
    public void testBuyCourseSuccess() throws InterruptedException {
        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.get(STR."\{COURSE_PAGE}14");

        WebElement buyBtn = driver.findElement(By.id("btn-buy-course"));
        buyBtn.click();

        WebElement toast = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#notification"))
        );

        String alertMessage = toast.getText();

        Assert.assertTrue(
                alertMessage.contains("Mua khóa học thành công!"),
                "Mua khóa học thành công!"
        );

        // đợi trang reload
        wait.until(ExpectedConditions.stalenessOf(toast));
    }
    @Test
    public void testBuyCourseWithoutLogin() {

        driver.get(STR."\{COURSE_PAGE}13");

        driver.findElement(By.id("btn-buy-course")).click();
        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");
        Assert.assertTrue(alertMessage.contains("Vui lòng đăng nhập để mua khóa học"),
                "Vui lòng đăng nhập để mua khóa học");


        Assert.assertTrue(isRedirectedToLoginPage(),
                "Should redirect to login page");
    }
    @Test
    public void testBuyCourseBalanceNotEnough() {

        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("123456@Abc");

        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.get(STR."\{COURSE_PAGE}14");

        WebElement buyBtn = driver.findElement(By.id("btn-buy-course"));
        buyBtn.click();

        WebElement toast = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#notification"))
        );

        String alertMessage = toast.getText();

        Assert.assertTrue(
                alertMessage.contains("Số dư không đủ! Vui lòng nạp thêm tiền."),
                "Số dư không đủ! Vui lòng nạp thêm tiền."
        );
    }
    @Test
    public void testBuyCourseAlreadyPurchased() {

        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.get(STR."\{COURSE_PAGE}14");

        WebElement buyBtn = driver.findElement(By.id("btn-buy-course"));
        buyBtn.click();

        WebElement toast = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#notification"))
        );

        String alertMessage = toast.getText();

        Assert.assertTrue(
                alertMessage.contains("Mua khóa học thành công!"),
                "Mua khóa học thành công!"
        );

        // đợi trang reload
        wait.until(ExpectedConditions.stalenessOf(toast));
    }
    @Test
    public void testClickBuyMultipleTimes() {
        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.get(STR."\{COURSE_PAGE}14");

        WebElement buyBtn = driver.findElement(By.id("btn-buy-course"));
        buyBtn.click();
        buyBtn.click();

        Assert.assertFalse(buyBtn.isEnabled());

    }
}
