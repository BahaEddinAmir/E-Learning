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
public class ChapterResponse {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private int videoDuration;
    private int orderIndex;
    private List<QuestionResponse> questions;
}