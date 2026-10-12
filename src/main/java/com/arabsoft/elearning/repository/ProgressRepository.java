package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByStudentId(Long studentId);
    Optional<Progress> findByStudentIdAndModuleId(Long studentId, Long moduleId);
    List<Progress> findByStudentIdAndModule_Course_Id(Long studentId, Long courseId);
}