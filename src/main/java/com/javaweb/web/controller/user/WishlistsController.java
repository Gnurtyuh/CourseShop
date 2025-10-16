package com.javaweb.web.controller.user;

import com.javaweb.web.entity.Courses;
import com.javaweb.web.entity.Users;
import com.javaweb.web.service.UsersService;
import com.javaweb.web.service.WishlistsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/wishlist")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WishlistsController {
    @Autowired
    WishlistsService wishlistsService;
    @Autowired
    UsersService usersService;
    @PostMapping("/add")
    public ResponseEntity<String> add(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int courseId) {
        Users user  = usersService.getUserByName(userDetails.getUsername());
        String msg = wishlistsService.addToWishlist(user.getId(), courseId);
        return ResponseEntity.ok(msg);
    }
    @DeleteMapping("/remove")
    public ResponseEntity<String> remove(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int courseId) {
        Users user  = usersService.getUserByName(userDetails.getUsername());
        String msg = wishlistsService.removeFromWishlist(user.getId(), courseId);
        return ResponseEntity.ok(msg);
    }
    @GetMapping("/listCourses")
    public ResponseEntity<List<Courses>> getAll(@RequestParam int userId) {
        return ResponseEntity.ok(wishlistsService.getWishlist(userId));
    }
}
