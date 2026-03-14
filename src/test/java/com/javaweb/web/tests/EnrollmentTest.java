package com.javaweb.web.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EnrollmentTest extends BaseTest {
    @Test
    public void testBuyCourseSuccess() {

        driver.get(COURSE_PAGE + "13");

        WebElement buyBtn = driver.findElement(By.id("buyCourseBtn"));
        buyBtn.click();

        WebElement message = driver.findElement(By.className("toast-message"));

        Assert.assertTrue(message.getText().contains("Mua khóa học thành công"));
    }
}
