package com.javaweb.web.controller.admin;

import com.javaweb.web.entity.*;
import com.javaweb.web.service.EnrollmentsService;
import com.javaweb.web.service.TopupsService;
import com.javaweb.web.service.UsersService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin/payment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdPaymentController {
    @Autowired
    TopupsService topupsService;
    @Autowired
    EnrollmentsService enrollmentService;
    @Autowired
    UsersService usersService;
    @PutMapping("/{id}/status")
    public ResponseEntity<?> UpdateTopups(@PathVariable int id,  @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || (!status.equals("success") && !status.equals("failed"))) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
        boolean updated = topupsService.updateTopups(id, status); // tự xử lý trong service
        return ResponseEntity.ok(updated ? "ok" : "no");
    }
    @GetMapping("/history")
    public List<Map<String, Object>> getSectionsWithLessons() {
        List<Users> users = usersService.getAllUsers();

        List<Map<String, Object>> response = new ArrayList<>();

        for (Users user : users ) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());

            List<Map<String, Object>> topupsForUser = new ArrayList<>();
            List<Topups> topups = topupsService.findByUserId(user.getId());
            for (Topups topup : topups) {
                Map<String, Object> topupMap = new HashMap<>();
                topupMap.put("id", topup.getId());
                topupMap.put("amount", topup.getAmount());
                topupMap.put("referenceCode", topup.getReferenceCode());
                topupMap.put("createAt", topup.getCreatedAt());
                topupMap.put("status", topup.getStatus());

                topupsForUser.add(topupMap);
            }
            userMap.put("topups", topupsForUser);
            response.add(userMap);
        }
        return response;
    }
    @GetMapping("/enrollments")
    public ResponseEntity<List<Enrollments>> getEnrolledCourses(@RequestParam int userId) {
        List<Enrollments> enrollments = enrollmentService.getEnrollmentByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }
    @GetMapping("/findAll")
    public ResponseEntity<List<Enrollments>> findAll() {
        List<Enrollments> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

}
