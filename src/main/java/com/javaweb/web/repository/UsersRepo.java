package com.javaweb.web.repository;

import com.javaweb.web.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Integer> {
    boolean existsByName(String name);
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Users findByEmail(@Param("email")String email);
    Optional<Users> findByName(String name);
    boolean existsByEmail(String email);
    // Tìm user theo tên
    @Query("SELECT u FROM Users u WHERE u.name = :name")
    Users findUserByName(@Param("name") String name);

}