package com.javaweb.web.controller.pub;

import com.javaweb.web.entity.Authentications;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.AuthenticationService;
import com.javaweb.web.service.UsersService;
import com.javaweb.web.util.AuthenticationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private UsersService usersService;
    @PostMapping("/login")
    public ResponseEntity<AuthenticationUtil> authenticate(@RequestBody Authentications authentications) {
        var result = authenticationService.authenticate(authentications);
        return ResponseEntity.ok(result);
    }
    @PostMapping
    ResponseEntity<String> createUser(@RequestBody Users user) {
        usersService.userRegister(user);
        return ResponseEntity.ok("Đăng ký thành công");
    }
}
