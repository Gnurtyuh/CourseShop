package com.javaweb.web.repository;

import com.javaweb.web.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Integer> {
    public boolean existsByName(String name);
    Optional<Admin> findByName(String adminName);
}
