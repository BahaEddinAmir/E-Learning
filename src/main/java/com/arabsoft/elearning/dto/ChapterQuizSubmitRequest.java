package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ChapterQuizSubmitRequest {
    private Long chapterId;
    private Map<Long, String> answers;
}