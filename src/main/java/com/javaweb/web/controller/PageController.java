package com.javaweb.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    @GetMapping("/course")
    public String course() {
        return "course";
    }
    @GetMapping("/course-video")
    public String courseVideo() {
        return "course-video";
    }
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    @GetMapping("/loginadmin")
    public String loginadmin() {
        return "loginadmin";
    }
    @GetMapping("/my-courses")
    public String myCourses() {
        return "my-courses";
    }
    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    @GetMapping("/topup")
    public String topup() {
        return "topup";
    }
    @GetMapping("/topup-guide")
    public String topupGuide() {
        return "topup-guide";
    }
}