package com.lionking.ddingchun.crawler.repository;

import com.lionking.ddingchun.crawler.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    boolean existsByLink(String link);

    List<Notice> findTop50ByOrderByPublishedAtDesc();

    List<Notice> findBySourceOrderByPublishedAtDesc(String source);
}
