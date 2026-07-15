package com.lionking.ddingchun.crawler.controller;

import com.lionking.ddingchun.crawler.service.NoticeAiTaggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/tagging")
@RequiredArgsConstructor
public class NoticeAiTaggingController {

    private final NoticeAiTaggingService taggingService;

    /**
     * 최근 크롤링 공지 중
     * 아직 태깅되지 않은 공지를 태깅한다.
     */
    @PostMapping("/notices")
    public Map<String, Integer> tagNotices() {

        int taggedCount =
                taggingService.tagLatestNotices();

        return Map.of(
                "taggedCount",
                taggedCount
        );
    }
}