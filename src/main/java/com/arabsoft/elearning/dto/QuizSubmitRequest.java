package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmitRequest {
    private Long quizId;
    private Map<Long, String> answers;
}