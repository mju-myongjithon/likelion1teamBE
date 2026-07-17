package com.lionking.ddingchun.application.dto;

import com.lionking.ddingchun.application.entity.Application;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostApplicantsResponse(

        Long postId,
        String postTitle,
        int currentCount,
        int maxCount,
        List<ApplicantResponse> applicants

) {

    public static PostApplicantsResponse of(
            Post post,
            List<Application> applications
    ) {
        return new PostApplicantsResponse(
                post.getId(),
                post.getTitle(),
                post.getCurrentCount(),
                post.getMaxCount(),
                applications.stream()
                        .map(ApplicantResponse::from)
                        .toList()
        );
    }

    public record ApplicantResponse(
            Long applicationId,
            Long userId,
            String name,
            String department,
            String campus,
            String introduction,
            String status,
            LocalDateTime appliedAt
    ) {

        public static ApplicantResponse from(Application application) {
            User applicant = application.getApplicant();

            return new ApplicantResponse(
                    application.getId(),
                    applicant.getId(),
                    applicant.getName(),
                    applicant.getDepartment(),
                    applicant.getCampus().name(),
                    application.getIntroduction(),
                    application.getStatus().name(),
                    application.getAppliedAt()
            );
        }
    }
}
