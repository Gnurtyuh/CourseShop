package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

public class RegisterTest extends BaseTest {

    @DataProvider(name = "successfulRegistrationData")
    public Object[][] getSuccessfulRegistrationData() {
        return new Object[][]{
                {"user" + UUID.randomUUID(), "user" + UUID.randomUUID() + "@email.com", "123456@Abc"}
        };
    }

    @DataProvider(name = "existingData")
    public Object[][] getExistingData() {
        return new Object[][]{
                {"admin", "newemail@test.com", "123456@Abc", "Tên đã tồn tại"},
                {"newuser", "nkocsoc2004@gmail.com", "123456@Abc", "Email đã tồn tại"}
        };
    }

    @DataProvider(name = "emptyFieldData")
    public Object[][] getEmptyFieldData() {
        return new Object[][]{
                {"", "test@email.com", "123456@Abc"},
                {"testuser", "", "123456@Abc"},
                {"testuser", "test@email.com", ""}
        };
    }

    @DataProvider(name = "invalidEmailData")
    public Object[][] getInvalidEmailData() {
        return new Object[][]{
                {"testuser", "invalid-email", "123456@Abc"}
        };
    }

    // TC-REG-001: Đăng ký thành công
    @Test(dataProvider = "successfulRegistrationData", priority = 1)
    public void testSuccessfulRegistration(String name, String email, String password) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");
        Assert.assertTrue(alertMessage.contains("Đăng ký thành công"),
                "Alert should contain success message");

        Assert.assertTrue(isRedirectedToLoginPage(),
                "Should redirect to login page");
    }

    // TC-REG-002 & 003: Dữ liệu đã tồn tại
    @Test(dataProvider = "existingData", priority = 2)
    public void testExistingData(String name, String email, String password, String expectedError) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");

        Assert.assertTrue(alertMessage.contains(expectedError),
                "Alert should contain: " + expectedError);

        Assert.assertFalse(isRedirectedToLoginPage(),
                "Should not redirect to login page");
    }

    // TC-REG-004,005,006: Bỏ trống trường
    @Test(dataProvider = "emptyFieldData", priority = 3)
    public void testEmptyFields(String name, String email, String password) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");

        Assert.assertTrue(alertMessage.contains("Vui lòng") ||
                        alertMessage.contains("required"),
                "Should show empty fields message");
    }

    // TC-REG-007: Email không hợp lệ
    @Test(dataProvider = "invalidEmailData", priority = 4)
    public void testInvalidEmail(String name, String email, String password) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");

        Assert.assertTrue(alertMessage.toLowerCase().contains("email"),
                "Should show email format error");
    }

    // TC-REG-010: Password quá ngắn
    @Test(priority = 5)
    public void testShortPassword() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("testuser", "test@email.com", "123");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");

        Assert.assertTrue(alertMessage.toLowerCase().contains("password"),
                "Password too short error expected");
    }

    // TC-REG-017 & 018: SQL Injection
    @Test(priority = 6)
    public void testSQLInjection() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("' OR '1'='1", "test@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertFalse(alertMessage != null &&
                        (alertMessage.contains("SQL") ||
                                alertMessage.contains("syntax") ||
                                alertMessage.contains("database")),
                "Should not expose SQL errors");
    }

    // TC-REG-019: XSS
    @Test(priority = 7)
    public void testXSSAttack() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("<script>alert(1)</script>", "test@email.com", "123456@Abc");
        submitForm();

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            Assert.assertFalse(alert.getText().contains("1"),
                    "XSS script should not execute");

        } catch (Exception e) {
            // Không có alert từ XSS là đúng
        }
    }

    // TC-REG-025: Concurrent registration
    @Test(priority = 8, invocationCount = 2, threadPoolSize = 2)
    public void testConcurrentRegistration() {

        driver.get(REGISTER_PAGE);

        String unique = "concurrent" + System.currentTimeMillis() + Thread.currentThread().getId();

        enterRegistrationData(unique, unique + "@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage);

        if (alertMessage.contains("thành công")) {
            Assert.assertTrue(isRedirectedToLoginPage());
        } else {
            Assert.assertTrue(alertMessage.contains("tồn tại") ||
                    alertMessage.contains("exists"));
        }
    }
}