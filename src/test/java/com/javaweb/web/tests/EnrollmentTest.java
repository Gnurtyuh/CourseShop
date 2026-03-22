package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnrollmentTest extends BaseTest {
    @Test
    public void TC_ENR_16() {
        step(1, "Open login page");
        driver.get(BASE_URL + "login");
        logUrl();

        step(2, "Check login elements");

        logElement("Name input", By.id("name"));
        logElement("Password input", By.id("password"));
        logElement("Login button", By.id("loginBtn"));

        step(3, "Enter login information");

        driver.findElement(By.id("name")).sendKeys("TestUser");
        driver.findElement(By.id("password")).sendKeys("123456@Abc");

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
        step(8, "Open enrolled courses page");
        driver.get(BASE_URL + "my-courses");

        step(9, "Wait for message");

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("no-courses")
                )
        );

        String text = message.getText();

        System.out.println("Displayed message: " + text);

        step(10, "Verify message");

        Assert.assertFalse(alertText.isEmpty());
    }
    @Test
    public void TC_ENR_17() {
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
        step(1, "Open course list page");
        driver.get(BASE_URL + "my-courses");

        step(2, "Wait for detail button");

        WebElement detailBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn-detail")
                )
        );

        step(3, "Click detail button");

        detailBtn.click();

        step(4, "Verify redirect to course detail");

        wait.until(ExpectedConditions.urlContains("course?id="));

        String currentUrl = driver.getCurrentUrl();

        System.out.println("Current URL: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("course?id="));
    }
    @Test
    public void TC_ENR_18() {

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
        step(1, "Open course list page");
        driver.get(BASE_URL + "my-courses");

        step(2, "Wait for detail button");

        WebElement detailBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn-watch")
                )
        );

        step(3, "Click detail button");

        detailBtn.click();

        step(4, "Verify redirect to course detail");

        wait.until(ExpectedConditions.urlContains("course-video?courseId="));

        String currentUrl = driver.getCurrentUrl();

        System.out.println("Current URL: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("course-video?courseId="));
    }
    @Test
    public void TC_ENR_19() {
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
        step(6, "Open course with invalid id");

        driver.get(BASE_URL + "course?id=-1");

        step(7, "Wait notification message");

        WebElement notification = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("notification")
                )
        );

        wait.until(ExpectedConditions.textToBePresentInElement(
                notification,
                "Không thể tải thông tin khóa học!"
        ));

        String message = notification.getText();

        System.out.println("Error message: " + message);

        step(8, "Verify error message");

        Assert.assertEquals(message, "Không thể tải thông tin khóa học!");
    }
    @Test
    public void TC_ENR_20() {
        driver.get(BASE_URL + "login");
        logUrl();
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "Đăng nhập thành công!");
        alert.accept();

        logLocalStorage("userToken");
        String token = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('userToken');");
        Assert.assertNotNull(token);
        Assert.assertFalse(alertText.isEmpty());
        wait.until(ExpectedConditions.urlContains("index"));
        logUrl();
        Assert.assertTrue(driver.getCurrentUrl().contains("index"));
        driver.get(BASE_URL + "course?id=14");
        WebElement buyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("btn-buy-course")
                )
        );

        buyBtn.click();

        Alert alert2 = wait.until(driver -> driver.switchTo().alert());
        String message = alert2.getText();
        alert2.accept();

        driver.get(BASE_URL + "my-courses");

        WebElement course = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".course-card")
                )
        );
        Assert.assertTrue(course.isDisplayed());
    }
    @Test
    public void TC_ENR_21() {
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
        step(1, "Open course detail");

        driver.get(BASE_URL + "course?id=14");
        // refresh trang
        driver.navigate().refresh();
    }
    @Test
    public void TC_ENR_22() {

        driver.get(BASE_URL + "login");

        // login
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("index"));

        // mở menu user
        wait.until(ExpectedConditions.elementToBeClickable(By.id("user-name"))).click();

        // logout
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout-btn"))).click();

        wait.until(ExpectedConditions.urlContains("index"));

        // click login
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-login"))).click();

        wait.until(ExpectedConditions.urlContains("login"));

        // login lại
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlContains("index"));

        driver.get(BASE_URL + "my-courses");
    }
    @Test
    public void TC_ENR_23() {

        driver.findElement(By.name("username")).sendKeys("user1");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        driver.get(BASE_URL + "my-courses");

        boolean courseVisible =
                driver.findElements(By.xpath("//div[contains(text(),'Course 1')]")).size() > 0;

        assertTrue(courseVisible);
    }
    @Test
    public void TC_ENR_24(){
        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        wait.until(ExpectedConditions.urlContains("index"));

        // truy cập lesson
        driver.get(BASE_URL + "course?id=1");

    }
    //
    @Test
    public void TC_ENR_01() {
        driver.get(STR."\{BASE_URL}login");

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
    public void TC_ENR_02() {

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
    public void TC_ENR_03() {

        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("TestUser");
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
    public void TC_ENR_04() {

        driver.get(STR."\{BASE_URL}login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        driver.get(STR."\{COURSE_PAGE}14");

        WebElement buyBtn = driver.findElement(By.id("btn-watch-course"));
        buyBtn.click();

    }
    @Test
    public void TC_ENR_05() throws InterruptedException {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        driver.get(BASE_URL + "course?id=13");

        WebElement buyBtn = driver.findElement(By.id("btn-buy-course"));

        buyBtn.click();
        buyBtn.click();
        buyBtn.click();

        String buttonText = buyBtn.getText();

        Assert.assertFalse(buttonText.isEmpty());
        tearDown();
    }
    @Test
    public void TC_ENR_06() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=18");

        WebElement buyBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("btn-buy-course"))
        );

        buyBtn.click();
    }
    @Test
    public void TC_ENR_07() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=13");

    }
    @Test
    public void TC_ENR_08() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=13");

    }
    @Test
    public void TC_ENR_09() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=13");

        WebElement viewBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-watch-course"))
        );

        viewBtn.click();

//        wait.until(ExpectedConditions.urlContains("course-video"));
    }
    @Test
    public void TC_ENR_10() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=999");

    }
    @Test
    public void TC_ENR_11() {


        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=null");
    }
    @Test
    public void TC_ENR_12() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course?id=13");

        WebElement buyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-buy-course"))
        );

        buyBtn.click();

        WebElement notification = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("notification"))
        );

        Assert.assertTrue(
                notification.getText().contains("Lỗi") ||
                        notification.getText().contains("Server error")
        );
    }
    @Test
    public void TC_ENR_13() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "course-video?id=13");
    }
    @Test
    public void TC_ENR_14() {

        driver.get(BASE_URL);

        ((JavascriptExecutor) driver)
                .executeScript("localStorage.removeItem('userToken');");

        driver.get(BASE_URL + "my-courses");

        wait.until(ExpectedConditions.urlContains("login"));

        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
    @Test
    public void TC_ENR_15() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        driver.get(BASE_URL + "my-courses");

    }
    @Test
    public void TC_ENR_25() {

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("loginBtn")).click();

        driver.get(BASE_URL + "my-courses");

        WebElement notification = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("notification"))
        );

        Assert.assertTrue(
                notification.getText().contains("Lỗi khi tải danh sách khóa học")
        );
    }
}
