package com.javaweb.web.controller.user;

import com.javaweb.web.entity.Users;
import com.javaweb.web.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
