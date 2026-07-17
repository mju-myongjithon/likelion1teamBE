package com.lionking.ddingchun.bookmark.dto;

import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;

public record BookmarkStatusResponse(

        Long userId,
        BookmarkTargetType targetType,
        Long targetId,
        boolean bookmarked

) {
}