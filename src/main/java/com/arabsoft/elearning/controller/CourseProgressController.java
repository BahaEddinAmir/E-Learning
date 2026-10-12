package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.dto.*;
import com.arabsoft.elearning.service.CourseProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class CourseProgressController {

    private final CourseProgressService courseProgressService;

    @PostMapping("/chapter/quiz")
    public ResponseEntity<ChapterQuizResult> submitChapterQuiz(
            @RequestBody ChapterQuizSubmitRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                courseProgressService.submitChapterQuiz(request, authentication.getName())
        );
    }

    @PostMapping("/final-quiz")
    public ResponseEntity<FinalQuizResult> submitFinalQuiz(
            @RequestBody FinalQuizSubmitRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                courseProgressService.submitFinalQuiz(request, authentication.getName())
        );
    }

    @GetMapping("/chapter/{chapterId}/accessible")
    public ResponseEntity<Boolean> isChapterAccessible(
            @PathVariable Long chapterId,
            Authentication authentication) {
        return ResponseEntity.ok(
                courseProgressService.isChapterAccessible(chapterId, authentication.getName())
        );
    }

    @GetMapping("/course/{courseId}/completed-chapters")
    public ResponseEntity<List<Long>> getCompletedChapterIds(
            @PathVariable Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(
                courseProgressService.getCompletedChapterIds(courseId, authentication.getName())
        );
    }
}