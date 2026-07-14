package com.lionking.ddingchun.crawler.service;

import com.lionking.ddingchun.crawler.config.CrawlTargets;
import com.lionking.ddingchun.crawler.config.SiteConfig;
import com.lionking.ddingchun.crawler.domain.Notice;
import com.lionking.ddingchun.crawler.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeCrawlerService {

    private final NoticeRepository noticeRepository;

    private static final Pattern DATE_IN_TEXT = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");

    public Map<String, Integer> crawlAll() {
        Map<String, Integer> savedCountBySource = new LinkedHashMap<>();

        for (SiteConfig site : CrawlTargets.SITES) {
            List<Notice> notices;
            try {
                notices = site.useRss() ? fetchRss(site, 30) : fetchHtmlList(site);
            } catch (Exception e) {
                log.warn("크롤링 실패: {} ({})", site.sourceName(), e.getMessage());
                savedCountBySource.put(site.sourceName(), -1); // -1 = 실패 표시
                continue;
            }

            int savedCount = 0;
            for (Notice notice : notices) {
                if (!noticeRepository.existsByLink(notice.getLink())) {
                    noticeRepository.save(notice);
                    savedCount++;
                }
            }
            savedCountBySource.put(site.sourceName(), savedCount);

            sleepQuietly(800);
        }
        return savedCountBySource;
    }

    // RSS 크롤링

    public List<Notice> fetchRss(SiteConfig site, int row) throws IOException {
        String rssUrl = site.rssUrl(row);

        Document doc = Jsoup.connect(rssUrl)
                .userAgent("Mozilla/5.0 (compatible; MjuNoticeCrawler/1.0)")
                .timeout(10_000)
                .parser(Parser.xmlParser())
                .get();

        Elements items = doc.select("item");
        List<Notice> result = new ArrayList<>();

        for (Element item : items) {
            String title = text(item, "title");
            String link = text(item, "link");
            String author = text(item, "author");
            String description = text(item, "description");
            String pubDateRaw = text(item, "pubDate");

            String fullLink = absolutize(site.baseUrl(), link);
            LocalDateTime publishedAt = parsePubDate(pubDateRaw);
            String summary = description.length() > 150
                    ? description.substring(0, 150) : description;

            result.add(new Notice(site.sourceName(), title, fullLink, author,
                    publishedAt, summary, site.category(), site.campus()));
        }
        return result;
    }

    private String text(Element parent, String tagName) {
        Element el = parent.selectFirst(tagName);
        return el == null ? "" : el.text().trim();
    }

    /**
     * RSS의 pubDate ("2026-07-10 15:26:32.0" 같은 형식)를
     * LocalDateTime으로 바꿔줍니다. 형식이 다르면 null을 반환합니다.
     */
    private LocalDateTime parsePubDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String trimmed = raw.trim();
        // 밀리초 부분(.0)이 있으면 제거하고 파싱
        String withoutMillis = trimmed.replaceAll("\\.\\d+$", "");
        try {
            return LocalDateTime.parse(withoutMillis,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.debug("날짜 파싱 실패: {}", raw);
            return null;
        }
    }

    // HTML 표 직접 파싱 (RSS 없는 게시판용 예비 방법)

    public List<Notice> fetchHtmlList(SiteConfig site) throws IOException {
        Document doc = Jsoup.connect(site.listUrl())
                .userAgent("Mozilla/5.0 (compatible; MjuNoticeCrawler/1.0)")
                .timeout(10_000)
                .get();

        Elements links = doc.select("a[href*=artclView.do]");

        Map<String, Notice> dedup = new LinkedHashMap<>();

        for (Element a : links) {
            String title = a.text().trim();
            if (title.isEmpty()) continue;

            String href = a.attr("abs:href");
            if (href.isEmpty()) {
                href = absolutize(site.baseUrl(), a.attr("href"));
            }

            Element row = a.closest("tr");
            LocalDateTime publishedAt = null;
            if (row != null) {
                Matcher m = DATE_IN_TEXT.matcher(row.text());
                if (m.find()) {
                    String dateStr = m.group(0).replace(".", "-");
                    publishedAt = LocalDateTime.parse(dateStr + "T00:00:00");
                }
            }

            dedup.put(href, new Notice(site.sourceName(), title, href, "",
                    publishedAt, "", site.category(), site.campus()));
        }
        return new ArrayList<>(dedup.values());
    }

    // 공통 유틸

    private String absolutize(String baseUrl, String maybeRelative) {
        if (maybeRelative == null || maybeRelative.isEmpty()) return "";
        if (maybeRelative.startsWith("http")) return maybeRelative;
        if (!maybeRelative.startsWith("/")) maybeRelative = "/" + maybeRelative;
        return baseUrl + maybeRelative;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}