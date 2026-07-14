package com.lionking.ddingchun.search.service;

import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.search.dto.SearchResponse;
import com.lionking.ddingchun.search.dto.SearchResultItem;
import com.lionking.ddingchun.search.exception.InvalidSearchConditionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;

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

        Page<Post> postPage =
                postRepository.searchByKeyword(
                        normalizedKeyword,
                        pageable
                );

        Page<SearchResultItem> resultPage =
                postPage.map(SearchResultItem::fromPost);

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