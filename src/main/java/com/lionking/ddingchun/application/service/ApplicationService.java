package com.lionking.ddingchun.application.service;

import com.lionking.ddingchun.application.dto.ApplicationCreateRequest;
import com.lionking.ddingchun.application.dto.ApplicationCreateResponse;
import com.lionking.ddingchun.application.dto.ApplicationStatusUpdateResponse;
import com.lionking.ddingchun.application.dto.PostApplicantsResponse;
import com.lionking.ddingchun.application.entity.Application;
import com.lionking.ddingchun.application.entity.ApplicationStatus;
import com.lionking.ddingchun.application.exception.*;
import com.lionking.ddingchun.application.repository.ApplicationRepository;
import com.lionking.ddingchun.chat.service.ChatService;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.entity.PostStatus;
import com.lionking.ddingchun.post.exception.PostNotFoundException;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.exception.UserNotFoundException;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    @Transactional
    public ApplicationCreateResponse apply(
            Long postId,
            ApplicationCreateRequest request
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        User applicant = userRepository
                .findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        validateSelfApplication(post, applicant);
        validatePostStatus(post);
        validateDuplicateApplication(post, applicant);

        Application application = Application.builder()
                .post(post)
                .applicant(applicant)
                .introduction(
                        normalizeIntroduction(
                                request.introduction()
                        )
                )
                .build();

        Application savedApplication =
                applicationRepository.save(application);

        return ApplicationCreateResponse.from(savedApplication);
    }

    private void validateSelfApplication(
            Post post,
            User applicant
    ) {
        if (post.getAuthor().getId().equals(applicant.getId())) {
            throw new SelfApplicationException();
        }
    }

    private void validatePostStatus(Post post) {
        if (post.getStatus() == PostStatus.CLOSED
                || post.getCurrentCount() >= post.getMaxCount()) {
            throw new PostClosedException();
        }
    }

    private void validateDuplicateApplication(
            Post post,
            User applicant
    ) {
        boolean alreadyApplied =
                applicationRepository
                        .existsByPost_IdAndApplicant_Id(
                                post.getId(),
                                applicant.getId()
                        );

        if (alreadyApplied) {
            throw new DuplicateApplicationException();
        }
    }

    private String normalizeIntroduction(
            String introduction
    ) {
        if (introduction == null
                || introduction.isBlank()) {
            return null;
        }

        return introduction.trim();
    }

    /*
     * 신청자 목록 조회 (작성자 전용)
     */
    @Transactional(readOnly = true)
    public PostApplicantsResponse getApplicants(
            Long postId,
            String requesterEmail
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(UserNotFoundException::new);

        validatePostAuthor(post, requester);

        List<Application> applications =
                applicationRepository.findByPost_IdOrderByAppliedAtDesc(postId);

        return PostApplicantsResponse.of(post, applications);
    }

    /*
     * 신청 수락/거절 (작성자 전용)
     */
    @Transactional
    public ApplicationStatusUpdateResponse updateStatus(
            Long postId,
            Long applicationId,
            String requesterEmail,
            ApplicationStatus newStatus
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(UserNotFoundException::new);

        validatePostAuthor(post, requester);

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        if (!application.getPost().getId().equals(postId)) {
            throw new ApplicationNotFoundException();
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new AlreadyProcessedApplicationException();
        }

        if (newStatus == ApplicationStatus.ACCEPTED) {
            try {
                post.acceptApplicant();
            } catch (IllegalStateException e) {
                throw new PostClosedException();
            }
            application.accept();
            chatService.addAcceptedApplicant(post, application.getApplicant());
        } else {
            application.reject();
        }

        return ApplicationStatusUpdateResponse.from(application);
    }

    private void validatePostAuthor(
            Post post,
            User requester
    ) {
        if (!post.getAuthor().getId().equals(requester.getId())) {
            throw new NotPostAuthorException();
        }
    }
}