package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private String niveau;
    private Long categoryId;
    private List<ModuleDto> modules;
    private FinalQuizDto finalQuiz;
}