package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.AdminStatsResponse;
import com.arabsoft.elearning.dto.AdminUserResponse;
import com.arabsoft.elearning.entity.User;
import com.arabsoft.elearning.enums.CourseStatus;
import com.arabsoft.elearning.repository.CertificateRepository;
import com.arabsoft.elearning.repository.CourseRepository;
import com.arabsoft.elearning.repository.EnrollmentRepository;
import com.arabsoft.elearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    public AdminStatsResponse getStats() {
        long publishedCourses = courseRepository.findByStatus(CourseStatus.PUBLISHED).size();

        return AdminStatsResponse.builder()
                .users(userRepository.count())
                .courses(publishedCourses)
                .enrollments(enrollmentRepository.count())
                .certificates(certificateRepository.count())
                .build();
    }

    public List<AdminUserResponse> getRecentUsers() {
        List<User> users = userRepository.findTop10ByOrderByCreatedAtDesc();
        return users.stream()
                .map(user -> AdminUserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .isActive(user.isActive())
                        .build())
                .collect(Collectors.toList());
    }
}