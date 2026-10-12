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
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String niveau;
    private String categoryName;
    private String formateurName;
    private int modulesCount;
    private Long quizId;
    private List<ModuleResponse> modules;
}