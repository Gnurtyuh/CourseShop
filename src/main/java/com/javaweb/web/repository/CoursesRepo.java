package com.javaweb.web.repository;

import com.javaweb.web.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface    CoursesRepo extends JpaRepository<Courses, Integer> {
    Courses findByTitle(String title);

}
