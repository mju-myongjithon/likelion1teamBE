package com.lionking.ddingchun.user.dto;

import com.lionking.ddingchun.application.entity.Application;
import com.lionking.ddingchun.application.entity.ApplicationStatus;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.user.entity.InterestTag;
import com.lionking.ddingchun.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record MyPageResponse(

        String email,
        String department,
        String campus,
        List<String> interestTags,
        int writtenPostCount,
        int joinedMeetingCount,
        List<ActivityResponse> activities

) {

    public static MyPageResponse of(
            User user,
            List<Post> writtenPosts,
            List<Application> applications,
            List<ActivityResponse> activities
    ) {
        long joinedMeetingCount = applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.ACCEPTED)
                .count();

        return new MyPageResponse(
                user.getEmail(),
                user.getDepartment(),
                user.getCampus().getLabel(),
                user.getTags().stream().map(InterestTag::getLabel).toList(),
                writtenPosts.size(),
                (int) joinedMeetingCount,
                activities
        );
    }

    /*
     * 내가 쓴 글 / 신청한 글을 하나로 합쳐서 보여주기 위한 활동 항목
     */
    public record ActivityResponse(

            Long postId,
            String activityType,
            String category,
            String title,
            String status,
            LocalDateTime date

    ) {

        public static ActivityResponse fromAuthoredPost(Post post) {
            return new ActivityResponse(
                    post.getId(),
                    "AUTHORED",
                    post.getCategory().name(),
                    post.getTitle(),
                    post.getStatus().name(),
                    post.getCreatedAt()
            );
        }

        public static ActivityResponse fromApplication(Application application) {
            return new ActivityResponse(
                    application.getPost().getId(),
                    "APPLIED",
                    application.getPost().getCategory().name(),
                    application.getPost().getTitle(),
                    application.getStatus().name(),
                    application.getAppliedAt()
            );
        }
    }
}