package com.arabsoft.elearning.controller;

import com.arabsoft.elearning.dto.ModuleRequest;
import com.arabsoft.elearning.dto.ModuleResponse;
import com.arabsoft.elearning.dto.VideoRequest;
import com.arabsoft.elearning.dto.VideoResponse;
import com.arabsoft.elearning.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleResponse>> getModules(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<ModuleResponse> createModule(
            @PathVariable Long courseId,
            @RequestBody ModuleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(moduleService.createModule(courseId, request, authentication.getName()));
    }

    @PostMapping("/modules/{moduleId}/videos")
    public ResponseEntity<VideoResponse> addVideo(
            @PathVariable Long moduleId,
            @RequestBody VideoRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(moduleService.addVideo(moduleId, request, authentication.getName()));
    }
}