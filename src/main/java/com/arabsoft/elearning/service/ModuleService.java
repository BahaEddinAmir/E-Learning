package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.ModuleRequest;
import com.arabsoft.elearning.dto.ModuleResponse;
import com.arabsoft.elearning.dto.VideoRequest;
import com.arabsoft.elearning.dto.VideoResponse;
import com.arabsoft.elearning.entity.Course;
import com.arabsoft.elearning.entity.CourseModule;
import com.arabsoft.elearning.entity.Video;
import com.arabsoft.elearning.repository.CourseRepository;
import com.arabsoft.elearning.repository.ModuleRepository;
import com.arabsoft.elearning.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;

    public List<ModuleResponse> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ModuleResponse createModule(Long courseId, ModuleRequest request, String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getFormateur().getEmail().equals(email)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }

        CourseModule module = CourseModule.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .course(course)
                .build();

        moduleRepository.save(module);
        return mapToResponse(module);
    }

    public VideoResponse addVideo(Long moduleId, VideoRequest request, String email) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module non trouvé"));

        if (!module.getCourse().getFormateur().getEmail().equals(email)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }

        Video video = Video.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .duration(request.getDuration())
                .orderIndex(request.getOrderIndex())
                .module(module)
                .build();

        videoRepository.save(video);
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .url(video.getUrl())
                .duration(video.getDuration())
                .orderIndex(video.getOrderIndex())
                .build();
    }

    private ModuleResponse mapToResponse(CourseModule module) {
        List<VideoResponse> videos = videoRepository.findByModuleIdOrderByOrderIndex(module.getId())
                .stream()
                .map(v -> VideoResponse.builder()
                        .id(v.getId())
                        .title(v.getTitle())
                        .url(v.getUrl())
                        .duration(v.getDuration())
                        .orderIndex(v.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .videos(videos)
                .build();
    }
}