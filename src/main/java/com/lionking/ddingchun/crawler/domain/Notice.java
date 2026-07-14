package com.lionking.ddingchun.crawler.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_notice",
        uniqueConstraints = @UniqueConstraint(columnNames = "link"))
@Getter
@Setter
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000, unique = true)
    private String link;

    @Column(length = 100)
    private String author;

    private LocalDateTime publishedAt;

    @Column(length = 500)
    private String summary;

    // ---------------------------------------------------------
    // 아래 4개는 "캘린더 화면"을 위해 새로 추가한 필드입니다.
    // 크롤링 시점에 SiteConfig에 정해둔 기본값으로 채워집니다.
    // ---------------------------------------------------------

    /** 공지/행사/공모전/장학금/동아리/축제/전시 중 하나 */
    @Column(length = 20)
    private String category;

    /** 전체 / 인문캠퍼스 / 자연캠퍼스 */
    @Column(length = 20)
    private String campus;

    /** 장소 (크롤링 데이터는 보통 비어있고, 모집글에서 주로 채워짐) */
    @Column(length = 200)
    private String place;

    /** 모집중 / 마감 등 (크롤링 데이터는 보통 비어있음) */
    @Column(length = 20)
    private String status;

    /** 우리 서버가 이 글을 수집한 시각 (디버깅/모니터링용) */
    @Column(nullable = false)
    private LocalDateTime crawledAt;

    @PrePersist
    protected void onCreate() {
        this.crawledAt = LocalDateTime.now();
    }

    public Notice(String source, String title, String link, String author,
                  LocalDateTime publishedAt, String summary,
                  String category, String campus) {
        this.source = source;
        this.title = title;
        this.link = link;
        this.author = author;
        this.publishedAt = publishedAt;
        this.summary = summary;
        this.category = category;
        this.campus = campus;
    }
}
