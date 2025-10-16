package com.javaweb.web.repository;

import com.javaweb.web.entity.CourseSections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionsRepo extends JpaRepository<CourseSections, Integer> {
    CourseSections findByTitle(String title);
    List<CourseSections> findByCourseId(int courseId);
}
