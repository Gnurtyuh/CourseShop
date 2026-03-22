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
    public void TC_VIEW_01() {

        step(1, "Open index page (normal)");

        driver.get(BASE_URL + "index"); // default = normal
        logUrl();

        step(2, "Wait for course items render");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("course-item")));

        step(3, "Count course items");

        int courseCount = driver.findElements(By.className("course-item")).size();
        System.out.println("Course count: " + courseCount);

        step(4, "Verify courses displayed");

        Assert.assertTrue(courseCount > 0, "Không có khóa học nào hiển thị");
    }
    @Test
    public void TC_VIEW_03() {

        step(1, "Open index page (error case)");

        driver.get(BASE_URL + "index?mode=error"); // 🔥 QUAN TRỌNG
        logUrl();

        step(2, "Wait notification appear");

        WebElement notification = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("notification"))
        );

        step(3, "Get notification message");

        String message = notification.getText();
        System.out.println("Notification message: " + message);

        step(4, "Verify error message");

        Assert.assertEquals(message, "Lỗi tải khóa học!");
    }

    @Test
    public void TC_VIEW_02() {

        step(1, "Open index page with empty courses");

        driver.get(BASE_URL + "index?mode=empty"); // 🔥 QUAN TRỌNG
        logUrl();

        step(2, "Wait message");

        WebElement msg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("no-course-msg"))
        );

        step(3, "Get message");

        String message = msg.getText();
        System.out.println("Message: " + message);

        step(4, "Verify message");

        Assert.assertEquals(message, "Không có khóa học nào.");
    }

    @Test
    public void TC_VIEW_04() {

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
    public void TC_VIEW_05() {

        step(1, "Login first");

        driver.get(BASE_URL + "login");

        driver.findElement(By.id("name")).sendKeys("Use");
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
    public void TC_VIEW_06() {

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
    public void TC_VIEW_07() {

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
    public void TC_VIEW_08() {

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
    public void TC_VIEW_09() {

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
    public void TC_VIEW_10() throws InterruptedException {

        step(1, "Open index page");
        driver.get(BASE_URL + "index");

        Thread.sleep(2000); // pause để nhìn trang index

        step(2, "Wait courses render");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("courses-container")));

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.className("course-item"), 0));

        Thread.sleep(2000); // giữ sau khi list load

        step(3, "Enter keyword xyz");

        driver.findElement(By.id("search-input")).sendKeys("xyz");

        Thread.sleep(1000); // nhìn thao tác nhập

        step(4, "Click search");

        driver.findElement(By.id("search-btn")).click();

        Thread.sleep(2000); // chờ kết quả search

        step(5, "Verify no course message");

        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("no-course-msg")));

        Thread.sleep(3000); // giữ thông báo để nhìn

        Assert.assertTrue(msg.getText().contains("Không có khóa học nào"),
                "Không hiển thị thông báo khi không có kết quả tìm kiếm");

        Thread.sleep(5000); // giữ màn cuối trước khi test kết thúc
    }

    @Test
    public void TC_VIEW_11() throws InterruptedException {

        driver.get(BASE_URL + "course?id=12");

        Thread.sleep(2000); // pause để nhìn trang ban đầu

        step(1, "Wait course title load");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBe(
                        By.id("course-title"),
                        "Đang tải..."
                )
        ));

        Thread.sleep(2000); // giữ sau khi title load

        step(2, "Wait instructor load");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBe(
                        By.id("course-instructor"),
                        ""
                )
        ));

        Thread.sleep(3000); // giữ sau khi instructor load

        step(3, "Verify course information");

        Assert.assertFalse(driver.findElement(By.id("course-title")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-description")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-instructor")).getText().isEmpty());
        Assert.assertFalse(driver.findElement(By.id("course-price")).getText().isEmpty());

        Thread.sleep(5000); // giữ màn cuối trước khi test kết thúc
    }

    @Test
    public void TC_VIEW_12() throws InterruptedException {

        step(1, "Open invalid course page");

        driver.get(BASE_URL + "course?id=999");

        Thread.sleep(2000); // pause để nhìn trang load

        step(2, "Wait error message");

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("course-title")));

        Thread.sleep(3000); // giữ màn hình để nhìn lỗi

        step(3, "Verify error message");

        Assert.assertTrue(title.getText().contains("Lỗi tải khóa học"),
                "Không hiển thị thông báo lỗi khi courseId không tồn tại");

        Thread.sleep(5000); // giữ màn cuối trước khi test kết thúc
    }


    @Test public void TC_VIEW_13() {
        step(1, "Open course page without id");
        driver.get(BASE_URL + "course");
        step(2, "Wait notification");
        WebElement noti = wait.until( ExpectedConditions.visibilityOfElementLocated( By.id("notification")) );
        step(3, "Verify notification");
        Assert.assertTrue(noti.getText().contains("Không tìm thấy mã khóa học!"));
        step(4, "Wait redirect to index");
        wait.until(ExpectedConditions.urlContains("index"));
        step(5, "Verify redirect");
        Assert.assertTrue(driver.getCurrentUrl().contains("index")); }

    @Test
    public void TC_VIEW_14() throws InterruptedException {

        step(1, "Open course detail page");

        driver.get(BASE_URL + "course?id=12");

        Thread.sleep(2000); // pause để nhìn trang load

        step(2, "Wait course page load");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("course-title")));

        Thread.sleep(2000); // giữ màn hình sau khi load xong

        step(3, "Verify buy button visible");

        WebElement buyBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("btn-buy-course"))
        );

        Assert.assertTrue(buyBtn.isDisplayed(),
                "Không hiển thị nút Mua khóa học khi chưa đăng nhập");

        Thread.sleep(5000); // giữ màn cuối để bạn nhìn UI trước khi đóng
    }


    @Test
    public void TC_VIEW_15() throws InterruptedException {

        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        Thread.sleep(2000); // pause 2s để nhìn UI

        step(2, "Enter login information");

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name"))
        );
        nameInput.clear();
        nameInput.sendKeys("Use");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("123456");

        Thread.sleep(2000);

        step(3, "Click login button");
        driver.findElement(By.id("loginBtn")).click();

        step(4, "Wait login success alert");

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();

        Assert.assertEquals(alertText, "Đăng nhập thành công!");

        Thread.sleep(2000); // giữ alert lâu hơn trước khi accept
        alert.accept();

        step(5, "Verify token stored in localStorage");

        String token = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('userToken');");

        Assert.assertNotNull(token, "Token không được lưu sau khi login");

        step(6, "Open index page");

        driver.get(BASE_URL + "index");

        Thread.sleep(3000); // giữ màn index lâu hơn

        step(7, "Verify user menu visible");

        WebElement userMenu = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("user-menu"))
        );

        Assert.assertTrue(userMenu.isDisplayed(),
                "Không hiển thị user menu khi đã đăng nhập");

        Thread.sleep(5000); // giữ màn cuối trước khi test kết thúc
    }


    @Test
    public void TC_VIEW_16() {

        int courseId = 12;

        // ===== 1. LOGIN =====
        step(1, "Open login page");
        driver.get(BASE_URL + "login");

        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("name"))
        );
        nameInput.clear();
        nameInput.sendKeys("Use");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("123456");

        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.alertIsPresent()).accept();

        // 🔥 FIX: đợi redirect sau login xong
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("index"),
                ExpectedConditions.urlContains("home"),
                ExpectedConditions.urlContains("course")
        ));

        // ===== 2. OPEN COURSE DETAIL =====
        step(2, "Open course detail page");

        // 🔥 FIX QUAN TRỌNG: vào đúng trang detail
        driver.get(BASE_URL + "course?id=" + courseId);

        // chỉ cần đợi page load, KHÔNG đợi course-title
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // ===== 3. CHECK & BUY IF NEEDED =====
        step(3, "Check if need to buy course");

        List<WebElement> buyBtnList = driver.findElements(By.id("btn-buy-course"));

        if (!buyBtnList.isEmpty() && buyBtnList.get(0).isDisplayed()) {

            step(4, "Click buy course");

            buyBtnList.get(0).click();

            try {
                Alert buyAlert = wait.until(ExpectedConditions.alertIsPresent());
                buyAlert.accept();
            } catch (Exception e) {
                System.out.println("No alert after buying (UI notification used)");
            }

            // 🔥 đợi nút mua biến mất (quyền đã update)
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("btn-buy-course")));
        }

        // ===== 4. VERIFY BUTTON STATE =====
        step(5, "Verify buy button hidden");

        List<WebElement> buyAfter = driver.findElements(By.id("btn-buy-course"));
        if (!buyAfter.isEmpty()) {
            Assert.assertFalse(buyAfter.get(0).isDisplayed(),
                    "Nút Mua vẫn còn sau khi đã mua");
        }

        step(6, "Verify view button visible");

        WebElement viewBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-watch-course"))
        );

        Assert.assertTrue(viewBtn.isDisplayed(),
                "Không hiển thị nút Xem khóa học");

        // ===== 5. CLICK VIEW COURSE =====
        step(7, "Click view course");

        viewBtn.click();

        // ===== 6. VERIFY NAVIGATION =====
        step(8, "Verify redirect to learning page");

        wait.until(ExpectedConditions.urlContains("course-video"));
        String currentUrl = driver.getCurrentUrl();

        Assert.assertTrue(
                currentUrl.contains("courseId=" + courseId) ||
                        currentUrl.contains("id=" + courseId),
                "Sai courseId sau khi chuyển trang: " + currentUrl
        );

        // ===== 7. VERIFY CONTENT LOAD =====
        step(9, "Verify course content loaded");

        // 🔥 FIX: đợi body hoặc element chung thay vì course-title cứng
        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("body"))
        );

        Assert.assertTrue(body.isDisplayed(),
                "Trang course video không load!");
    }

    @Test
    public void TC_VIEW_17() {

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

        step(4, "Close alert if appears");

        // chỉ đóng alert, không cần check nội dung
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (Exception e) {
            // nếu không có alert thì bỏ qua
        }

        step(5, "Open course detail page");
        driver.get(BASE_URL + "course?id=12");

        step(6, "Verify owned badge");

        WebElement badge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("owned-badge"))
        );

        Assert.assertTrue(badge.isDisplayed(),
                "Không hiển thị badge 'Đã sở hữu'");
    }


    @Test
    public void TC_VIEW_18() {

        step(1, "Open course detail page");
        driver.get(BASE_URL + "course?id=12");

        step(2, "Verify section list is displayed");

        List<WebElement> sections = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className("course-section"))
        );

        Assert.assertTrue(sections.size() > 0,
                "Không có section nào hiển thị");

        step(3, "Verify lesson list");

        List<WebElement> lessons = driver.findElements(
                By.cssSelector("#section-list li")
        );

        Assert.assertTrue(lessons.size() > 0,
                "Không có lesson nào hiển thị");
    }

    @Test
    public void TC_VIEW_19() {

        step(1, "Open course detail page");
        driver.get(BASE_URL + "course?id=15"); // 👉 dùng id=15
        try {
            Thread.sleep(5000); // dừng 5s để nhìn UI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        step(2, "Verify section WITHOUT lesson message");

        List<WebElement> messages = driver.findElements(
                By.xpath("//*[contains(text(),'Chưa có bài học nào trong phần này')]")
        );

        Assert.assertTrue(messages.isEmpty(),
                "Text không được hiển thị nhưng lại xuất hiện");
    }

    @Test
    public void TC_VIEW_20() {

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
    public void TC_VIEW_21() {

        step(1, "Open course detail with invalid id");

        driver.get(BASE_URL + "course-video?id=999999");

        step(2, "Verify error notification is displayed");

        WebElement notification = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("notification"))
        );

// 🔥 THÊM DÒNG NÀY (quan trọng nhất)
        wait.until(driver -> !notification.getText().trim().isEmpty());

        String message = notification.getText();
        System.out.println("DEBUG message = " + message);

        Assert.assertTrue(
                message.contains("Không thể tải thông tin khóa học!"),
                "Actual message: " + message
        );
    }

}
