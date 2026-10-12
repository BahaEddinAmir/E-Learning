package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.*;
import com.arabsoft.elearning.entity.*;
import com.arabsoft.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizResultRepository quizResultRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;

    public QuizResponse createQuiz(Long courseId, QuizRequest request, String email) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getFormateur().getEmail().equals(email)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce cours");
        }

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .passingScore(request.getPassingScore())
                .maxAttempts(request.getMaxAttempts())
                .course(course)
                .build();
        quizRepository.save(quiz);

        for (QuestionRequest qr : request.getQuestions()) {
            Question question = Question.builder()
                    .content(qr.getContent())
                    .optionA(qr.getOptionA())
                    .optionB(qr.getOptionB())
                    .optionC(qr.getOptionC())
                    .optionD(qr.getOptionD())
                    .correctAnswer(qr.getCorrectAnswer())
                    .orderIndex(qr.getOrderIndex())
                    .quiz(quiz)
                    .build();
            questionRepository.save(question);
        }

        return getQuizForEdit(quiz.getId(), email);
    }

    public QuizResponse getQuizForEdit(Long quizId, String email) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        if (!quiz.getCourse().getFormateur().getEmail().equals(email)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à voir ce quiz");
        }

        List<QuestionRequest> questions = questionRepository.findByQuizIdOrderByOrderIndex(quizId)
                .stream()
                .map(q -> {
                    QuestionRequest qr = new QuestionRequest();
                    qr.setContent(q.getContent());
                    qr.setOptionA(q.getOptionA());
                    qr.setOptionB(q.getOptionB());
                    qr.setOptionC(q.getOptionC());
                    qr.setOptionD(q.getOptionD());
                    qr.setCorrectAnswer(q.getCorrectAnswer());
                    qr.setOrderIndex(q.getOrderIndex());
                    return qr;
                })
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .courseId(quiz.getCourse().getId())
                .questions(questions)
                .build();
    }

    public QuizTakeResponse getQuizToTake(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        List<QuestionResponse> questions = questionRepository.findByQuizIdOrderByOrderIndex(quizId)
                .stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .content(q.getContent())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .orderIndex(q.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        return QuizTakeResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .questions(questions)
                .build();
    }

    public QuizResultResponse submitQuiz(QuizSubmitRequest request, String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        int attemptCount = quizResultRepository.countByStudentIdAndQuizId(student.getId(), quiz.getId());
        if (attemptCount >= quiz.getMaxAttempts()) {
            throw new RuntimeException("Nombre maximum de tentatives atteint");
        }

        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndex(quiz.getId());
        Map<Long, String> answers = request.getAnswers();

        long correctCount = questions.stream()
                .filter(q -> q.getCorrectAnswer().equalsIgnoreCase(answers.get(q.getId())))
                .count();

        int score = questions.isEmpty() ? 0 : Math.round((correctCount * 100f) / questions.size());
        boolean passed = score >= quiz.getPassingScore();

        QuizResult result = QuizResult.builder()
                .score(score)
                .passed(passed)
                .attemptNumber(attemptCount + 1)
                .student(student)
                .quiz(quiz)
                .build();
        quizResultRepository.save(result);

        String certificateUrl = null;
        String message;

        if (passed) {
            Course course = quiz.getCourse();
            boolean alreadyHasCert = certificateRepository.existsByStudentIdAndCourseId(student.getId(), course.getId());

            if (!alreadyHasCert) {
                Certificate certificate = Certificate.builder()
                        .student(student)
                        .course(course)
                        .uniqueNumber(UUID.randomUUID())
                        .build();
                certificateRepository.save(certificate);
                certificateUrl = "/api/certificates/" + certificate.getId();
            } else {
                Certificate existing = certificateRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                        .orElse(null);
                if (existing != null) {
                    certificateUrl = "/api/certificates/" + existing.getId();
                }
            }
            message = "Félicitations, vous avez réussi le quiz !";
        } else {
            message = "Score insuffisant. Réessayez.";
        }

        return QuizResultResponse.builder()
                .score(score)
                .passed(passed)
                .attemptNumber(result.getAttemptNumber())
                .message(message)
                .certificateUrl(certificateUrl)
                .build();
    }
}