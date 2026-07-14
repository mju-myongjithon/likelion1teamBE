package com.lionking.ddingchun.search.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record SearchResponse(

        long totalCount,
        SearchPageResponse results

) {

    public static SearchResponse from(
            Page<SearchResultItem> resultPage
    ) {
        return new SearchResponse(
                resultPage.getTotalElements(),
                SearchPageResponse.from(resultPage)
        );
    }

    public record SearchPageResponse(

            List<SearchResultItem> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext

    ) {

        public static SearchPageResponse from(
                Page<SearchResultItem> resultPage
        ) {
            return new SearchPageResponse(
                    List.copyOf(resultPage.getContent()),
                    resultPage.getNumber(),
                    resultPage.getSize(),
                    resultPage.getTotalElements(),
                    resultPage.getTotalPages(),
                    resultPage.hasNext()
            );
        }
    }
}