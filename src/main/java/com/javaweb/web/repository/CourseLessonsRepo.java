package com.javaweb.web.repository;

import com.javaweb.web.entity.CourseLessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLessonsRepo extends JpaRepository<CourseLessons, Integer> {
    public CourseLessons findByTitle(String title);
    public List<CourseLessons> getCoursesLessonBySectionId(int sectionId);
}
