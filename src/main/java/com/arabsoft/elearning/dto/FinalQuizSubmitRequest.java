package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.Map;

@Data
public class FinalQuizSubmitRequest {
    private Long courseId;
    private Map<Long, String> answers;
}