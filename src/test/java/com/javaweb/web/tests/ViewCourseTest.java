package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static com.javaweb.web.tests.BaseTest.BASE_URL;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.step;

public class ViewCourseTest extends BaseTest {
    @Test
    public void TC_VIEW_01_ShowCourseList() {

        step(1, "Open index page");

        driver.get(BASE_URL + "index");
        logUrl();

        step(2, "Wait for course items render");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("course-item")));

        step(3, "Count course items");

        int courseCount = driver.findElements(By.className("course-item")).size();

        System.out.println("Course count: " + courseCount);

        step(4, "Verify courses displayed");

        Assert.assertTrue(courseCount > 0);

    }
    @Test
    public void TC_VIEW_03_LoadCourseError() {

        step(1, "Open index page");

        driver.get(BASE_URL + "index");
        logUrl();

        step(2, "Wait notification appear");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification")));

        step(3, "Get notification message");

        String message = driver.findElement(By.id("notification")).getText();

        System.out.println("Notification message: " + message);

        step(4, "Verify error message");

        Assert.assertEquals(message, "Lỗi tải khóa học!");

    }

    @Test
    public void TC_VIEW_02_NoCourseAvailable() {

        step(1, "Open index page with empty courses");

        driver.get(BASE_URL + "index");
        logUrl();

        step(2, "Wait message");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("no-course-msg")));

        step(3, "Get message");

        String message = driver.findElement(By.id("no-course-msg")).getText();

        System.out.println("Message: " + message);

        step(4, "Verify message");

        Assert.assertEquals(message, "Không có khóa học nào.");
    }

    @Test
    public void TC_VIEW_04_ClickCourseDetail() {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");
        logUrl();

        step(2, "Wait course list");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("courses-container")));

        step(3, "Click detail button");
        WebElement detailBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-detail"))
        );

        detailBtn.click();

        step(4, "Wait redirect");
        wait.until(ExpectedConditions.urlContains("course"));

        logUrl();

        step(5, "Verify course id");
        Assert.assertTrue(driver.getCurrentUrl().contains("id="));
    }


    @Test
    public void TC_VIEW_05_ShowPurchasedCourses() {

        step(1, "Login first");

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("TenNguoiDung");
        driver.findElement(By.id("password")).sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(ExpectedConditions.urlContains("index"));

        step(2, "Open my-courses page");

        driver.get(BASE_URL + "my-courses");

        step(3, "Wait courses render");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.id("my-courses-list")),
                ExpectedConditions.visibilityOfElementLocated(By.id("no-courses"))
        ));

        step(4, "Verify purchased courses");

        boolean hasCourse =
                driver.findElements(By.cssSelector(".course-card")).size() > 0;

        boolean noCourseMsg =
                driver.findElement(By.id("no-courses")).isDisplayed();

        Assert.assertTrue(hasCourse || noCourseMsg);

    }

    @Test
    public void TC_VIEW_06_ShowBuyButtonWhenNotPurchased() {

        step(1, "Open index page");

        driver.get(BASE_URL + "index");

        step(2, "Wait courses render");

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector(".course-item"), 0));

        step(3, "Check buy button");

        WebElement buyBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".btn-buy")
                )
        );

        Assert.assertTrue(buyBtn.isDisplayed());
    }

    @Test
    public void TC_VIEW_07_LoadMoreCourses() {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");

        step(2, "Wait courses render");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("courses-container")));

        step(3, "Count courses before click");

        int before = driver.findElements(By.cssSelector(".course-item")).size();

        step(4, "Click 'Xem thêm'");

        WebElement loadMore = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-load-more"))
        );
        loadMore.click();

        step(5, "Wait more courses");

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector(".course-item"), before
        ));

        int after = driver.findElements(By.cssSelector(".course-item")).size();

        step(6, "Verify more courses");

        Assert.assertTrue(after > before);
    }


    @Test
    public void TC_VIEW_08_SearchCourseByName() {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");

        step(2, "Wait courses render");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("courses-container")));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.className("course-item"), 0));

        step(3, "Enter keyword");

        driver.findElement(By.id("search-input")).sendKeys("java");

        step(4, "Click search");

        driver.findElement(By.id("search-btn")).click();

        step(5, "Verify result");

        List<WebElement> results = driver.findElements(By.className("course-item"));

        Assert.assertTrue(results.size() > 0, "Không tìm thấy khóa học chứa 'java'");
    }

    @Test
    public void TC_VIEW_09_SearchCourseByInstructor() {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");

        step(2, "Wait courses render");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("courses-container")));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.className("course-item"), 0));

        step(3, "Enter instructor keyword");

        driver.findElement(By.id("search-input")).sendKeys("Admin");

        step(4, "Click search");

        driver.findElement(By.id("search-btn")).click();

        step(5, "Verify result");

        List<WebElement> instructors = driver.findElements(
                By.xpath("//p[contains(text(),'Giảng viên')]"));

        boolean found = false;

        for (WebElement ins : instructors) {
            if (ins.getText().toLowerCase().contains("Admin")) {
                found = true;
                break;
            }
        }

        Assert.assertTrue(found, "Không tìm thấy khóa học của giảng viên admin");
    }

    @Test
    public void TC_VIEW_10_SearchNoResult() {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");

        step(2, "Wait courses render");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("courses-container")));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.className("course-item"), 0));

        step(3, "Enter keyword xyz");

        driver.findElement(By.id("search-input")).sendKeys("xyz");

        step(4, "Click search");

        driver.findElement(By.id("search-btn")).click();

        step(5, "Verify no course message");

        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("no-course-msg")));

        Assert.assertTrue(msg.getText().contains("Không có khóa học nào"),
                "Không hiển thị thông báo khi không có kết quả tìm kiếm");
    }

    @Test
    public void TC_VIEW_11_ViewCourseDetail() {

        driver.get(BASE_URL + "course?id=12");

        step(1, "Wait course title load");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBe(
                        By.id("course-title"),
                        "Đang tải..."
                )
        ));

        step(2, "Wait instructor load");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBe(
                        By.id("course-instructor"),
                        ""
                )
        ));

        step(3, "Verify course information");

        Assert.assertFalse(driver.findElement(By.id("course-title")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-description")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-instructor")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-price")).getText().isEmpty());
    }

    @Test
    public void TC_VIEW_12_InvalidCourseId() {

        step(1, "Open invalid course page");

        driver.get(BASE_URL + "course?id=999");

        step(2, "Wait error message");

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("course-title")));

        step(3, "Verify error message");

        Assert.assertTrue(title.getText().contains("Lỗi tải khóa học"),
                "Không hiển thị thông báo lỗi khi courseId không tồn tại");
    }

    @Test
    public void TC_VIEW_13_NoCourseId() {

        step(1, "Open course page without id");

        driver.get(BASE_URL + "course");

        step(2, "Wait notification");

        WebElement noti = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("notification"))
        );

        step(3, "Verify notification");

        Assert.assertTrue(noti.getText().contains("Không tìm thấy mã khóa học!"));

        step(4, "Wait redirect to index");

        wait.until(ExpectedConditions.urlContains("index"));

        step(5, "Verify redirect");

        Assert.assertTrue(driver.getCurrentUrl().contains("index"));
    }

    @Test
    public void TC_VIEW_14_ViewCourseWhenNotLoggedIn() {

        step(1, "Open course detail page");

        driver.get(BASE_URL + "course?id=12");

        step(2, "Wait course page load");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("course-title")));

        step(3, "Verify buy button visible");

        WebElement buyBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("btn-buy-course"))
        );

        Assert.assertTrue(buyBtn.isDisplayed(),
                "Không hiển thị nút Mua khóa học khi chưa đăng nhập");
    }


    @Test
    public void TC_VIEW_15_ViewWhenLoggedIn() {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        step(2, "Enter login information");

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name"))
        );
        nameInput.clear();
        nameInput.sendKeys("Use");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("123456");

        step(3, "Click login button");
        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait login success alert");

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();

        Assert.assertEquals(alertText, "Đăng nhập thành công!");
        alert.accept();

        step(5, "Verify token stored in localStorage");

        String token = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('userToken');");

        Assert.assertNotNull(token, "Token không được lưu sau khi login");

        step(6, "Open index page");

        driver.get(BASE_URL + "index");

        step(7, "Verify user menu visible");

        WebElement userMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("user-menu"))
        );

        Assert.assertTrue(userMenu.isDisplayed(),
                "Không hiển thị user menu khi đã đăng nhập");
    }
}
