package com.lionking.ddingchun.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Campus campus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private College college;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String studentId;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag", nullable = false)
    private List<InterestTag> tags = new ArrayList<>();

    @Builder
    public User(String email, Course course, Campus campus, College college, String department, String studentId, List<InterestTag> tags) {
        this.email = email;
        this.course = course;
        this.campus = campus;
        this.college = college;
        this.department = department;
        this.studentId = studentId;
        this.tags = tags;
    }

    public void updateTags(List<InterestTag> tags) {
        this.tags = tags;
    }
}