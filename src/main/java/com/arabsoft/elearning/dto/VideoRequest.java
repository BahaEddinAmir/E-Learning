package com.arabsoft.elearning.dto;

import lombok.Data;

@Data
public class VideoRequest {
    private String title;
    private String url;
    private int duration;
    private int orderIndex;
}