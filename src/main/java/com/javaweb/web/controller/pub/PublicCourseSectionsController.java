package com.javaweb.web.controller.pub;

import com.javaweb.web.entity.CourseLessons;
import com.javaweb.web.entity.CourseSections;
import com.javaweb.web.service.CourseLessonsService;
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
@RequestMapping("/api/public/courseSection")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicCourseSectionsController {
    @Autowired
    CourseSectionsService sectionService;
    @Autowired
    CourseLessonsService lessonService;

//    @GetMapping("/by-course/{courseId}")
//    public ResponseEntity<List<CourseSections>> getByCourse(@PathVariable int courseId) {
//        List<CourseSections> sections = sectionService.getCourseSectionsByCourseId(courseId);
//        return ResponseEntity.ok(sections);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            CourseSections section = sectionService.getCourseSectionsBySectionId(id);
            return ResponseEntity.ok(section);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương học.");
        }
    }
    @GetMapping("/by-course/{courseId}")
    public List<Map<String, Object>> getSectionsWithLessons(@PathVariable int courseId) {
        List<CourseSections> sections = sectionService.getCourseSectionsByCourseId(courseId);


        List<Map<String, Object>> response = new ArrayList<>();

        for (CourseSections section : sections) {
            Map<String, Object> sectionMap = new HashMap<>();
            sectionMap.put("sectionTitle", section.getTitle());

            List<Map<String, Object>> lessonsForSection = new ArrayList<>();
            List<CourseLessons> allLessons = lessonService.getCoursesLessonBySectionId(section.getId());
            for (CourseLessons lesson : allLessons) {
                    Map<String, Object> lessonMap = new HashMap<>();
                    lessonMap.put("title", lesson.getTitle());
                    lessonsForSection.add(lessonMap);
            }
            sectionMap.put("lessons", lessonsForSection);
            response.add(sectionMap);
        }
        return response;
    }
}