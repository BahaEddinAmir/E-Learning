package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChapterDto {
    private String title;
    private String description;
    private String videoUrl;
    private int videoDuration;
    private int orderIndex;
    private List<QuestionDto> questions;
}