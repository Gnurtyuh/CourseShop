package com.javaweb.web.repository;

import com.javaweb.web.entity.Wishlists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface WishlistsRepo extends JpaRepository<Wishlists, Integer> {
    boolean existsByUserIdAndCourseId(int userId, int courseId);
    Optional<Wishlists> findByUserIdAndCourseId(int userId, int courseId);
    List<Wishlists> findAllByUserId(int userId);
    List<Wishlists> findByCourseId(int courseId);
}
