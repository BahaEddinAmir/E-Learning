package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "quiz_passed")
    @Builder.Default
    private Boolean quizPassed = false;

    @Column(name = "quiz_score")
    @Builder.Default
    private int quizScore = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_position")
    @Builder.Default
    private int lastPosition = 0;
}