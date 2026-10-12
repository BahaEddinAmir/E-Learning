package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.dto.EnrollmentRequest;
import com.arabsoft.elearning.dto.EnrollmentResponse;
import com.arabsoft.elearning.entity.Enrollment;
import com.arabsoft.elearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<Enrollment> enroll(
            @RequestBody EnrollmentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                enrollmentService.enroll(request.getCourseId(), authentication.getName())
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(Authentication authentication) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(authentication.getName()));
    }
    @GetMapping("/check/{courseId}")
    public ResponseEntity<Boolean> checkEnrollment(
            @PathVariable Long courseId,
            Authentication authentication) {

        return ResponseEntity.ok(
                enrollmentService.isEnrolled(
                        authentication.getName(),
                        courseId
                )
        );
    }
}
