package com.arabsoft.elearning.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}