package com.javaweb.web.controller.pub;

import com.javaweb.web.dto.RegisterRequest;
import com.javaweb.web.entity.Authentications;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.AuthenticationService;
import com.javaweb.web.service.UsersService;
import com.javaweb.web.util.AuthenticationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    final AuthenticationService authenticationService;

    private final UsersService usersService;
    @PostMapping("/login")
    public ResponseEntity<AuthenticationUtil> authenticate(@RequestBody Authentications authentications) {
        var result = authenticationService.authenticate(authentications);
        return ResponseEntity.ok(result);
    }
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest user) {
        if (usersService.getUserByUsername(user.getName()) != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tên đã tồn tại");
        }
        if( usersService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email đã tồn tại");
        }
        if(user.getPassword() == null ||user.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email đã tồn tại");
        }
        usersService.userRegister(user);
        return ResponseEntity.ok("Đăng ký thành công");
    }
}
