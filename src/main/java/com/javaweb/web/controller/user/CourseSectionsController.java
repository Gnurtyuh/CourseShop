package com.javaweb.web.controller.user;

import com.javaweb.web.entity.CourseSections;
import com.javaweb.web.service.CourseSectionsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/courseSection")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseSectionsController {
    @Autowired
    CourseSectionsService sectionService;
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<CourseSections>> getByCourse(@PathVariable int courseId) {


        List<CourseSections> sections = sectionService.getCourseSectionsByCourseId(courseId);

        if (sections == null) {
            sections = new ArrayList<>();
        }

        return ResponseEntity.ok(sections);
    }

    @GetMapping("/by-course/999")
    public List<CourseSections> emptySections() {
        return new ArrayList<>();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            CourseSections section = sectionService.getCourseSectionsBySectionId(id);
            return ResponseEntity.ok(section);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương học.");
        }
    }
}