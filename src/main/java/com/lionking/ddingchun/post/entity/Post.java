package com.lionking.ddingchun.post.entity;

import com.lionking.ddingchun.user.entity.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 게시글 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /*
     * 프로젝트, 학교 행사, 공모전 등의 게시글 분류
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PostCategory category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /*
     * 실제 모임 또는 행사 일시
     */
    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false, length = 255)
    private String place;

    /*
     * 인문캠, 자연캠, 캠퍼스 무관
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PostCampus campus;

    /*
     * 작성자를 포함한 현재 참여 인원
     */
    @Column(nullable = false)
    private int currentCount;

    @Column(nullable = false)
    private int maxCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    /*
     * 게시글 검색에 사용하는 태그
     */
    @ElementCollection
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "tag", nullable = false, length = 50)
    private List<String> tags = new ArrayList<>();

    /*
     * 학교 공지에서 파생된 모집글일 경우 원본 공지 ID
     */
    @Column(name = "source_notice_id")
    private Long sourceNoticeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Post(
            User author,
            PostCategory category,
            String title,
            String description,
            LocalDateTime eventDate,
            String place,
            PostCampus campus,
            int maxCount,
            List<String> tags,
            Long sourceNoticeId
    ) {
        validateMaxCount(maxCount);

        this.author = author;
        this.category = category;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.place = place;
        this.campus = campus;
        this.currentCount = 1;
        this.maxCount = maxCount;
        this.status = PostStatus.RECRUITING;
        this.tags = tags == null
                ? new ArrayList<>()
                : new ArrayList<>(tags);
        this.sourceNoticeId = sourceNoticeId;
    }

    /*
     * 신청자를 수락했을 때 현재 인원을 한 명 증가시킨다.
     */
    public void acceptApplicant() {
        if (status == PostStatus.CLOSED) {
            throw new IllegalStateException("이미 마감된 모집글입니다.");
        }

        if (currentCount >= maxCount) {
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }

        currentCount++;

        if (currentCount >= maxCount) {
            status = PostStatus.CLOSED;
        }
    }

    /*
     * 모집글을 직접 마감한다.
     */
    public void close() {
        this.status = PostStatus.CLOSED;
    }

    /*
     * 제목과 설명 수정
     */
    public void updateContent(
            String title,
            String description
    ) {
        this.title = title;
        this.description = description;
    }

    /*
     * 모임 정보 수정
     */
    public void updateEventInformation(
            LocalDateTime eventDate,
            String place,
            PostCampus campus
    ) {
        this.eventDate = eventDate;
        this.place = place;
        this.campus = campus;
    }

    /*
     * 태그 수정
     */
    public void updateTags(List<String> tags) {
        this.tags = tags == null
                ? new ArrayList<>()
                : new ArrayList<>(tags);
    }

    /*
     * 모집 인원 수정
     */
    public void updateMaxCount(int maxCount) {
        validateMaxCount(maxCount);

        if (maxCount < currentCount) {
            throw new IllegalArgumentException(
                    "최대 모집 인원은 현재 참여 인원보다 작을 수 없습니다."
            );
        }

        this.maxCount = maxCount;

        if (currentCount >= maxCount) {
            this.status = PostStatus.CLOSED;
        } else {
            this.status = PostStatus.RECRUITING;
        }
    }

    private void validateMaxCount(int maxCount) {
        if (maxCount < 2) {
            throw new IllegalArgumentException(
                    "모집 인원은 작성자를 포함하여 2명 이상이어야 합니다."
            );
        }
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}