package com.arabsoft.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormateurCourseResponse {
    private Long id;
    private String title;
    private String status;
    private int studentsCount;
}