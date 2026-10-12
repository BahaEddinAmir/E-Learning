package com.arabsoft.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalQuizResult {
    private int score;
    private boolean passed;
    private int attemptNumber;
    private String message;
    private String certificateId;
    private String certificateNumber;
}