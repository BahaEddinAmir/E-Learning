package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_number", unique = true, columnDefinition = "UUID")
    private UUID uniqueNumber;



    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "file_path")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @PrePersist
    public void prePersist() {
        issuedAt = LocalDateTime.now();
        if (uniqueNumber == null) {
            uniqueNumber = UUID.randomUUID();
        }
    }
}