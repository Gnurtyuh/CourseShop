package com.javaweb.web.controller.user;

import com.javaweb.web.entity.Enrollments;
import com.javaweb.web.entity.Topups;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.EnrollmentsService;
import com.javaweb.web.service.TopupsService;
import com.javaweb.web.service.UsersService;
import com.javaweb.web.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users/payment")

public class PaymentController {
    @Autowired
    private TopupsService topupsService;
    @Autowired
    private EnrollmentsService enrollmentService;

    @Autowired private UsersService usersService;

    @PostMapping("/topup")
    public ResponseEntity<String> addtopup(@RequestBody Topups topups,@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = usersService.getUserByName(userDetails.getUsername());
            topups.setUser(user);
            topupsService.addTopups(topups);
            return ResponseEntity.ok("Nạp tiền thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/buy-course")
    public ResponseEntity<?> buyCourse(@RequestParam int courseId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = usersService.getUserByName(userDetails.getUsername());
            String message = enrollmentService.enroll(user.getId(), courseId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.ok("no");
        }
    }

    @GetMapping("/topup/history")
    public ResponseEntity<List<Topups>> getTopupHistory(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = usersService.getUserByName(userDetails.getUsername());
            List<Topups> topupHistory = topupsService.findByUserId(user.getId());
            return ResponseEntity.ok(topupHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
    }




}
