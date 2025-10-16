package com.javaweb.web.service;

import com.javaweb.web.entity.Courses;
import com.javaweb.web.entity.Enrollments;
import com.javaweb.web.repository.CoursesRepo;
import com.javaweb.web.repository.EnrollmentsRepo;
import com.javaweb.web.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentsService {
    @Autowired
    private EnrollmentsRepo enrollmentsRepo;
    @Autowired private CoursesRepo courseRepo;
    @Autowired private UsersService userService;
    @Autowired private UsersRepo userRepo;

    public List<Enrollments> getAllEnrollments() {
        return enrollmentsRepo.findAll();
    }
    public Enrollments getEnrollmentById(int id) {
        return enrollmentsRepo.findById(id).orElseThrow(() -> new RuntimeException("enrollment not found"));
    }

    public Enrollments updateEnrollment(int id,Enrollments enrollment) {
        if (enrollmentsRepo.findById(id).orElseThrow(() -> new RuntimeException("enrollment not found")) != null) {
            return enrollmentsRepo.save(enrollment);
        }
        return null;
    }
    public List<Courses> getCoursesByUserId(int userId) {
        return getEnrollmentByUserId(userId).stream()
                .map(Enrollments::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }
    public List<Enrollments> getEnrollmentByUserId(int userId) {
        return enrollmentsRepo.findByUserId(userId);
    }
    public List<Enrollments> getEnrollmentByCourseId(int courseId) {
        return enrollmentsRepo.findByCourseId(courseId);
    }
    public String enroll(int userId, int courseId) {
        if (enrollmentsRepo.existsByUserIdAndCourseId(userId, courseId)) {
            return "Đã ghi danh rồi.";
        }

        Courses course = courseRepo.findById(courseId).orElseThrow();
        userService.decreaseBalance(userId, course.getPrice());

        Enrollments e = new Enrollments();
        e.setUser(userRepo.getReferenceById(userId));
        e.setCourse(course);
        enrollmentsRepo.save(e);
        return "Ghi danh thành công.";
    }
}
