package com.javaweb.web.service;

import com.javaweb.web.entity.Admin;
import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepo adminRepo;
    public Admin adminRegister(Admin admin) {
        if (adminRepo.existsByName(admin.getName())) {
            return null;
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        admin.setPasswordHash(passwordEncoder.encode(admin.getPasswordHash()));
        return adminRepo.save(admin);
    }
}
