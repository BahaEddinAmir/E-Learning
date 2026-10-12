package com.arabsoft.elearning.service;

import com.arabsoft.elearning.dto.*;
import com.arabsoft.elearning.entity.*;
import com.arabsoft.elearning.enums.CourseStatus;
import com.arabsoft.elearning.enums.NiveauCours;
import com.arabsoft.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final FinalQuizRepository finalQuizRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<CourseResponse> getAllPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CourseResponse> getPendingCourses() {
        return courseRepository.findByStatus(CourseStatus.DRAFT)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<CourseResponse> getCoursesByFormateur(String email) {
        User formateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return courseRepository.findByFormateurId(formateur.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public FormateurStatsResponse getFormateurStats(String email) {
        User formateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<Course> courses = courseRepository.findByFormateurId(formateur.getId());
        
        int coursesCount = courses.size();
        int publishedCount = (int) courses.stream().filter(c -> c.getStatus() == CourseStatus.PUBLISHED).count();
        
        int studentsCount = 0;
        int quizzesCount = 0;
        
        for (Course c : courses) {
            studentsCount += enrollmentRepository.findByCourseId(c.getId()).size();
            
            // Check if there are any quizzes. The new model uses course.getFinalQuiz() but since it might not be initialized,
            // we can just check if final quiz is present. Wait, there's FinalQuizRepository. 
            if (finalQuizRepository.findByCourseId(c.getId()).isPresent()) {
                quizzesCount++;
            }
        }

        return FormateurStatsResponse.builder()
                .courses(coursesCount)
                .students(studentsCount)
                .quizzes(quizzesCount)
                .published(publishedCount)
                .build();
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        return mapToDetailResponse(course);
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request, String email) {
        User formateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        NiveauCours niveau = null;
        if (request.getNiveau() != null) {
            try { niveau = NiveauCours.valueOf(request.getNiveau()); }
            catch (Exception ignored) {}
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(CourseStatus.DRAFT)
                .niveau(niveau)
                .formateur(formateur)
                .category(category)
                .build();

        Course savedCourse = courseRepository.save(course);

        if (request.getModules() != null) {
            for (ModuleDto moduleDto : request.getModules()) {
                CourseModule module = CourseModule.builder()
                        .title(moduleDto.getTitle())
                        .description(moduleDto.getDescription())
                        .orderIndex(moduleDto.getOrderIndex())
                        .course(savedCourse)
                        .build();
                CourseModule savedModule = moduleRepository.save(module);

                if (moduleDto.getChapters() != null) {
                    for (ChapterDto chapterDto : moduleDto.getChapters()) {
                        Chapter chapter = Chapter.builder()
                                .title(chapterDto.getTitle())
                                .description(chapterDto.getDescription())
                                .videoUrl(chapterDto.getVideoUrl())
                                .videoDuration(chapterDto.getVideoDuration())
                                .orderIndex(chapterDto.getOrderIndex())
                                .module(savedModule)
                                .build();
                        Chapter savedChapter = chapterRepository.save(chapter);

                        if (chapterDto.getQuestions() != null) {
                            List<ChapterQuestion> questions = new ArrayList<>();
                            for (QuestionDto qDto : chapterDto.getQuestions()) {
                                ChapterQuestion q = ChapterQuestion.builder()
                                        .content(qDto.getContent())
                                        .optionA(qDto.getOptionA())
                                        .optionB(qDto.getOptionB())
                                        .optionC(qDto.getOptionC())
                                        .optionD(qDto.getOptionD())
                                        .correctAnswer(qDto.getCorrectAnswer())
                                        .orderIndex(qDto.getOrderIndex())
                                        .chapter(savedChapter)
                                        .build();
                                questions.add(q);
                            }
                            savedChapter.setQuestions(questions);
                            chapterRepository.save(savedChapter);
                        }
                    }
                }
            }
        }

        if (request.getFinalQuiz() != null) {
            FinalQuizDto fqDto = request.getFinalQuiz();
            FinalQuiz finalQuiz = FinalQuiz.builder()
                    .title(fqDto.getTitle())
                    .passingScore(fqDto.getPassingScore() > 0 ? fqDto.getPassingScore() : 70)
                    .maxAttempts(fqDto.getMaxAttempts() > 0 ? fqDto.getMaxAttempts() : 3)
                    .course(savedCourse)
                    .build();
            FinalQuiz savedFQ = finalQuizRepository.save(finalQuiz);

            if (fqDto.getQuestions() != null) {
                List<FinalQuizQuestion> fqQuestions = new ArrayList<>();
                for (QuestionDto qDto : fqDto.getQuestions()) {
                    FinalQuizQuestion q = FinalQuizQuestion.builder()
                            .content(qDto.getContent())
                            .optionA(qDto.getOptionA())
                            .optionB(qDto.getOptionB())
                            .optionC(qDto.getOptionC())
                            .optionD(qDto.getOptionD())
                            .correctAnswer(qDto.getCorrectAnswer())
                            .orderIndex(qDto.getOrderIndex())
                            .finalQuiz(savedFQ)
                            .build();
                    fqQuestions.add(q);
                }
                savedFQ.setQuestions(fqQuestions);
                finalQuizRepository.save(savedFQ);
            }
        }

        return mapToResponse(savedCourse);
    }

    public CourseResponse updateCourse(Long id, CourseRequest request, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getFormateur().getEmail().equals(email)) {
            throw new RuntimeException("Non autorisé");
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());

        if (request.getNiveau() != null) {
            try { course.setNiveau(NiveauCours.valueOf(request.getNiveau())); }
            catch (Exception ignored) {}
        }

        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .ifPresent(course::setCategory);
        }

        courseRepository.save(course);
        return mapToResponse(course);
    }

    public CourseResponse publishCourse(Long id, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = course.getFormateur().getEmail().equals(email);
        if (!isAdmin && !isOwner) throw new RuntimeException("Non autorisé");
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
        return mapToResponse(course);
    }

    public CourseResponse publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
        return mapToResponse(course);
    }

    public void deleteCourse(Long id, String email) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = course.getFormateur().getEmail().equals(email);
        if (!isAdmin && !isOwner) throw new RuntimeException("Non autorisé");
        courseRepository.deleteById(id);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .status(course.getStatus().name())
                .niveau(course.getNiveau() != null ? course.getNiveau().name() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .formateurName(course.getFormateur().getFirstName() + " " + course.getFormateur().getLastName())
                .modulesCount(course.getModules() != null ? course.getModules().size() : 0)
                .build();
    }

    private CourseResponse mapToDetailResponse(Course course) {
        List<ModuleResponse> modules = new ArrayList<>();
        if (course.getModules() != null) {
            for (CourseModule m : course.getModules()) {
                List<ChapterResponse> chapters = new ArrayList<>();
                if (m.getChapters() != null) {
                    for (Chapter c : m.getChapters()) {
                        List<QuestionResponse> questions = new ArrayList<>();
                        if (c.getQuestions() != null) {
                            for (ChapterQuestion q : c.getQuestions()) {
                                questions.add(QuestionResponse.builder()
                                        .id(q.getId())
                                        .content(q.getContent())
                                        .optionA(q.getOptionA())
                                        .optionB(q.getOptionB())
                                        .optionC(q.getOptionC())
                                        .optionD(q.getOptionD())
                                        .orderIndex(q.getOrderIndex())
                                        .build());
                            }
                        }
                        chapters.add(ChapterResponse.builder()
                                .id(c.getId())
                                .title(c.getTitle())
                                .description(c.getDescription())
                                .videoUrl(c.getVideoUrl())
                                .videoDuration(c.getVideoDuration())
                                .orderIndex(c.getOrderIndex())
                                .questions(questions)
                                .build());
                    }
                }
                modules.add(ModuleResponse.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .description(m.getDescription())
                        .orderIndex(m.getOrderIndex())
                        .chapters(chapters)
                        .build());
            }
        }

        Long quizId = finalQuizRepository.findByCourseId(course.getId())
                .map(FinalQuiz::getId).orElse(null);

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .status(course.getStatus().name())
                .niveau(course.getNiveau() != null ? course.getNiveau().name() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .formateurName(course.getFormateur().getFirstName() + " " + course.getFormateur().getLastName())
                .modulesCount(modules.size())
                .quizId(quizId)
                .modules(modules)
                .build();
    }
}