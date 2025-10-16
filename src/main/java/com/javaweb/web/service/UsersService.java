package com.javaweb.web.service;

import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.UsersRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UsersRepo userRepo;

    public Users userRegister(Users user) {
        if (userRepo.existsByName(user.getName())) {
            return null;
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }
    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }
    public Users getUserByName(String name) {
        if (userRepo.findByName(name).isPresent()) {
            return userRepo.findByName(name)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        return null;
    }
    public Users getUserById(int id) {
        return userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }
    public Users getUserByEmail(String email) {
        if (userRepo.findByEmail(email) != null) {
            return userRepo.findByEmail(email);
        }
        return null;
    }
    public int count(){
        return (int )userRepo.count();
    }
    public void updateUser(Users user) {
        Optional<Users> optional = userRepo.findById(user.getId());
        Users users = optional.get();
        users.setName(user.getName());
        users.setEmail(user.getEmail());
        userRepo.save(users);
    }
    public void increaseBalance(int id, BigDecimal amount) {
        Users user = userRepo.findById(id).orElseThrow();
        user.setBalance(user.getBalance().add(amount));
        userRepo.save(user);
    }

    public void decreaseBalance(int id, BigDecimal amount) {
        Users user = userRepo.findById(id).orElseThrow();
        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Không đủ số dư.");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepo.save(user);
    }
}
