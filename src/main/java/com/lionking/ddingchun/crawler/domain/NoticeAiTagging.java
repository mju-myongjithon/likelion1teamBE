package com.lionking.ddingchun.crawler.domain;

import com.lionking.ddingchun.crawler.domain.Notice;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notice_ai_tagging")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeAiTagging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 크롤링 공지 하나당 AI 태깅 결과 하나만 저장
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "notice_id",
            nullable = false,
            unique = true
    )
    private Notice notice;

    /*
     * AI가 판단한 카테고리
     */
    @Column(length = 30)
    private String aiCategory;

    /*
     * AI가 생성한 관심 분야 태그
     */
    @ElementCollection
    @CollectionTable(
            name = "notice_ai_tags",
            joinColumns = @JoinColumn(name = "tagging_id")
    )
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaggingStatus status;

    private LocalDateTime taggedAt;

    @Column(length = 500)
    private String errorMessage;

    private LocalDateTime createdAt;

    public NoticeAiTagging(Notice notice) {
        this.notice = notice;
        this.status = TaggingStatus.PENDING;
        this.tags = new ArrayList<>();
    }

    public void complete(
            String aiCategory,
            List<String> tags
    ) {
        this.aiCategory = aiCategory;

        this.tags.clear();

        if (tags != null) {
            this.tags.addAll(tags);
        }

        this.status = TaggingStatus.COMPLETED;
        this.taggedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void fail(String errorMessage) {
        this.status = TaggingStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = TaggingStatus.PENDING;
        }
    }
}