package com.lionking.ddingchun.crawler.scheduler;

import com.lionking.ddingchun.crawler.service.NoticeCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeCrawlScheduler {

    private final NoticeCrawlerService noticeCrawlerService;

    // 초 분 시 일 월 요일  -> 매 3시간마다 자동 크롤링
    @Scheduled(cron = "0 0/1800 * * * *")
    public void crawlPeriodically() {
        log.info("정기 크롤링 시작");
        Map<String, Integer> result = noticeCrawlerService.crawlAll();
        log.info("정기 크롤링 완료: {}", result);
    }
}