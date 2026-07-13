package com.lionking.ddingchun.user.service;

import com.lionking.ddingchun.auth.exception.EmailNotVerifiedException;
import com.lionking.ddingchun.auth.service.EmailVerificationService;
import com.lionking.ddingchun.user.dto.UserProfileRequest;
import com.lionking.ddingchun.user.dto.UserTagsRequest;
import com.lionking.ddingchun.user.entity.Campus;
import com.lionking.ddingchun.user.entity.College;
import com.lionking.ddingchun.user.entity.Course;
import com.lionking.ddingchun.user.entity.InterestTag;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.exception.DuplicateEmailException;
import com.lionking.ddingchun.user.exception.InvalidUserFieldException;
import com.lionking.ddingchun.user.exception.UserNotFoundException;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MIN_INTEREST_TAG_COUNT = 3;

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public void saveProfile(UserProfileRequest request) {
        if (!emailVerificationService.isVerified(request.email())) {
            throw new EmailNotVerifiedException();
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        User user = User.builder()
                .email(request.email())
                .course(Course.fromLabel(request.course()))
                .campus(Campus.fromLabel(request.campus()))
                .college(College.fromLabel(request.college()))
                .department(request.department())
                .studentId(request.studentId())
                .tags(List.of())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void saveTags(UserTagsRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        user.updateTags(toInterestTags(request.tags()));
    }

    private List<InterestTag> toInterestTags(List<String> rawTags) {
        Set<InterestTag> tags = new LinkedHashSet<>();
        for (String rawTag : rawTags) {
            tags.add(InterestTag.fromLabel(rawTag));
        }

        if (tags.size() < MIN_INTEREST_TAG_COUNT) {
            throw new InvalidUserFieldException("관심 태그를 중복 없이 3개 이상 선택해주세요.");
        }

        return List.copyOf(tags);
    }
}