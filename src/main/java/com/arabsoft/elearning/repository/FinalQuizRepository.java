package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.FinalQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FinalQuizRepository extends JpaRepository<FinalQuiz, Long> {
    Optional<FinalQuiz> findByCourseId(Long courseId);
}