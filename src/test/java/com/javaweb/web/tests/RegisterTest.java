package com.javaweb.web.tests;

import net.bytebuddy.implementation.bind.annotation.Super;
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

    @Test( priority = 1)
    public void  TC_REG_01() {
        String name = STR."user\{System.currentTimeMillis()}";
        String email =STR."user\{System.currentTimeMillis()}@email.com";
        String password ="123456@Abc";
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
    @Test( priority = 2)
    public void TC_REG_02() {
        String name="newuser";
        String email="nkocsoc2004@gmail.com";
        String password="123456@Abc";
        String expectedError="Email đã tồn tại";
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
    @Test( priority =3)
    public void TC_REG_03() {
        String name="User" ;
        String email="newemail@test.com" ;
        String password="123456@Abc" ;
        String expectedError="Tên đã tồn tại";
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
    @Test( priority = 4)
    public void TC_REG_04() {
        String name="" ;
        String email="test@email.com" ;
        String password="123456@Abc" ;
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
    @Test( priority = 5)
    public void TC_REG_05() {
        String name="testuser";
        String email="" ;
        String password="123456@Abc" ;

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
    @Test( priority = 6)
    public void TC_REG_06() {

        String name="testuser";
        String email="test@email.com" ;
        String password="" ;

        driver.get(REGISTER_PAGE);

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

    @Test( priority = 7)
    public void TC_REG_07() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("testuser", "invalid-email", "123456@Abc");
        submitForm();

        WebElement emailField = driver.findElement(By.id("email"));

        String validationMessage = emailField.getAttribute("validationMessage");

        Assert.assertNotNull(validationMessage);
        Assert.assertFalse(validationMessage.isEmpty(),
                "Browser should show email validation message");
    }

    // TC-REG-010: Password quá ngắn
    @Test(priority = 8)
    public void TC_REG_08() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("testuser", "test@email.com", "123");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");

        Assert.assertTrue(alertMessage.toLowerCase().contains("password"),
                "Password too short error expected");
    }
    @Test(priority = 9)
    public void TC_REG_09() {

        driver.get(REGISTER_PAGE);
        String randomEmail = "user" + UUID.randomUUID() + "@email.com";
        enterRegistrationData("test@#$%", randomEmail, "123456@Abc");
        submitForm();
        String alertMessage = getAlertMessage();
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


//
//    // TC-REG-019: XSS
//    @Test(priority = 7)
//    public void testXSSAttack() {
//
//        driver.get(REGISTER_PAGE);
//
//        enterRegistrationData("<script>alert(1)</script>", "test@email.com", "123456@Abc");
//        submitForm();
//
//        try {
//            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//
//            Assert.assertFalse(alert.getText().contains("1"),
//                    "XSS script should not execute");
//
//        } catch (Exception e) {
//            // Không có alert từ XSS là đúng
//        }
//    }

    @Test(priority = 10)
    public void TC_REG_10() {

        driver.get(REGISTER_PAGE);

        String randomEmail = "userasfg@email.com";

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
    // TC-REG-011 & 012: Trim khoảng trắng
    @Test( priority = 11)
    public void TC_REG_11() {
        String expectedError="Email không được chứa khoảng trắng";
        driver.get(REGISTER_PAGE);
        enterRegistrationData("Huy",
                "      user   @email.com      ",
                "123456");
        submitForm();
        // Có thể thành công hoặc thất bại tùy business rule
        String alertMessage = getAlertMessage();
        if (alertMessage != null && !alertMessage.contains("thành công")) {
            Assert.assertTrue(alertMessage.contains(expectedError),
                    expectedError);
        }
    }
    @Test( priority = 12)
    public void TC_REG_12() {

        String expectedError ="Tên không được chứa khoảng trắng";
        driver.get(REGISTER_PAGE);
        enterRegistrationData("  Huy  jkhk",
                "user@email.com",
                " 123456 ");
        submitForm();
        // Có thể thành công hoặc thất bại tùy business rule
        String alertMessage = getAlertMessage();
        if (alertMessage != null && !alertMessage.contains("thành công")) {
            Assert.assertTrue(alertMessage.contains(expectedError),
                    expectedError);
        }
    }
    @Test( priority = 13)
    public void TC_REG_13() {

        String expectedError ="Password không được chứa khoảng trắng";
        driver.get(REGISTER_PAGE);
        enterRegistrationData("Huy",
                "user@email.com",
                "   1234   56   ");
        submitForm();
        // Có thể thành công hoặc thất bại tùy business rule
        String alertMessage = getAlertMessage();
        if (alertMessage != null && !alertMessage.contains("thành công")) {
            Assert.assertTrue(alertMessage.contains(expectedError),
                    expectedError);
        }
    }
    @Test(priority = 14)
    public void TC_REG_14() {

        driver.get(REGISTER_PAGE);

        enterRegistrationData("' OR '1'='1", "testfsđfv@email.com", "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertFalse(alertMessage != null &&
                        (alertMessage.contains("SQL") ||
                                alertMessage.contains("syntax") ||
                                alertMessage.contains("database")),
                "Should not expose SQL errors");
    }

    @Test(priority = 15)
    public void TC_REG_15() {

        driver.get(REGISTER_PAGE);
        String randomEmail = "userghfgsdfsdf@email.com";
        enterRegistrationData("123456", randomEmail, "123456@Abc");
        submitForm();
        String alertMessage = getAlertMessage();
        Assert.assertNotNull(alertMessage, "Hệ thống phải trả về thông báo lỗi");

        // Kiểm tra nội dung lỗi
        Assert.assertTrue(
                alertMessage.contains("Tên không hợp lệ") ,
                "Tên không hợp lệ"
        );
    }
    @Test(priority = 16)
    public void TC_REG_16() {

        driver.get(REGISTER_PAGE);

        String email = "test.user.email@gmail.com";
        enterRegistrationData("TestUser", email, "123456@Abc");
        submitForm();
        String alertMessage = getAlertMessage();
        Assert.assertNotNull(alertMessage, "Alert should appear");
        Assert.assertTrue(alertMessage.contains("Đăng ký thành công"),
                "Đăng ký thành công! Chuyển đến trang đăng nhập...");

        Assert.assertTrue(isRedirectedToLoginPage(),
                "Should redirect to login page");
    }
    @Test(priority = 17)
    public void TC_REG_17() {

        driver.get(REGISTER_PAGE);

        String randomEmail = "usersdffsdf@email.com";

        enterRegistrationData("NguyễnVănA", randomEmail, "123456@Abc");
        submitForm();

        String alertMessage = getAlertMessage();

        Assert.assertNotNull(alertMessage, "Alert should appear");
        Assert.assertTrue(alertMessage.contains("Đăng ký thành công"),
                "Đăng ký thành công! Chuyển đến trang đăng nhập...");

        Assert.assertTrue(isRedirectedToLoginPage(),
                "Should redirect to login page");
    }

}