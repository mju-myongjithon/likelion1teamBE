package com.lionking.ddingchun.search.dto;

import com.lionking.ddingchun.post.entity.Post;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record SearchResultItem(

        String type,
        Long id,
        String title,
        String category,
        String source,
        Integer dday,
        List<String> tags,
        Integer currentCount,
        Integer maxCount

) {

    /*
     * Post 엔티티를 검색 결과 형태로 변환한다.
     */
    public static SearchResultItem fromPost(Post post) {
        return new SearchResultItem(
                "POST",
                post.getId(),
                post.getTitle(),
                post.getCategory().name(),
                null,
                calculateDday(post),
                List.copyOf(post.getTags()),
                post.getCurrentCount(),
                post.getMaxCount()
        );
    }

    private static int calculateDday(Post post) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = post.getEventDate().toLocalDate();

        long days = ChronoUnit.DAYS.between(
                today,
                eventDate
        );

        return Math.toIntExact(days);
    }
}