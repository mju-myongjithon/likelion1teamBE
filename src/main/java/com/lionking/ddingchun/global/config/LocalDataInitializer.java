package com.lionking.ddingchun.global.config;

import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.entity.PostCampus;
import com.lionking.ddingchun.post.entity.PostCategory;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.user.entity.Campus;
import com.lionking.ddingchun.user.entity.College;
import com.lionking.ddingchun.user.entity.Course;
import com.lionking.ddingchun.user.entity.InterestTag;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalDataInitializer implements CommandLineRunner {

    private static final String AUTHOR_EMAIL =
            "author@mju.ac.kr";

    private static final String APPLICANT_EMAIL =
            "applicant@mju.ac.kr";

    private static final String TEST_POST_TITLE =
            "AI 아이디어 공모전 같이 참가할 팀원 구합니다";

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void run(String... args) {

        User author = findOrCreateAuthor();
        findOrCreateApplicant();

        boolean testPostExists = postRepository.findAll()
                .stream()
                .anyMatch(post ->
                        TEST_POST_TITLE.equals(post.getTitle())
                );

        if (!testPostExists) {
            Post post = Post.builder()
                    .author(author)
                    .category(PostCategory.CONTEST)
                    .title(TEST_POST_TITLE)
                    .description(
                            "명지톤에서 AI 아이디어 공모전에 " +
                            "함께 참가할 백엔드 팀원을 모집합니다."
                    )
                    .eventDate(
                            LocalDateTime.now()
                                    .plusDays(7)
                                    .withHour(14)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .withNano(0)
                    )
                    .place("명지대학교 자연캠퍼스")
                    .campus(PostCampus.ALL)
                    .maxCount(4)
                    .tags(
                            List.of(
                                    "공모전",
                                    "AI",
                                    "팀원모집"
                            )
                    )
                    .sourceNoticeId(null)
                    .build();

            postRepository.save(post);
        }
    }

    private User findOrCreateAuthor() {
        return userRepository.findByEmail(AUTHOR_EMAIL)
                .orElseGet(() ->
                        userRepository.save(
                                User.builder()
                                        .email(AUTHOR_EMAIL)
                                        .name("홍길동")
                                        .course(Course.UNDERGRADUATE)
                                        .campus(Campus.NATURAL_SCIENCE)
                                        .college(
                                                College.AI_SOFTWARE_CONVERGENCE
                                        )
                                        .department("응용소프트웨어전공")
                                        .studentId("60240001")
                                        .tags(
                                                List.of(
                                                        InterestTag.CONTEST,
                                                        InterestTag.STUDY
                                                )
                                        )
                                        .build()
                        )
                );
    }

    private User findOrCreateApplicant() {
        return userRepository.findByEmail(APPLICANT_EMAIL)
                .orElseGet(() ->
                        userRepository.save(
                                User.builder()
                                        .email(APPLICANT_EMAIL)
                                        .name("김영희")
                                        .course(Course.UNDERGRADUATE)
                                        .campus(Campus.HUMANITIES)
                                        .college(College.BUSINESS)
                                        .department("경영학과")
                                        .studentId("60240002")
                                        .tags(
                                                List.of(
                                                        InterestTag.CONTEST,
                                                        InterestTag.EMPLOYMENT
                                                )
                                        )
                                        .build()
                        )
                );
    }
}