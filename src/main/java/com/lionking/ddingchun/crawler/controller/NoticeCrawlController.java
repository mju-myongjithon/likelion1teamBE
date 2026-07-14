package com.lionking.ddingchun.crawler.controller;

import com.lionking.ddingchun.crawler.domain.Notice;
import com.lionking.ddingchun.crawler.repository.NoticeRepository;
import com.lionking.ddingchun.crawler.service.NoticeCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 크롤링을 테스트해볼 수 있는 임시 API.
 *
 *  POST /admin/crawl   -> 지금 바로 전체 사이트 크롤링 실행
 *  GET  /admin/notices  -> 저장된 공지 최신 50개 조회
 *
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class NoticeCrawlController {

    private final NoticeCrawlerService noticeCrawlerService;
    private final NoticeRepository noticeRepository;

    @PostMapping("/crawl")
    public Map<String, Integer> crawlNow() {
        return noticeCrawlerService.crawlAll();
    }

    @GetMapping("/notices")
    public List<Notice> listNotices() {
        return noticeRepository.findTop50ByOrderByPublishedAtDesc();
    }
}