package com.lionking.ddingchun.crawler.service;

import com.lionking.ddingchun.crawler.domain.Notice;
import com.lionking.ddingchun.crawler.domain.NoticeAiTagging;
import com.lionking.ddingchun.crawler.domain.TaggingStatus;
import com.lionking.ddingchun.crawler.repository.NoticeAiTaggingRepository;
import com.lionking.ddingchun.crawler.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoticeAiTaggingService {

    /*
     * 한 번의 Gemini 요청에서 처리할 최대 공지 수
     */
    private static final int BATCH_SIZE = 20;

    /*
     * DB를 한 번에 조회할 공지 수
     */
    private static final int PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_CATEGORIES =
            Set.of(
                    "NOTICE",
                    "EVENT",
                    "CONTEST",
                    "SCHOLARSHIP",
                    "CLUB",
                    "FESTIVAL",
                    "EXHIBITION",
                    "CAREER"
            );

    private static final Map<String, String>
            DEFAULT_CATEGORY_TAGS =
            Map.of(
                    "NOTICE", "공지",
                    "EVENT", "행사",
                    "CONTEST", "공모전",
                    "SCHOLARSHIP", "장학금",
                    "CLUB", "동아리",
                    "FESTIVAL", "축제",
                    "EXHIBITION", "전시·공연",
                    "CAREER", "취업·진로"
            );

    private final NoticeRepository noticeRepository;
    private final NoticeAiTaggingRepository taggingRepository;
    private final GeminiTaggingClient geminiTaggingClient;

    /**
     * 아직 Gemini 태깅이 완료되지 않은 공지를 찾아
     * 한 번에 최대 20개씩 처리한다.
     */
    public int tagLatestNotices() {

        List<Notice> targets =
                findTaggingTargets();

        if (targets.isEmpty()) {
            return 0;
        }

        Map<Long, GeminiTaggingClient.TaggingResult>
                results;

        try {
            results =
                    geminiTaggingClient.tagNotices(
                            targets
                    );

        } catch (RuntimeException exception) {
            markAllFailed(
                    targets,
                    exception.getMessage()
            );

            throw exception;
        }

        int completedCount = 0;

        for (Notice notice : targets) {
            NoticeAiTagging tagging =
                    findOrCreateTagging(notice);

            try {
                GeminiTaggingClient.TaggingResult result =
                        results.get(notice.getId());

                if (result == null) {
                    throw new IllegalStateException(
                            "Gemini 응답에 해당 공지 결과가 없습니다."
                    );
                }

                String category =
                        normalizeCategory(
                                result.category()
                        );

                List<String> tags =
                        normalizeTags(
                                result.tags(),
                                category
                        );

                tagging.complete(
                        category,
                        tags
                );

                taggingRepository.save(tagging);
                completedCount++;

            } catch (RuntimeException exception) {
                tagging.fail(
                        safeErrorMessage(
                                exception.getMessage()
                        )
                );

                taggingRepository.save(tagging);
            }
        }

        return completedCount;
    }

    /**
     * 최신순으로 조회하면서
     * COMPLETED가 아닌 공지를 최대 20개 찾는다.
     */
    private List<Notice> findTaggingTargets() {

        List<Notice> targets =
                new ArrayList<>();

        int pageNumber = 0;

        Sort sort = Sort.by(
                Sort.Order.desc("publishedAt"),
                Sort.Order.desc("id")
        );

        while (targets.size() < BATCH_SIZE) {

            Page<Notice> noticePage =
                    noticeRepository.findAll(
                            PageRequest.of(
                                    pageNumber,
                                    PAGE_SIZE,
                                    sort
                            )
                    );

            for (Notice notice
                    : noticePage.getContent()) {

                if (notice.getId() == null) {
                    continue;
                }

                boolean completed =
                        taggingRepository
                                .findByNotice_Id(
                                        notice.getId()
                                )
                                .map(tagging ->
                                        tagging.getStatus()
                                                == TaggingStatus.COMPLETED
                                )
                                .orElse(false);

                if (!completed) {
                    targets.add(notice);
                }

                if (targets.size()
                        >= BATCH_SIZE) {
                    break;
                }
            }

            if (!noticePage.hasNext()) {
                break;
            }

            pageNumber++;
        }

        return targets;
    }

    private NoticeAiTagging findOrCreateTagging(
            Notice notice
    ) {
        return taggingRepository
                .findByNotice_Id(
                        notice.getId()
                )
                .orElseGet(() ->
                        new NoticeAiTagging(notice)
                );
    }

    private String normalizeCategory(
            String category
    ) {
        if (category == null
                || category.isBlank()) {
            throw new IllegalArgumentException(
                    "카테고리가 비어 있습니다."
            );
        }

        String normalized =
                category.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        if (!ALLOWED_CATEGORIES
                .contains(normalized)) {
            throw new IllegalArgumentException(
                    "허용되지 않은 카테고리입니다: "
                            + normalized
            );
        }

        return normalized;
    }

    private List<String> normalizeTags(
            List<String> tags,
            String category
    ) {
        LinkedHashSet<String> normalized =
                new LinkedHashSet<>();

        if (tags != null) {
            for (String tag : tags) {

                if (tag == null
                        || tag.isBlank()) {
                    continue;
                }

                String trimmed = tag.trim();

                if (trimmed.length() > 50) {
                    trimmed =
                            trimmed.substring(0, 50);
                }

                normalized.add(trimmed);

                if (normalized.size() >= 5) {
                    break;
                }
            }
        }

        if (normalized.isEmpty()) {
            normalized.add(
                    DEFAULT_CATEGORY_TAGS.get(
                            category
                    )
            );
        }

        return List.copyOf(normalized);
    }

    private void markAllFailed(
            List<Notice> notices,
            String errorMessage
    ) {
        String safeMessage =
                safeErrorMessage(errorMessage);

        for (Notice notice : notices) {

            NoticeAiTagging tagging =
                    findOrCreateTagging(notice);

            tagging.fail(safeMessage);
            taggingRepository.save(tagging);
        }
    }

    private String safeErrorMessage(
            String message
    ) {
        String safeMessage =
                message == null
                        || message.isBlank()
                        ? "Gemini 태깅 처리 중 오류가 발생했습니다."
                        : message;

        if (safeMessage.length() > 500) {
            return safeMessage.substring(0, 500);
        }

        return safeMessage;
    }
}