package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.Post;

import java.time.LocalDateTime;

public record PostCreateResponse(

        Long postId,
        String status,
        LocalDateTime createdAt

) {

    public static PostCreateResponse from(Post post) {
        return new PostCreateResponse(
                post.getId(),
                post.getStatus().name(),
                post.getCreatedAt()
        );
    }
}