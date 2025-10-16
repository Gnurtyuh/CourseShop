package com.javaweb.web.service;

import com.javaweb.web.entity.CourseLessons;
import com.javaweb.web.entity.CourseSections;
import com.javaweb.web.repository.CourseLessonsRepo;
import com.javaweb.web.repository.CourseSectionsRepo;
import com.javaweb.web.repository.EnrollmentsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseLessonsService {
    @Autowired
    private CourseLessonsRepo coursesLessonsRepo;
    @Autowired private EnrollmentsRepo enrollRepo;
    @Autowired
    private CourseSectionsRepo courseSectionsRepo;
    public CourseLessons getLessonWithAccessCheck(int id, int userId) {
        CourseLessons lesson = coursesLessonsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        int courseId = lesson.getSection().getCourse().getId();

        boolean enrolled = enrollRepo.existsByUserIdAndCourseId(userId, courseId);
        if (!enrolled) {
            throw new AccessDeniedException("Bạn chưa mua khóa học này");
        }
        return lesson;
    }
    public List<CourseLessons> getAllCoursesLessons() {
        return coursesLessonsRepo.findAll();
    }
    public CourseLessons findById(int id) {
        return coursesLessonsRepo.findById(id).orElseThrow(()-> new RuntimeException("Course Lesson not found"));
    }
    public CourseLessons getCoursesLessonByName(String title) {
        return coursesLessonsRepo.findByTitle(title);
    }
    public List<CourseLessons> getCoursesLessonBySectionId(int sectionId) {
        return coursesLessonsRepo.getCoursesLessonBySectionId(sectionId);
    }

    public CourseLessons addCoursesLesson(int sectionId,CourseLessons coursesLesson) {
        CourseSections section = courseSectionsRepo.findById(sectionId).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        coursesLesson.setSection(section);
        return coursesLessonsRepo.save(coursesLesson);
    }
    public CourseLessons updateCoursesLesson(int id,CourseLessons coursesLesson) {
            CourseLessons courseLessons = findById(id);
            courseLessons.setTitle(coursesLesson.getTitle());
            courseLessons.setVideoUrl(coursesLesson.getVideoUrl());
            courseLessons.setContent(coursesLesson.getTitle());
            courseLessons.setLessonOrder(coursesLesson.getLessonOrder());
            return coursesLessonsRepo.save(courseLessons);
    }
    public void deleteCoursesLesson(int id) {
            coursesLessonsRepo.deleteById(id);
    }
}
