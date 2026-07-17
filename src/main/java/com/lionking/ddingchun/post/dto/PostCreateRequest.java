package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.PostCampus;
import com.lionking.ddingchun.post.entity.PostCategory;

import java.time.LocalDateTime;
import java.util.List;

public record PostCreateRequest(

        Long authorId,
        PostCategory category,
        String title,
        String description,
        LocalDateTime eventDate,
        String place,
        PostCampus campus,
        int maxCount,
        List<String> tags,
        Long sourceNoticeId

) {
}