package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record PostListItemResponse(

        Long postId,
        String category,
        String title,
        LocalDateTime eventDate,
        String place,
        String campus,
        int currentCount,
        int maxCount,
        String status,
        List<String> tags,
        int dday,
        boolean bookmarked,
        LocalDateTime createdAt

) {

    public static PostListItemResponse from(
            Post post,
            boolean bookmarked
    ) {
        return new PostListItemResponse(
                post.getId(),
                post.getCategory().name(),
                post.getTitle(),
                post.getEventDate(),
                post.getPlace(),
                post.getCampus().name(),
                post.getCurrentCount(),
                post.getMaxCount(),
                post.getStatus().name(),
                post.getTags() == null
                        ? List.of()
                        : List.copyOf(post.getTags()),
                calculateDday(post),
                bookmarked,
                post.getCreatedAt()
        );
    }

    private static int calculateDday(Post post) {
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