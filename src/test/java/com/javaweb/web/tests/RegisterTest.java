package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

public class RegisterTest extends BaseTest {

    @DataProvider(name = "successfulRegistrationData")
    public Object[][] getSuccessfulRegistrationData() {
        return new Object[][]{
                {"nguyenvana", "nguyenvana@email.com", "123456@Abc"}
        };
    }

    @DataProvider(name = "existingData")
    public Object[][] getExistingData() {
        return new Object[][]{
                {"admin", "newemail@test.com", "123456@Abc", "Tên đã tồn tại"},
                {"newuser", "existing@email.com", "123456@Abc", "Email đã tồn tại"}
        };
    }

    @DataProvider(name = "emptyFieldData")
    public Object[][] getEmptyFieldData() {
        return new Object[][]{
                {"", "test@email.com", "123456@Abc", "name"},
                {"testuser", "", "123456@Abc", "email"},
                {"testuser", "test@email.com", "", "password"}
        };
    }

    @DataProvider(name = "invalidEmailData")
    public Object[][] getInvalidEmailData() {
        return new Object[][]{
                {"testuser", "invalid-email", "123456@Abc"}
        };
    }

    @DataProvider(name = "sqlInjectionData")
    public Object[][] getSQLInjectionData() {
        return new Object[][]{
                {"' OR '1'='1", "test@email.com", "123456@Abc"},
                {"testuser", "' OR '1'='1'@email.com", "123456@Abc"}
        };
    }

    @DataProvider(name = "xssData")
    public Object[][] getXSSData() {
        return new Object[][]{
                {"<script>alert(1)</script>", "test@email.com", "123456@Abc"}
        };
    }

    @DataProvider(name = "edgeCaseData")
    public Object[][] getEdgeCaseData() {
        return new Object[][]{
                {" testuser ", "test@email.com", "123456@Abc"},
                {"testuser", " test@email.com ", "123456@Abc"}, // Email có khoảng trắng
                {"Nguyen Van A", "test@email.com", "123456@Abc"},
                {"123456789", "test@email.com", "123456@Abc"},
                {"NguyễnVănA", "test@email.com", "123456@Abc"},
                {"testuser", "test.user.name@email.com", "123456@Abc"}, // Email nhiều dấu chấm
                {"testuser", "test+label@email.com", "123456@Abc"}, // Email có dấu +
                {"testuser", "test@email.com", "!@#$%^&*()"} // Password ký tự đặc biệt
        };
    }

    // TC-REG-001: Đăng ký thành công
    @Test(dataProvider = "successfulRegistrationData", priority = 1)
    public void testSuccessfulRegistration(String name, String email, String password) {
        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains("Đăng ký thành công"),
                "Alert message should contain success message");
        Assert.assertTrue(isRedirectedToLoginPage(),
                "Should redirect to login page");
    }

    // TC-REG-002 & 003: Email
    @Test(dataProvider = "existingData", priority = 2)
    public void testExistingData(String name, String email, String password, String expectedError) {
        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains(expectedError),
                "Alert should contain: " + expectedError);
        Assert.assertFalse(isRedirectedToLoginPage(),
                "Should not redirect to login page");
    }

    // TC-REG-004, 005, 006: Bỏ trống trường
    @Test(dataProvider = "emptyFieldData", priority = 3)
    public void testEmptyFields(String name, String email, String password, String emptyField) {
        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertEquals(alertMessage, "Vui lòng điền đầy đủ thông tin.",
                "Should show empty fields message");
    }

    // TC-REG-007: Email không hợp lệ
    @Test(dataProvider = "invalidEmailData", priority = 4)
    public void testInvalidEmail(String name, String email, String password) {
        enterRegistrationData(name, email, password);
        submitForm();

        // Kiểm tra response từ backend
        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains("email") ||
                        alertMessage.contains("format"),
                "Should show email format error");
    }

    @Test(priority = 5)
    public void testSpecialCharUsername() {
        enterRegistrationData("test@user#", "test@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains("Tên không hợp lệ") ||
                        alertMessage.contains("name"),
                "Should show invalid name error");
    }

    @Test(priority = 6)
    public void testLongUsername() {
        String longUsername = "a".repeat(100);
        enterRegistrationData(longUsername, "test@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains("quá dài") ||
                        alertMessage.contains("too long"),
                "Should show name too long error");
    }

    // TC-REG-010: Password quá ngắn
    @Test(priority = 7)
    public void testShortPassword() {
        enterRegistrationData("testuser", "test@email.com", "123");
        submitForm();

        String alertMessage = getAlertMessage();
        Assert.assertTrue(alertMessage.contains("password") ||
                        alertMessage.contains("mật khẩu"),
                "Should show password too short error");
    }

    // TC-REG-011 & 012: Trim khoảng trắng
    @Test(dataProvider = "edgeCaseData", priority = 8)
    public void testTrimSpaces(String name, String email, String password) {
        enterRegistrationData(name, email, password);
        submitForm();

        // Có thể thành công hoặc thất bại tùy business rule
        String alertMessage = getAlertMessage();
        if (alertMessage != null && !alertMessage.contains("thành công")) {
            Assert.assertTrue(alertMessage.contains("hợp lệ") ||
                            alertMessage.contains("invalid"),
                    "Should handle spaces appropriately");
        }
    }

    // TC-REG-017 & 018: SQL Injection
    @Test(dataProvider = "sqlInjectionData", priority = 9)
    public void testSQLInjection(String name, String email, String password) {
        enterRegistrationData(name, email, password);
        submitForm();

        // Kiểm tra không bị SQL injection (không có database error)
        String alertMessage = getAlertMessage();
        Assert.assertFalse(alertMessage != null &&
                        (alertMessage.contains("SQL") ||
                                alertMessage.contains("syntax") ||
                                alertMessage.contains("database")),
                "Should not show SQL errors");
    }

    // TC-REG-019: XSS Attack
    @Test(dataProvider = "xssData", priority = 10)
    public void testXSSAttack(String name, String email, String password) {
        enterRegistrationData(name, email, password);
        submitForm();

        // Kiểm tra XSS không thực thi
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            Assert.assertFalse(alert.getText().contains("1"),
                    "XSS script should not execute");
        } catch (Exception e) {
            // Không có alert từ XSS là tốt
        }
    }

    // TC-REG-021: Gửi request thiếu trường (test qua JavaScript injection)
    @Test(priority = 11)
    public void testMissingField() {
        // Cast driver sang JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script =
                "fetch('http://localhost:8080/CourseShop/api/public/auth', {" +
                        "  method: 'POST'," +
                        "  headers: {'Content-Type': 'application/json'}," +
                        "  body: JSON.stringify({name: 'testuser', email: 'test@email.com'})" +
                        "})" +
                        ".then(response => response.text())" +
                        ".then(text => alert(text));";

        // Sử dụng js thay vì driver
        js.executeScript(script);

        // Xử lý alert
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertMessage = alert.getText();
            alert.accept();

            Assert.assertTrue(alertMessage != null, "Alert should appear");
            System.out.println("Alert message: " + alertMessage);

        } catch (Exception e) {
            Assert.fail("No alert appeared: " + e.getMessage());
        }
    }

    // TC-REG-025: Concurrent registration
    @Test(priority = 12, invocationCount = 2, threadPoolSize = 2)
    public void testConcurrentRegistration() {
        String uniqueUsername = "concurrent" + System.currentTimeMillis() + Thread.currentThread().getId();
        enterRegistrationData(uniqueUsername, uniqueUsername + "@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();
        // Chỉ một thread thành công, các thread còn lại thất bại
        if (alertMessage.contains("thành công")) {
            Assert.assertTrue(isRedirectedToLoginPage(), "Should redirect on success");
        } else {
            Assert.assertTrue(alertMessage.contains("tồn tại") ||
                            alertMessage.contains("exists"),
                    "Other threads should fail with duplicate error");
        }
    }
}