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
public class ModuleResponse {
    private Long id;
    private String title;
    private String description;
    private int orderIndex;
    private List<ChapterResponse> chapters;
    private List<VideoResponse> videos;
}