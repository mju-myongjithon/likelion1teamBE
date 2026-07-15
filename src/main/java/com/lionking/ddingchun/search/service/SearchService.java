package com.lionking.ddingchun.search.service;

import com.lionking.ddingchun.crawler.domain.TaggingStatus;
import com.lionking.ddingchun.crawler.repository.NoticeAiTaggingRepository;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.search.dto.SearchResponse;
import com.lionking.ddingchun.search.dto.SearchResultItem;
import com.lionking.ddingchun.search.exception.InvalidSearchConditionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;
    private final NoticeAiTaggingRepository noticeAiTaggingRepository;

    @Transactional(readOnly = true)
    public SearchResponse search(
            String keyword,
            int page,
            int size
    ) {
        validatePageCondition(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        if (keyword == null || keyword.isBlank()) {
            Page<SearchResultItem> emptyPage =
                    Page.empty(pageable);

            return SearchResponse.from(emptyPage);
        }

        String normalizedKeyword = keyword.trim();

        /*
         * 모집글 검색
         */
        Page<Post> postPage =
                postRepository.searchByKeyword(
                        normalizedKeyword,
                        Pageable.unpaged()
                );

        List<SearchResultItem> combinedResults =
                new ArrayList<>();

        combinedResults.addAll(
                postPage.getContent()
                        .stream()
                        .map(SearchResultItem::fromPost)
                        .toList()
        );

        /*
         * Gemini 태깅이 완료된 학교 공지 검색
         */
        combinedResults.addAll(
                noticeAiTaggingRepository
                        .searchByKeyword(
                                normalizedKeyword,
                                TaggingStatus.COMPLETED
                        )
                        .stream()
                        .map(
                                SearchResultItem
                                        ::fromNoticeAiTagging
                        )
                        .toList()
        );

        /*
         * 모집글과 공지를 합친 후 통합 페이지 처리
         */
        long requestedStart =
                (long) page * size;

        int start =
                (int) Math.min(
                        requestedStart,
                        combinedResults.size()
                );

        int end =
                Math.min(
                        start + size,
                        combinedResults.size()
                );

        List<SearchResultItem> pageContent =
                combinedResults.subList(
                        start,
                        end
                );

        Page<SearchResultItem> resultPage =
                new PageImpl<>(
                        pageContent,
                        pageable,
                        combinedResults.size()
                );

        return SearchResponse.from(resultPage);
    }

    private void validatePageCondition(
            int page,
            int size
    ) {
        if (page < 0) {
            throw new InvalidSearchConditionException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidSearchConditionException(
                    "페이지 크기는 1 이상 100 이하여야 합니다."
            );
        }
    }
}