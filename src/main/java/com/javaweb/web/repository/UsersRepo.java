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
    Users findByEmail(String email);
    Optional<Users> findByName(String name);

    // Tìm user theo tên
    @Query("SELECT u FROM Users u WHERE u.name = :name")
    Users findUserByName(@Param("name") String name);

    // Kiểm tra tên đã tồn tại chưa (loại trừ user hiện tại)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE LOWER(TRIM(u.name)) = LOWER(TRIM(:name)) AND u.id != :id")
    boolean existsByNameExcludingId(@Param("name") String name, @Param("id") int id);

    // Kiểm tra email đã tồn tại chưa (loại trừ user hiện tại)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email)) AND u.id != :id")
    boolean existsByEmailExcludingId(@Param("email") String email, @Param("id") int id);
}