package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.dto.CourseRequest;
import com.arabsoft.elearning.dto.CourseResponse;
import com.arabsoft.elearning.dto.FormateurStatsResponse;
import com.arabsoft.elearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllPublishedCourses());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CourseResponse>> getMyCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getCoursesByFormateur(authentication.getName()));
    }

    @GetMapping("/my/stats")
    public ResponseEntity<FormateurStatsResponse> getMyStats(Authentication authentication) {
        return ResponseEntity.ok(courseService.getFormateurStats(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestBody CourseRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(courseService.createCourse(request, authentication.getName()));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(courseService.updateCourse(id, request, authentication.getName()));
    }

    @PutMapping("/{id:\\d+}/publish")
    public ResponseEntity<CourseResponse> publishCourse(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(courseService.publishCourse(id, authentication.getName()));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            Authentication authentication) {
        courseService.deleteCourse(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}