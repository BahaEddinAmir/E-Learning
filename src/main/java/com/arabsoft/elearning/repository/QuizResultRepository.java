package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByStudentId(Long studentId);
    List<QuizResult> findByStudentIdAndQuizId(Long studentId, Long quizId);
    int countByStudentIdAndQuizId(Long studentId, Long quizId);
}