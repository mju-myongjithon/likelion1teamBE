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

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String campus;

    @Column(nullable = false)
    private String college;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String studentId;

    @ElementCollection
    @CollectionTable(name = "user_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "tag", nullable = false)
    private List<String> tags = new ArrayList<>();

    @Builder
    public User(String email, String course, String campus, String college, String department, String studentId, List<String> tags) {
        this.email = email;
        this.course = course;
        this.campus = campus;
        this.college = college;
        this.department = department;
        this.studentId = studentId;
        this.tags = tags;
    }
}
