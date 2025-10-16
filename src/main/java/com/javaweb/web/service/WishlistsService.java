package com.javaweb.web.service;

import com.javaweb.web.entity.Courses;
import com.javaweb.web.entity.Wishlists;
import com.javaweb.web.repository.CoursesRepo;
import com.javaweb.web.repository.UsersRepo;
import com.javaweb.web.repository.WishlistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistsService {
    @Autowired
    private WishlistsRepo wishlistRepo;

    @Autowired private UsersRepo userRepo;
    @Autowired private CoursesRepo courseRepo;

    public String addToWishlist(int userId, int courseId) {
        if (wishlistRepo.existsByUserIdAndCourseId(userId, courseId)) {
            return "Khóa học đã có trong danh sách yêu thích.";
        }

        Wishlists wishlist = new Wishlists();
        wishlist.setUser(userRepo.getReferenceById(userId));
        wishlist.setCourse(courseRepo.getReferenceById(courseId));
        wishlistRepo.save(wishlist);

        return "Đã thêm vào danh sách yêu thích.";
    }

    public String removeFromWishlist(int userId, int courseId) {
        Wishlists wishlist = wishlistRepo.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục yêu thích"));
        wishlistRepo.delete(wishlist);
        return "Đã xóa khỏi danh sách yêu thích.";
    }

    public List<Courses> getWishlist(int userId) {
        return wishlistRepo.findAllByUserId(userId)
                .stream()
                .map(Wishlists::getCourse)
                .distinct()
                .toList();
    }

    public List<Wishlists> getAllWishlists() {
        return wishlistRepo.findAll();
    }
    public Wishlists getWishlistById(int id) {
        return wishlistRepo.findById(id).orElseThrow(()-> new RuntimeException("Wishlist not found"));
    }
    public Wishlists addWishlists(Wishlists wishlists) {
        return wishlistRepo.save(wishlists);
    }
    public Wishlists updateWishlists(int id,Wishlists wishlists) {
        if (wishlistRepo.findById(id).orElseThrow(()-> new RuntimeException("Wishlist not found")) != null) {
            return wishlistRepo.save(wishlists);
        }
        return null;
    }
    public List<Wishlists> getWishlistsByCourseId(int courseId) {
        return wishlistRepo.findByCourseId(courseId);
    }
}
