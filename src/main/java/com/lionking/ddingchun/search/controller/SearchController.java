package com.lionking.ddingchun.search.controller;

import com.lionking.ddingchun.global.response.ApiResponse;
import com.lionking.ddingchun.search.dto.SearchResponse;
import com.lionking.ddingchun.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "모집글 및 학교 공지 통합 검색")
    @GetMapping
    public ApiResponse<SearchResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchResponse response =
                searchService.search(
                        keyword,
                        page,
                        size
                );

        return new ApiResponse<>(
                true,
                "COMMON200",
                "검색에 성공했습니다.",
                response
        );
    }
}