package com.arabsoft.elearning.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private int orderIndex;
}