package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import com.arabsoft.elearning.entity.CourseModule;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float percentage;

    @Column(name = "last_position")
    private int lastPosition;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;
}