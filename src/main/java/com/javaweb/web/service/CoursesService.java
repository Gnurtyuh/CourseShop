package com.javaweb.web.service;

import com.javaweb.web.entity.Courses;
import com.javaweb.web.repository.CoursesRepo;
import com.javaweb.web.util.DirectoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoursesService {
    @Autowired
    private CoursesRepo coursesRepo;
    public List<Courses> getAllCourses() {
        return coursesRepo.findAll();
    }
    public Courses getCourseById(int id) {
        return coursesRepo.findById(id).orElseThrow(()-> new RuntimeException("Course Not Found"));
    }
    public Courses addCourse(Courses course) {
        DirectoryUtil.createCourseVideoDirectory(course.getTitle());
        return coursesRepo.save(course);
    }
    public Courses updateCourse(int id,Courses course) {
        Optional<Courses> optional = coursesRepo.findById(id);
        Courses courses = optional.get();
        courses.setTitle(course.getTitle());
        courses.setPrice(course.getPrice());
        return coursesRepo.save(courses);


    }
    public void deleteCourse(int id) {
        coursesRepo.deleteById(id);
    }
    public Courses getCourseByTitle(String title) {
        return coursesRepo.findByTitle(title);
    }
}
