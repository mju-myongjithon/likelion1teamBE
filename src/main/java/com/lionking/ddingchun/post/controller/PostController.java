package com.lionking.ddingchun.post.controller;

import com.lionking.ddingchun.global.response.ApiResponse;
import com.lionking.ddingchun.post.dto.PostDetailResponse;
import com.lionking.ddingchun.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {

    private final PostService postService;

    @Operation(summary = "모집글 상세 조회")
    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(
            @PathVariable Long postId
    ) {
        PostDetailResponse response =
                postService.getPostDetail(postId);

        return new ApiResponse<>(
                true,
                "COMMON200",
                "모집글 상세 조회에 성공했습니다.",
                response
        );
    }
}