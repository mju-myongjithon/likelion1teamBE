package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(

        Long postId,
        String category,
        String title,
        String description,
        AuthorResponse author,
        LocalDateTime eventDate,
        String place,
        String campus,
        int currentCount,
        int maxCount,
        String status,
        List<String> tags,
        Long sourceNoticeId,
        LocalDateTime createdAt

) {

    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getCategory().name(),
                post.getTitle(),
                post.getDescription(),
                AuthorResponse.from(post.getAuthor()),
                post.getEventDate(),
                post.getPlace(),
                post.getCampus().name(),
                post.getCurrentCount(),
                post.getMaxCount(),
                post.getStatus().name(),
                List.copyOf(post.getTags()),
                post.getSourceNoticeId(),
                post.getCreatedAt()
        );
    }

    public record AuthorResponse(
            Long userId,
            String email,
            String department,
            String campus
    ) {

        public static AuthorResponse from(User user) {
            return new AuthorResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getDepartment(),
                    user.getCampus().name()
            );
        }
    }
}