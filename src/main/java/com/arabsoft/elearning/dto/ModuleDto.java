package com.arabsoft.elearning.dto;

import lombok.Data;
import java.util.List;

@Data
public class ModuleDto {
    private String title;
    private String description;
    private int orderIndex;
    private List<ChapterDto> chapters;
}