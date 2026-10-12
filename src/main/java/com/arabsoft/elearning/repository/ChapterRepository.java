package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByModuleIdOrderByOrderIndex(Long moduleId);

    @Query("SELECT COUNT(c) FROM Chapter c WHERE c.module.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT c.id FROM Chapter c WHERE c.module.course.id = :courseId")
    List<Long> findIdsByCourseId(@Param("courseId") Long courseId);
}