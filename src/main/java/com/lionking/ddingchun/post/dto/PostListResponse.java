package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public record PostListResponse(

        long totalCount,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        List<PostListItemResponse> posts

) {

    public static PostListResponse from(
            Page<Post> postPage,
            Set<Long> bookmarkedPostIds
    ) {
        Set<Long> safeBookmarkedPostIds =
                bookmarkedPostIds == null
                        ? Set.of()
                        : bookmarkedPostIds;

        List<PostListItemResponse> posts =
                postPage.getContent()
                        .stream()
                        .map(post ->
                                PostListItemResponse.from(
                                        post,
                                        safeBookmarkedPostIds.contains(
                                                post.getId()
                                        )
                                )
                        )
                        .toList();

        return new PostListResponse(
                postPage.getTotalElements(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.hasNext(),
                posts
        );
    }
}