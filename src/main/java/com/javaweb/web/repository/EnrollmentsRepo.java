package com.javaweb.web.repository;

import com.javaweb.web.entity.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentsRepo extends JpaRepository<Enrollments, Integer> {
    List<Enrollments> findByUserId(Integer userId);
    List<Enrollments> findByCourseId(Integer courseId);
    boolean existsByUserIdAndCourseId(int userId,int courseId);
}
