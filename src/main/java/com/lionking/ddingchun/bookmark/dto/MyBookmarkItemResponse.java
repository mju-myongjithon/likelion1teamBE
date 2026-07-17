package com.lionking.ddingchun.bookmark.dto;

import com.lionking.ddingchun.bookmark.entity.Bookmark;
import com.lionking.ddingchun.crawler.domain.Notice;
import com.lionking.ddingchun.post.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record MyBookmarkItemResponse(

        Long bookmarkId,
        String type,
        Long targetId,
        String title,
        String category,
        String source,
        String link,
        Integer dday,
        List<String> tags,
        Integer currentCount,
        Integer maxCount,
        LocalDateTime bookmarkedAt

) {

    /*
     * 사용자가 작성한 모집글 찜 응답
     */
    public static MyBookmarkItemResponse fromPost(
            Bookmark bookmark,
            Post post
    ) {
        return new MyBookmarkItemResponse(
                bookmark.getId(),
                "POST",
                post.getId(),
                post.getTitle(),
                post.getCategory() == null
                        ? null
                        : post.getCategory().name(),
                null,
                null,
                calculateDday(post),
                post.getTags() == null
                        ? List.of()
                        : List.copyOf(post.getTags()),
                post.getCurrentCount(),
                post.getMaxCount(),
                bookmark.getCreatedAt()
        );
    }

    /*
     * 크롤링한 학교 공지 찜 응답
     */
    public static MyBookmarkItemResponse fromNotice(
            Bookmark bookmark,
            Notice notice
    ) {
        return new MyBookmarkItemResponse(
                bookmark.getId(),
                "NOTICE",
                notice.getId(),
                notice.getTitle(),
                notice.getCategory(),
                notice.getSource(),
                notice.getLink(),
                null,
                List.of(),
                null,
                null,
                bookmark.getCreatedAt()
        );
    }

    private static Integer calculateDday(Post post) {
        if (post.getEventDate() == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate eventDate =
                post.getEventDate().toLocalDate();

        long days = ChronoUnit.DAYS.between(
                today,
                eventDate
        );

        return Math.toIntExact(days);
    }
}