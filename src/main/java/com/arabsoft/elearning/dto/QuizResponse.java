package com.arabsoft.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private int passingScore;
    private int maxAttempts;
    private Long courseId;
    private List<QuestionRequest> questions;
}