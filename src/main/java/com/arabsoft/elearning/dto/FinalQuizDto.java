package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.List;

@Data
public class FinalQuizDto {
    private String title;
    private int passingScore;
    private int maxAttempts;
    private List<QuestionDto> questions;
}