package com.lionking.ddingchun.post.dto;

import com.lionking.ddingchun.post.entity.PostCampus;

import java.time.LocalDateTime;
import java.util.List;

public record PostUpdateRequest(

        Long authorId,
        String title,
        String description,
        LocalDateTime eventDate,
        String place,
        PostCampus campus,
        int maxCount,
        List<String> tags

) {
}