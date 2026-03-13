package com.javaweb.web.service;

import com.github.dockerjava.api.exception.BadRequestException;
import com.javaweb.web.dto.RegisterRequest;
import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.UsersRepo;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.config.http.MatcherType.regex;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepo userRepo;
    private static final String regex = "^(.+)@(.+)$";
    @Transactional
    public Users userRegister(RegisterRequest user) {
        if (userRepo.existsByName(user.getName())) {
            throw new RuntimeException("Tên đã tồn tại");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new BadRequestException("Định dạng email bị lỗi");
        }
        if (!user.getName().matches("^[A-Za-zÀ-ỹ\\s]+$")) {
            throw new BadRequestException("Tên không hợp lệ");
        }
        if (user.getPassword().length() < 6) {
            throw new BadRequestException("Mật khẩu quá ngắn");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Users users = new Users();
        users.setName(user.getName());
        users.setEmail(user.getEmail());
        users.setBalance(BigDecimal.valueOf(0));
        users.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(users);
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

        return userRepo.findByEmail(email);
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

    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // Tìm user theo ID
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng!");
        }

        // Kiểm tra mật khẩu mới có trùng với mật khẩu cũ không
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu mới phải khác mật khẩu hiện tại!");
        }

        // Kiểm tra độ dài mật khẩu mới
        if (newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        // Mã hóa và lưu mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
    public Users getUserByUsername(String username) {
        // Tìm theo email trước
        return userRepo.findUserByName(username);
    }
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        EmailValidator validator = EmailValidator.getInstance();

        return validator.isValid(email)||matcher.matches();
    }
}
