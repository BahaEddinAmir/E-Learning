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
public class QuizTakeResponse {
    private Long id;
    private String title;
    private int passingScore;
    private int maxAttempts;
    private List<QuestionResponse> questions;
}