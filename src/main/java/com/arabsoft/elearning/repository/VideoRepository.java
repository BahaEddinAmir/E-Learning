package com.arabsoft.elearning.repository;

import com.arabsoft.elearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByModuleIdOrderByOrderIndex(Long moduleId);
}