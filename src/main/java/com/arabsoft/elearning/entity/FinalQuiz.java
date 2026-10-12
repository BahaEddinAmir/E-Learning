package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "final_quiz")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "passing_score")
    @Builder.Default
    private int passingScore = 70;

    @Column(name = "max_attempts")
    @Builder.Default
    private int maxAttempts = 3;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "finalQuiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<FinalQuizQuestion> questions;
}