package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.EnrollmentResponse;
import com.arabsoft.elearning.entity.Course;
import com.arabsoft.elearning.entity.Enrollment;
import com.arabsoft.elearning.entity.User;
import com.arabsoft.elearning.enums.EnrollStatus;
import com.arabsoft.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    @Transactional
    public Enrollment enroll(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new RuntimeException("Déjà inscrit à ce cours");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollStatus.ACTIVE)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        return enrollments.stream()
                .map(enrollment -> toResponse(enrollment, student.getId()))
                .toList();
    }

    private EnrollmentResponse toResponse(Enrollment enrollment, Long studentId) {
        Course course = enrollment.getCourse();

        // Count total chapters in this course (across all modules)
        long totalChapters = chapterRepository.countByCourseId(course.getId());

        // Count chapters this student completed in this course
        List<Long> courseChapterIds = chapterRepository.findIdsByCourseId(course.getId());
        long chaptersCompleted = courseChapterIds.isEmpty() ? 0 :
                chapterProgressRepository.countByStudentIdAndChapterIdInAndIsCompletedTrue(studentId, courseChapterIds);

        int progressPercent = totalChapters == 0
                ? 0
                : (int) Math.round((chaptersCompleted * 100.0) / totalChapters);

        // Number of modules (for display)
        int totalModules = course.getModules() != null ? course.getModules().size() : 0;

        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .status(enrollment.getStatus().name())
                .totalModules(totalModules)
                .modulesCompleted((int) chaptersCompleted)
                .progress(progressPercent)
                .build();
    }

    public boolean isEnrolled(String email, Long courseId) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(),
                courseId
        );
    }
}