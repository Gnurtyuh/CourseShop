package com.javaweb.web.controller.admin;

import com.javaweb.web.entity.CourseLessons;
import com.javaweb.web.entity.CourseSections;
import com.javaweb.web.service.CourseLessonsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin/courseLesson")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdCourseLessonsController {
    @Autowired
    CourseLessonsService courseLessonsService;
    @PostMapping("/by-course/{sectionId}")
    ResponseEntity<?> addCourseLesson(@PathVariable int sectionId ,@RequestBody CourseLessons courseLessons) {
        try {

            CourseLessons saved = courseLessonsService.addCoursesLesson(sectionId,courseLessons);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khóa học.");
        }
    }
    @GetMapping("/{sectionId}")
    ResponseEntity<List<CourseLessons>> getCoursesLessonBySectionId(@PathVariable int sectionId) {
        List<CourseLessons> lessons = courseLessonsService.getCoursesLessonBySectionId(sectionId);
        return ResponseEntity.ok(lessons);
    }
    @GetMapping("/lesson/{lessonId}")
    ResponseEntity<CourseLessons> findByLessonId(@PathVariable int lessonId) {
        return ResponseEntity.ok(courseLessonsService.findById(lessonId));
    }
    @PutMapping("/lesson/{lessonId}")
    public ResponseEntity<?> update(@PathVariable int lessonId, @RequestBody CourseLessons lesson) {
//        try {
            CourseLessons updated = courseLessonsService.updateCoursesLesson(lessonId, lesson);
            return ResponseEntity.ok(updated);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bài học.");
//        }
    }

    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<?> delete(@PathVariable int lessonId) {
        try {
            courseLessonsService.deleteCoursesLesson(lessonId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bài học.");
        }
    }
}
