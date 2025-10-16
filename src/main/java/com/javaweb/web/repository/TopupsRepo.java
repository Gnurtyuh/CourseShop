package com.javaweb.web.repository;

import com.javaweb.web.entity.Topups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopupsRepo extends JpaRepository<Topups, Integer> {
    List<Topups> findByUserId(Integer userId);
}
