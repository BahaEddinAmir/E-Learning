package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;

    @Builder.Default
    private Boolean passed = false;

    @Column(name = "attempt_number")
    @Builder.Default
    private int attemptNumber = 1;

    @Column(name = "attempt_date")
    private LocalDateTime attemptDate;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @PrePersist
    public void prePersist() { attemptDate = LocalDateTime.now(); }
}