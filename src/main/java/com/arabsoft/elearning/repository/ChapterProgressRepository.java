package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {
    Optional<ChapterProgress> findByStudentIdAndChapterId(Long studentId, Long chapterId);
    List<ChapterProgress> findByStudentId(Long studentId);
    long countByStudentIdAndIsCompletedTrue(Long studentId);
    long countByStudentIdAndChapterIdInAndIsCompletedTrue(Long studentId, List<Long> chapterIds);
    List<ChapterProgress> findByStudentIdAndChapterIdIn(Long studentId, List<Long> chapterIds);
}