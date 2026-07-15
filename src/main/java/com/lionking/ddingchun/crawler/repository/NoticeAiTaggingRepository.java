package com.lionking.ddingchun.crawler.repository;

import com.lionking.ddingchun.crawler.domain.NoticeAiTagging;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeAiTaggingRepository
        extends JpaRepository<NoticeAiTagging, Long> {

    boolean existsByNotice_Id(Long noticeId);

    Optional<NoticeAiTagging> findByNotice_Id(Long noticeId);
}