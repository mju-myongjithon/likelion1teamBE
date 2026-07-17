package com.lionking.ddingchun.user.dto;

import com.lionking.ddingchun.user.entity.User;

public record UserProfileCreateResponse(

        Long userId,
        String email

) {

    public static UserProfileCreateResponse from(User user) {
        return new UserProfileCreateResponse(
                user.getId(),
                user.getEmail()
        );
    }
}