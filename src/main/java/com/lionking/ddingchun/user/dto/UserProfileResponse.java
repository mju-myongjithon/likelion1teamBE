package com.lionking.ddingchun.user.dto;

import com.lionking.ddingchun.user.entity.InterestTag;
import com.lionking.ddingchun.user.entity.User;

import java.util.List;

public record UserProfileResponse(

        String email,
        String course,
        String campus,
        String college,
        String department,
        String studentId,
        List<String> tags

) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getEmail(),
                user.getCourse().getLabel(),
                user.getCampus().getLabel(),
                user.getCollege().getLabel(),
                user.getDepartment(),
                user.getStudentId(),
                user.getTags().stream().map(InterestTag::getLabel).toList()
        );
    }
}