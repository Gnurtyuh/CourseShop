package com.javaweb.web.tests.api;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RegisterAPITest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/public/auth";
    }

    @Test(priority = 1)
    public void testRegisterSuccess() {
        String uniqueEmail = "user" + UUID.randomUUID() + "@email.com";
        String uniqueName   = "user" + UUID.randomUUID() ;
        Map<String, Object> user = new HashMap<>();
        user.put("name", uniqueName);
        user.put("email", uniqueEmail);
        user.put("password", "123456@Abc");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(200)
                .body(equalTo("Đăng ký thành công"));
    }

    @Test(priority = 2)
    public void testRegisterDuplicateUsername() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Use");
        user.put("email", "new" + UUID.randomUUID() + "@email.com");
        user.put("password", "123456@Abc");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(anyOf(is(400), is(401)))
                .body(containsString("Tên đã tồn tại"));
    }

    @Test(priority = 3)
    public void testRegisterDuplicateEmail() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "new" + UUID.randomUUID());
        user.put("email", "nkocsoc200@gmail.com"); // Email đã tồn tại
        user.put("password", "123456@Abc");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(401)))
                .body(containsString("Email đã tồn tại"));
    }

    @Test(priority = 4)
    public void testRegisterInvalidEmail() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "InvalidEmail"+ UUID.randomUUID());
        user.put("email", "invalid-email");
        user.put("password", "123456@Abc");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(401)))
                .body(containsString("email"));
    }

    @Test(priority = 5)
    public void testRegisterMissingField() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "MissingField" + UUID.randomUUID());
        user.put("email", "test"+UUID.randomUUID()+"@email.com");
        // Thiếu password

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(400), is(401)));
    }

    @Test(priority = 6)
    public void testSQLInjection() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "' OR '1'='1");
        user.put("email", "test@email.com");
        user.put("password", "123456@Abc");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(anyOf(is(401), is(200))) // Không được 500
                .body(not(containsString("SQL")));
    }
}