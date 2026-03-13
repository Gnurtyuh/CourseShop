package com.javaweb.web.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Random;
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
                {"User", "newemail@test.com", "123456@Abc", "Tên đã tồn tại"},
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
                "Đăng ký thành công! Chuyển đến trang đăng nhập...");

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
                expectedError);

        Assert.assertFalse(isRedirectedToLoginPage(),
                "Should not redirect to login page");
    }

    // TC-REG-004,005,006: Bỏ trống trường
    @Test(dataProvider = "emptyFieldData", priority = 3)
    public void testEmptyFields(String name, String email, String password) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        WebElement nameField = driver.findElement(By.id("name"));
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));

        String validationMessage = "";

        if (name == null || name.isBlank()) {
            validationMessage = nameField.getAttribute("validationMessage");
        }
        else if (email == null || email.isBlank()) {
            validationMessage = emailField.getAttribute("validationMessage");
        }
        else if (password == null || password.isBlank()) {
            validationMessage = passwordField.getAttribute("validationMessage");
        }

        Assert.assertNotNull(validationMessage, "Validation message should appear");
        Assert.assertFalse(validationMessage.isEmpty(),
                "Browser should show required field validation");
    }

    // TC-REG-007: Email không hợp lệ
    @Test(dataProvider = "invalidEmailData", priority = 4)
    public void testInvalidEmail(String name, String email, String password) {

        driver.get(REGISTER_PAGE);

        enterRegistrationData(name, email, password);
        submitForm();

        WebElement emailField = driver.findElement(By.id("email"));

        String validationMessage = emailField.getAttribute("validationMessage");

        Assert.assertNotNull(validationMessage);
        Assert.assertFalse(validationMessage.isEmpty(),
                "Browser should show email validation message");
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
    // TC-REG-013: Name chứa ký tự đặc biệt
    @Test(priority = 8)
    public void testNameWithSpecialCharacters() {

        driver.get(REGISTER_PAGE);

        String randomEmail = "user" + UUID.randomUUID() + "@email.com";

        // Nhập name có ký tự lạ
        enterRegistrationData("test@#$%", randomEmail, "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        // Kiểm tra có thông báo lỗi
        Assert.assertNotNull(alertMessage, "Hệ thống phải trả về thông báo lỗi");

        // Kiểm tra nội dung lỗi
        Assert.assertTrue(
                alertMessage.contains("tên") ||
                        alertMessage.contains("name") ||
                        alertMessage.contains("hợp lệ") ||
                        alertMessage.contains("invalid"),
                "Tên không hợp lệ"
        );
    }
    @DataProvider(name = "edgeCaseData")
    public Object[][] edgeCaseData() {
        return new Object[][]{
                {"  Huy  "+ UUID.randomUUID(),"user" + UUID.randomUUID() + "@email.com", " 123456 ","Tên không được chứa khoảng trắng"},
                {"Huy"+ UUID.randomUUID(), "      user  " + UUID.randomUUID() + "  @email.com      ", "123456","Email không được chứa khoảng trắng"},
                {"Huy"+ UUID.randomUUID(),"user" + UUID.randomUUID() + "@email.com", "   1234   56   ", "Password không được chứa khoảng trắng"}
        };
    }
    // TC-REG-011 & 012: Trim khoảng trắng
    @Test(dataProvider = "edgeCaseData", priority = 9)
    public void testTrimSpaces(String name, String email, String password, String expectedError) {

        driver.get(REGISTER_PAGE);
        enterRegistrationData(name, email, password);
        submitForm();
        // Có thể thành công hoặc thất bại tùy business rule
        String alertMessage = getAlertMessage();
        if (alertMessage != null && !alertMessage.contains("thành công")) {
            Assert.assertTrue(alertMessage.contains(expectedError),
                    expectedError);
        }
    }

    @Test(priority = 10)
    public void testLongName() {

        driver.get(REGISTER_PAGE);

        String randomEmail = "user" + UUID.randomUUID() + "@email.com";

        // Nhập name có ký tự lạ
        enterRegistrationData("Longáddsdyasgdsiaudasuidhasuidhasidhs" +
                "uihdasiudhasuidhashdasihdasiudhasiudhasiudhasiudhasiu" +
                "dhasiudhasiudhasuidhasiudhasuidhasuidhasuidhasuidhasui" +
                "dhasuidhasuidhasuidhasuidhasuidhasuidhasuidhsadhasuidh" +
                "asiudhsicbszkbcszcbqyuibcbcabcqweydcgqcbnqgxcquidhqawixcasihqa8idf", randomEmail, "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        // Kiểm tra có thông báo lỗi
        Assert.assertNotNull(alertMessage, "Hệ thống phải trả về thông báo lỗi");

        // Kiểm tra nội dung lỗi
        Assert.assertTrue(
                alertMessage.contains("Tên quá dài") ,
                "Tên quá dài"
        );
    }
    @Test(priority = 10)
    public void testNameNumber() {

        driver.get(REGISTER_PAGE);

        String randomEmail = "user" + UUID.randomUUID() + "@email.com";

        // Nhập name có ký tự lạ
        enterRegistrationData("123456", randomEmail, "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        // Kiểm tra có thông báo lỗi
        Assert.assertNotNull(alertMessage, "Hệ thống phải trả về thông báo lỗi");

        // Kiểm tra nội dung lỗi
        Assert.assertTrue(
                alertMessage.contains("Tên không hợp lệ") ,
                "Tên không hợp lệ"
        );
    }
}