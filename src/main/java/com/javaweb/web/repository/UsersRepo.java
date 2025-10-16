package com.javaweb.web.repository;

import com.javaweb.web.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Integer> {
    boolean existsByName(String name);
    Users findByEmail(String email);
    Optional<Users> findByName(String name);
}
