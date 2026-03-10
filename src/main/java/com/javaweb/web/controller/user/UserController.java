package com.javaweb.web.controller.user;

import com.javaweb.web.entity.Users;
import com.javaweb.web.service.UsersService;
import com.javaweb.web.util.ChangePasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/users/user")
public class UserController {
    @Autowired
    private UsersService usersService;
    @PostMapping
    ResponseEntity<String> createUser(@RequestBody Users user) {
        usersService.userRegister(user);
        return ResponseEntity.ok("Đăng ký thành công");
    }
    @PutMapping
    ResponseEntity<?> updateUser(@RequestBody Users userUpdate ,@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Users user = usersService.getUserByName(username);
        user.setName(userUpdate.getName());
        user.setEmail(userUpdate.getEmail());
        usersService.updateUser(user);
        return ResponseEntity.ok("Đổi thông tin thành công");
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Users user = usersService.getUserByName(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordUtil request) {
        try {
            // Lấy thông tin user từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Tìm user theo username (email hoặc name)
            Users user = usersService.getUserByUsername(userDetails.getUsername());

            // Gọi service đổi mật khẩu
            usersService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());

            return ResponseEntity.ok("Đổi mật khẩu thành công!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }}
