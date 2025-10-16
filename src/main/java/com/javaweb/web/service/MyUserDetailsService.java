package com.javaweb.web.service;

import com.javaweb.web.entity.Admin;
import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.AdminRepo;
import com.javaweb.web.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepo userRepository;
    @Autowired private AdminRepo adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kiểm tra bảng User
        Optional<Users> user = userRepository.findByName(username);
        if (user.isPresent()) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.get().getName())
                    .password(user.get().getPasswordHash())
                    .authorities("ROLE_USER") // Gán quyền USER
                    .build();
        }

        // Kiểm tra bảng Admin
        Optional<Admin> admin = adminRepository.findByName(username);
        if (admin.isPresent()) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(admin.get().getName())
                    .password(admin.get().getPasswordHash())
                    .authorities("ROLE_ADMIN") // Gán quyền ADMIN
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

    // Hàm phụ để lấy ID người dùng (dùng trong JwtUtil)
    public int getIdByName(String username) {
        Optional<Users> user = userRepository.findByName(username);
        if (user.isPresent()) {
            return user.get().getId();
        }
        Optional<Admin> admin = adminRepository.findByName(username);
        if (admin.isPresent()) {
            return admin.get().getId();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}