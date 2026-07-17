package com.lionking.ddingchun.bookmark.dto;

import java.util.List;

public record MyBookmarkListResponse(

        int totalCount,
        List<MyBookmarkItemResponse> bookmarks

) {

    public static MyBookmarkListResponse from(
            List<MyBookmarkItemResponse> bookmarks
    ) {
        List<MyBookmarkItemResponse> safeBookmarks =
                bookmarks == null
                        ? List.of()
                        : List.copyOf(bookmarks);

        return new MyBookmarkListResponse(
                safeBookmarks.size(),
                safeBookmarks
        );
    }
}