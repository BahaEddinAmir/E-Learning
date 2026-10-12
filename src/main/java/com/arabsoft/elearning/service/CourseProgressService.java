package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.*;
import com.arabsoft.elearning.entity.*;
import com.arabsoft.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseProgressService {

    private final ChapterRepository chapterRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final FinalQuizRepository finalQuizRepository;
    private final QuizResultRepository quizResultRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Soumettre le quiz d'un chapitre
    public ChapterQuizResult submitChapterQuiz(ChapterQuizSubmitRequest request, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));

        List<ChapterQuestion> questions = chapter.getQuestions();
        if (questions == null || questions.isEmpty()) {
            // Pas de questions → passer directement
            markChapterComplete(student, chapter, 100, true);
            return ChapterQuizResult.builder()
                    .score(100).passed(true).canProceed(true)
                    .message("Chapitre complété !").build();
        }

        // Calculer le score
        int correct = 0;
        for (ChapterQuestion q : questions) {
            String answer = request.getAnswers().get(q.getId());
            if (answer != null && answer.equals(q.getCorrectAnswer())) {
                correct++;
            }
        }

        int score = (correct * 100) / questions.size();
        boolean passed = score >= 100; // 100% requis pour passer au chapitre suivant

        markChapterComplete(student, chapter, score, passed);

        String message = passed
                ? "✅ Excellent ! Vous pouvez passer au chapitre suivant."
                : "❌ Score insuffisant. Réessayez pour continuer.";

        return ChapterQuizResult.builder()
                .score(score).passed(passed).canProceed(passed)
                .message(message).build();
    }

    private void markChapterComplete(User student, Chapter chapter, int score, boolean passed) {
        ChapterProgress progress = chapterProgressRepository
                .findByStudentIdAndChapterId(student.getId(), chapter.getId())
                .orElse(ChapterProgress.builder()
                        .student(student).chapter(chapter).build());

        progress.setQuizScore(score);
        progress.setQuizPassed(passed);
        if (passed) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        chapterProgressRepository.save(progress);
    }

    // Soumettre le quiz final
    public FinalQuizResult submitFinalQuiz(FinalQuizSubmitRequest request, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        FinalQuiz finalQuiz = finalQuizRepository.findByCourseId(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Quiz final non trouvé"));

        // Vérifier nb tentatives
        int attempts = quizResultRepository
                .countByStudentIdAndQuizId(student.getId(), finalQuiz.getId());

        if (attempts >= finalQuiz.getMaxAttempts()) {
            return FinalQuizResult.builder()
                    .score(0).passed(false)
                    .attemptNumber(attempts)
                    .message("Nombre maximum de tentatives atteint.").build();
        }

        // Calculer le score
        List<FinalQuizQuestion> questions = finalQuiz.getQuestions();
        int correct = 0;
        for (FinalQuizQuestion q : questions) {
            String answer = request.getAnswers().get(q.getId());
            if (answer != null && answer.equals(q.getCorrectAnswer())) {
                correct++;
            }
        }

        int score = questions.isEmpty() ? 0 : (correct * 100) / questions.size();
        boolean passed = score >= finalQuiz.getPassingScore();

        // Sauvegarder résultat
        QuizResult result = QuizResult.builder()
                .student(student)
                .quiz(null)
                .score(score)
                .passed(passed)
                .attemptNumber(attempts + 1)
                .build();
        quizResultRepository.save(result);

        String certNumber = null;
        String certId = null;

        if (passed && !certificateRepository.existsByStudentIdAndCourseId(
                student.getId(), course.getId())) {
            // Générer le certificat
            Certificate cert = Certificate.builder()
                    .student(student)
                    .course(course)
                    .filePath("/certificates/" + student.getId() + "_" + course.getId() + ".pdf")
                    .build();
            Certificate saved = certificateRepository.save(cert);
            certNumber = saved.getUniqueNumber().toString();
            certId = saved.getId().toString();

            // Mettre à jour l'enrollment
            enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                    .ifPresent(enrollment -> {
                        enrollment.setStatus(com.arabsoft.elearning.enums.EnrollStatus.COMPLETED);
                        enrollmentRepository.save(enrollment);
                    });
        }

        String message = passed
                ? "🎉 Félicitations ! Vous avez réussi le quiz final avec " + score + "%"
                : "Score : " + score + "%. Il faut " + finalQuiz.getPassingScore() + "% pour réussir.";

        return FinalQuizResult.builder()
                .score(score).passed(passed)
                .attemptNumber(attempts + 1)
                .message(message)
                .certificateId(certId)
                .certificateNumber(certNumber)
                .build();
    }

    // Vérifier si un chapitre est accessible
    public boolean isChapterAccessible(Long chapterId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Long studentId = student.getId();

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));

        List<Chapter> moduleChapters = chapterRepository
                .findByModuleIdOrderByOrderIndex(chapter.getModule().getId());

        int chapterIndex = -1;
        for (int i = 0; i < moduleChapters.size(); i++) {
            if (moduleChapters.get(i).getId().equals(chapterId)) {
                chapterIndex = i;
                break;
            }
        }

        if (chapterIndex == 0) return true; // Premier chapitre toujours accessible

        // Vérifier que le chapitre précédent est complété
        Chapter previousChapter = moduleChapters.get(chapterIndex - 1);
        return chapterProgressRepository
                .findByStudentIdAndChapterId(studentId, previousChapter.getId())
                .map(ChapterProgress::getIsCompleted)
                .orElse(false);
    }

    // Retourner les IDs des chapitres complétés dans un cours
    public List<Long> getCompletedChapterIds(Long courseId, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Long> courseChapterIds = chapterRepository.findIdsByCourseId(courseId);
        if (courseChapterIds.isEmpty()) return List.of();

        return chapterProgressRepository
                .findByStudentIdAndChapterIdIn(student.getId(), courseChapterIds)
                .stream()
                .filter(cp -> Boolean.TRUE.equals(cp.getIsCompleted()))
                .map(cp -> cp.getChapter().getId())
                .toList();
    }
}