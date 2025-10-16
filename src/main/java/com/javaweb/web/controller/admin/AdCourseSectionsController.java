package com.javaweb.web.controller.admin;

import com.javaweb.web.entity.CourseSections;
import com.javaweb.web.service.CourseSectionsService;
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
@RequestMapping("/api/admin/courseSection")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdCourseSectionsController {
    @Autowired
    CourseSectionsService sectionService;


    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<CourseSections>> getByCourse(@PathVariable int courseId) {
        List<CourseSections> sections = sectionService.getCourseSectionsByCourseId(courseId);
        return ResponseEntity.ok(sections);
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

    @PostMapping("/by-course/{courseId}")
    public ResponseEntity<?> add(@PathVariable int courseId, @RequestBody CourseSections section) {
        try {
            CourseSections saved = sectionService.addCourseSections(courseId, section);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khóa học.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CourseSections section) {
        try {
            CourseSections updated = sectionService.update(id, section);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương học.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            sectionService.deleteCourseSections(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương học.");
        }
    }
}
