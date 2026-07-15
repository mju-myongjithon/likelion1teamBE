package com.lionking.ddingchun.crawler.repository;

import com.lionking.ddingchun.crawler.domain.NoticeAiTagging;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.lionking.ddingchun.crawler.domain.TaggingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeAiTaggingRepository
        extends JpaRepository<NoticeAiTagging, Long> {

    boolean existsByNotice_Id(Long noticeId);

    Optional<NoticeAiTagging> findByNotice_Id(Long noticeId);
    @EntityGraph(attributePaths = {"notice", "tags"})
@Query("""
        select distinct tagging
        from NoticeAiTagging tagging
        join tagging.notice notice
        left join tagging.tags tag
        where tagging.status = :status
          and (
                lower(notice.title)
                    like lower(concat('%', :keyword, '%'))
             or lower(coalesce(notice.summary, ''))
                    like lower(concat('%', :keyword, '%'))
             or lower(coalesce(tagging.aiCategory, ''))
                    like lower(concat('%', :keyword, '%'))
             or lower(tag)
                    like lower(concat('%', :keyword, '%'))
          )
        order by tagging.taggedAt desc
        """)
List<NoticeAiTagging> searchByKeyword(
        @Param("keyword") String keyword,
        @Param("status") TaggingStatus status
);
}