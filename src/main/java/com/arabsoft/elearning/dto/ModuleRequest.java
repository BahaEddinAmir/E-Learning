package com.arabsoft.elearning.dto;

import lombok.Data;

@Data
public class ModuleRequest {
    private String title;
    private String description;
    private int orderIndex;
}