package com.lionking.ddingchun.bookmark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bookmark_user_target",
                        columnNames = {
                                "user_id",
                                "target_type",
                                "target_id"
                        }
                )
        }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 찜한 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /*
     * POST: 모집글
     * NOTICE: 학교 공지
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private BookmarkTargetType targetType;

    /*
     * POST 또는 NOTICE의 실제 ID
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Bookmark(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    ) {
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}