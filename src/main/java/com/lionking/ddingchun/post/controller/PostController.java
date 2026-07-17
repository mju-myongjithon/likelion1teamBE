package com.lionking.ddingchun.post.controller;

import com.lionking.ddingchun.global.response.ApiResponse;
import com.lionking.ddingchun.post.dto.PostCreateRequest;
import com.lionking.ddingchun.post.dto.PostCreateResponse;
import com.lionking.ddingchun.post.dto.PostDetailResponse;
import com.lionking.ddingchun.post.dto.PostListResponse;
import com.lionking.ddingchun.post.dto.PostUpdateRequest;
import com.lionking.ddingchun.post.entity.PostCampus;
import com.lionking.ddingchun.post.entity.PostCategory;
import com.lionking.ddingchun.post.entity.PostSortType;
import com.lionking.ddingchun.post.entity.PostStatus;
import com.lionking.ddingchun.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {

    private final PostService postService;

    @Operation(summary = "모집글 작성")
    @PostMapping
    public ApiResponse<PostCreateResponse> createPost(
            @RequestBody PostCreateRequest request
    ) {
        PostCreateResponse response =
                postService.createPost(request);

        return new ApiResponse<>(
                true,
                "POST201",
                "모집글 작성에 성공했습니다.",
                response
        );
    }

    @Operation(summary = "모집글 목록 조회")
    @GetMapping
    public ApiResponse<PostListResponse> getPostList(

            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            PostCategory category,

            @RequestParam(required = false)
            PostCampus campus,

            @RequestParam(required = false)
            PostStatus status,

            @RequestParam(defaultValue = "LATEST")
            PostSortType sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        PostListResponse response =
                postService.getPostList(
                        userId,
                        category,
                        campus,
                        status,
                        sort,
                        page,
                        size
                );

        return new ApiResponse<>(
                true,
                "COMMON200",
                "모집글 목록 조회에 성공했습니다.",
                response
        );
    }

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

    /**
     * 게시글 수정
     *
     * PATCH /api/posts/{postId}
     */
    @Operation(summary = "모집글 수정")
    @PatchMapping("/{postId}")
    public ApiResponse<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        PostDetailResponse response =
                postService.updatePost(
                        postId,
                        request
                );

        return new ApiResponse<>(
                true,
                "COMMON200",
                "모집글 수정에 성공했습니다.",
                response
        );
    }
    /**
 * 모집글 직접 마감
 *
 * PATCH /api/posts/{postId}/close?authorId=1
 */
@Operation(summary = "모집글 직접 마감")
@PatchMapping("/{postId}/close")
public ApiResponse<PostDetailResponse> closePost(
        @PathVariable Long postId,
        @RequestParam Long authorId
) {
    PostDetailResponse response =
            postService.closePost(
                    postId,
                    authorId
            );

    return new ApiResponse<>(
            true,
            "COMMON200",
            "모집글을 마감했습니다.",
            response
    );
}

/**
 * 모집글 삭제
 *
 * DELETE /api/posts/{postId}?authorId=1
 */
@Operation(summary = "모집글 삭제")
@DeleteMapping("/{postId}")
public ApiResponse<Void> deletePost(
        @PathVariable Long postId,
        @RequestParam Long authorId
) {
    postService.deletePost(
            postId,
            authorId
    );

    return new ApiResponse<>(
            true,
            "COMMON200",
            "모집글을 삭제했습니다.",
            null
    );
}
}