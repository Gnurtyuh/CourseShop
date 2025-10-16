package com.javaweb.web.controller.user;

import com.javaweb.web.entity.Courses;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/course")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CoursesController {
    @Autowired
    CoursesService courseService;
    @Autowired
    EnrollmentsService enrollmentsService;
    @Autowired
    UsersService usersService;
    @Autowired
    AccessService accessService;

    @GetMapping
    public ResponseEntity<List<Courses>> getAll() {
        List<Courses> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/myCourse")
    public ResponseEntity<List<Courses>> getMyCourse(@AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersService.getUserByName(userDetails.getUsername());
        List<Courses> courses = enrollmentsService.getCoursesByUserId(user.getId());
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            Courses course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khóa học.");
        }
    }
    @GetMapping("/me/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = usersService.getUserByName(userDetails.getUsername());
            accessService.checkAccessToCourse(user.getId(), id);
            Courses course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bài học.");
        }
    }
}
