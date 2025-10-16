package com.javaweb.web.controller.user;

import com.javaweb.web.entity.CourseLessons;
import com.javaweb.web.entity.Courses;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.AccessService;
import com.javaweb.web.service.CourseLessonsService;
import com.javaweb.web.service.UsersService;
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
@RequestMapping("/api/users/courseLesson")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseLessonsController {
    @Autowired
    CourseLessonsService courseLessonsService;
    @Autowired
    UsersService usersService;
    @Autowired
    AccessService accessService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CourseLessons lesson = courseLessonsService.findById(id);
            Users user = usersService.getUserByName(userDetails.getUsername());
            accessService.checkAccessToCourse(user.getId(), lesson.getSection().getCourse().getId());
            return ResponseEntity.ok(lesson);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bài học.");
        }
    }
    @GetMapping("/findAll")
    ResponseEntity<List<CourseLessons>> getCoursesLessonBySectionId(@RequestParam("section_id") int sectionId) {
        List<CourseLessons> lessons = courseLessonsService.getCoursesLessonBySectionId(sectionId);
        return ResponseEntity.ok(lessons);
    }
    @GetMapping("/find{id}")
    ResponseEntity<CourseLessons> findByLessonId(@PathVariable("id") int id) {
        return ResponseEntity.ok(courseLessonsService.findById(id));
    }
}
