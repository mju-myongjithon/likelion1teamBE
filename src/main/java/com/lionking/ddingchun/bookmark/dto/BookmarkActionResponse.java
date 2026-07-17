package com.lionking.ddingchun.bookmark.dto;

import com.lionking.ddingchun.bookmark.entity.Bookmark;
import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;

import java.time.LocalDateTime;

public record BookmarkActionResponse(

        Long bookmarkId,
        Long userId,
        BookmarkTargetType targetType,
        Long targetId,
        boolean bookmarked,
        LocalDateTime createdAt

) {

    public static BookmarkActionResponse from(Bookmark bookmark) {
        return new BookmarkActionResponse(
                bookmark.getId(),
                bookmark.getUserId(),
                bookmark.getTargetType(),
                bookmark.getTargetId(),
                true,
                bookmark.getCreatedAt()
        );
    }

    public static BookmarkActionResponse removed(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    ) {
        return new BookmarkActionResponse(
                null,
                userId,
                targetType,
                targetId,
                false,
                null
        );
    }
}