package com.lionking.ddingchun.bookmark.controller;

import com.lionking.ddingchun.bookmark.dto.BookmarkActionResponse;
import com.lionking.ddingchun.bookmark.dto.BookmarkStatusResponse;
import com.lionking.ddingchun.bookmark.dto.MyBookmarkListResponse;
import com.lionking.ddingchun.bookmark.entity.Bookmark;
import com.lionking.ddingchun.bookmark.entity.BookmarkFilterType;
import com.lionking.ddingchun.bookmark.entity.BookmarkSortType;
import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;
import com.lionking.ddingchun.bookmark.service.BookmarkService;
import com.lionking.ddingchun.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@Tag(
        name = "Bookmark",
        description = "게시글 및 공지 찜 API"
)
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 마이페이지 찜 목록 조회
     *
     * GET /api/bookmarks?userId=1&type=ALL&sort=DEADLINE
     */
    @Operation(summary = "마이페이지 찜 목록 조회")
    @GetMapping
    public ApiResponse<MyBookmarkListResponse> getMyBookmarks(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "ALL")
            BookmarkFilterType type,
            @RequestParam(defaultValue = "DEADLINE")
            BookmarkSortType sort
    ) {
        MyBookmarkListResponse response =
                bookmarkService.getMyBookmarkList(
                        userId,
                        type,
                        sort
                );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "찜 목록을 조회했습니다.",
                response
        );
    }

    /**
     * 모집글 찜 추가
     *
     * POST /api/bookmarks/posts/1?userId=1
     */
    @Operation(summary = "모집글 찜 추가")
    @PostMapping("/posts/{postId}")
    public ApiResponse<BookmarkActionResponse> addPostBookmark(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        Bookmark bookmark =
                bookmarkService.addBookmark(
                        userId,
                        BookmarkTargetType.POST,
                        postId
                );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "모집글을 찜했습니다.",
                BookmarkActionResponse.from(bookmark)
        );
    }

    /**
     * 모집글 찜 취소
     *
     * DELETE /api/bookmarks/posts/1?userId=1
     */
    @Operation(summary = "모집글 찜 취소")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<BookmarkActionResponse> removePostBookmark(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        bookmarkService.removeBookmark(
                userId,
                BookmarkTargetType.POST,
                postId
        );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "모집글 찜을 취소했습니다.",
                BookmarkActionResponse.removed(
                        userId,
                        BookmarkTargetType.POST,
                        postId
                )
        );
    }

    /**
     * 모집글 찜 여부 확인
     *
     * GET /api/bookmarks/posts/1/status?userId=1
     */
    @Operation(summary = "모집글 찜 여부 확인")
    @GetMapping("/posts/{postId}/status")
    public ApiResponse<BookmarkStatusResponse>
    getPostBookmarkStatus(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        boolean bookmarked =
                bookmarkService.isBookmarked(
                        userId,
                        BookmarkTargetType.POST,
                        postId
                );

        BookmarkStatusResponse response =
                new BookmarkStatusResponse(
                        userId,
                        BookmarkTargetType.POST,
                        postId,
                        bookmarked
                );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "모집글 찜 여부를 조회했습니다.",
                response
        );
    }

    /**
     * 학교 공지 찜 추가
     *
     * POST /api/bookmarks/notices/1?userId=1
     */
    @Operation(summary = "학교 공지 찜 추가")
    @PostMapping("/notices/{noticeId}")
    public ApiResponse<BookmarkActionResponse> addNoticeBookmark(
            @PathVariable Long noticeId,
            @RequestParam Long userId
    ) {
        Bookmark bookmark =
                bookmarkService.addBookmark(
                        userId,
                        BookmarkTargetType.NOTICE,
                        noticeId
                );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "학교 공지를 찜했습니다.",
                BookmarkActionResponse.from(bookmark)
        );
    }

    /**
     * 학교 공지 찜 취소
     *
     * DELETE /api/bookmarks/notices/1?userId=1
     */
    @Operation(summary = "학교 공지 찜 취소")
    @DeleteMapping("/notices/{noticeId}")
    public ApiResponse<BookmarkActionResponse>
    removeNoticeBookmark(
            @PathVariable Long noticeId,
            @RequestParam Long userId
    ) {
        bookmarkService.removeBookmark(
                userId,
                BookmarkTargetType.NOTICE,
                noticeId
        );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "학교 공지 찜을 취소했습니다.",
                BookmarkActionResponse.removed(
                        userId,
                        BookmarkTargetType.NOTICE,
                        noticeId
                )
        );
    }

    /**
     * 학교 공지 찜 여부 확인
     *
     * GET /api/bookmarks/notices/1/status?userId=1
     */
    @Operation(summary = "학교 공지 찜 여부 확인")
    @GetMapping("/notices/{noticeId}/status")
    public ApiResponse<BookmarkStatusResponse>
    getNoticeBookmarkStatus(
            @PathVariable Long noticeId,
            @RequestParam Long userId
    ) {
        boolean bookmarked =
                bookmarkService.isBookmarked(
                        userId,
                        BookmarkTargetType.NOTICE,
                        noticeId
                );

        BookmarkStatusResponse response =
                new BookmarkStatusResponse(
                        userId,
                        BookmarkTargetType.NOTICE,
                        noticeId,
                        bookmarked
                );

        return new ApiResponse<>(
                true,
                "BOOKMARK200",
                "학교 공지 찜 여부를 조회했습니다.",
                response
        );
    }
}