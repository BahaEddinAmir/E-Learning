package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.dto.*;
import com.arabsoft.elearning.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/courses/{courseId}/quiz")
    public ResponseEntity<QuizResponse> createQuiz(
            @PathVariable Long courseId,
            @RequestBody QuizRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.createQuiz(courseId, request, authentication.getName()));
    }

    @GetMapping("/quiz/{quizId}/take")
    public ResponseEntity<QuizTakeResponse> getQuizToTake(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizToTake(quizId));
    }

    @PostMapping("/quiz/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @RequestBody QuizSubmitRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(quizService.submitQuiz(request, authentication.getName()));
    }
}