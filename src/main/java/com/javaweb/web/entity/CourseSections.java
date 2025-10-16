package com.javaweb.web.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "course_sections")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CourseSections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "course_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Courses course;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "section_order", nullable = false)
    private int sectionOrder;
    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

}
