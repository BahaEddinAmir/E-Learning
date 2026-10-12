package com.arabsoft.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import com.arabsoft.elearning.entity.CourseModule;

@Entity
@Table(name = "video")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    private int duration;

    @Column(name = "order_index")
    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;
}