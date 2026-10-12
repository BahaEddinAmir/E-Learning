package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.Course;
import com.arabsoft.elearning.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByFormateurId(Long formateurId);
    List<Course> findByCategoryId(Long categoryId);
}