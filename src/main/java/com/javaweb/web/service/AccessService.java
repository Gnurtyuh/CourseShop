package com.javaweb.web.service;

import com.javaweb.web.repository.EnrollmentsRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccessService {
    @Autowired
    private EnrollmentsRepo enrollmentRepo;

    public boolean checkAccessToCourse(int userId, int courseId) {
        return enrollmentRepo.existsByUserIdAndCourseId(userId, courseId);
    }
}
