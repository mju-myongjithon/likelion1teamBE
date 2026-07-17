package com.lionking.ddingchun.post.service;

import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;
import com.lionking.ddingchun.bookmark.repository.BookmarkRepository;
import com.lionking.ddingchun.post.dto.PostCreateRequest;
import com.lionking.ddingchun.post.dto.PostCreateResponse;
import com.lionking.ddingchun.post.dto.PostDetailResponse;
import com.lionking.ddingchun.post.dto.PostListResponse;
import com.lionking.ddingchun.post.dto.PostUpdateRequest;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.entity.PostCampus;
import com.lionking.ddingchun.post.entity.PostCategory;
import com.lionking.ddingchun.post.entity.PostSortType;
import com.lionking.ddingchun.post.entity.PostStatus;
import com.lionking.ddingchun.post.exception.PostNotFoundException;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 작성
     */
    @Transactional
    public PostCreateResponse createPost(
            PostCreateRequest request
    ) {
        validateCreateRequest(request);

        User author = userRepository
                .findById(request.authorId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 사용자입니다. userId="
                                        + request.authorId()
                        )
                );

        List<String> normalizedTags =
                normalizeTags(request.tags());

        Post post = Post.builder()
                .author(author)
                .category(request.category())
                .title(request.title().trim())
                .description(request.description().trim())
                .eventDate(request.eventDate())
                .place(request.place().trim())
                .campus(request.campus())
                .maxCount(request.maxCount())
                .tags(normalizedTags)
                .sourceNoticeId(request.sourceNoticeId())
                .build();

        Post savedPost = postRepository.save(post);

        return PostCreateResponse.from(savedPost);
    }

    /**
     * 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public PostListResponse getPostList(
            Long userId,
            PostCategory category,
            PostCampus campus,
            PostStatus status,
            PostSortType sort,
            int page,
            int size
    ) {
        validatePageCondition(page, size);
        validateOptionalUser(userId);

        PostSortType safeSort =
                sort == null
                        ? PostSortType.LATEST
                        : sort;

        Sort springSort;

        if (safeSort == PostSortType.DEADLINE) {
            springSort = Sort.by(
                            Sort.Direction.ASC,
                            "eventDate"
                    )
                    .and(
                            Sort.by(
                                    Sort.Direction.DESC,
                                    "createdAt"
                            )
                    );
        } else {
            springSort = Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                springSort
        );

        Page<Post> postPage =
                postRepository.findAllWithFilters(
                        category,
                        campus,
                        status,
                        pageable
                );

        Set<Long> bookmarkedPostIds =
                getBookmarkedPostIds(userId);

        return PostListResponse.from(
                postPage,
                bookmarkedPostIds
        );
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailResponse.from(post);
    }

    /**
     * 게시글 수정
     *
     * 작성자 본인만 수정할 수 있다.
     */
    @Transactional
    public PostDetailResponse updatePost(
            Long postId,
            PostUpdateRequest request
    ) {
        validateUpdateRequest(request);

        Post post = postRepository
                .findById(postId)
                .orElseThrow(PostNotFoundException::new);

        validatePostAuthor(
                post,
                request.authorId()
        );

        post.updateContent(
                request.title().trim(),
                request.description().trim()
        );

        post.updateEventInformation(
                request.eventDate(),
                request.place().trim(),
                request.campus()
        );

        post.updateMaxCount(
                request.maxCount()
        );

        post.updateTags(
                normalizeTags(request.tags())
        );

        return PostDetailResponse.from(post);
    }

    private Set<Long> getBookmarkedPostIds(Long userId) {
        if (userId == null) {
            return Set.of();
        }

        return bookmarkRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(bookmark ->
                        bookmark.getTargetType()
                                == BookmarkTargetType.POST
                )
                .map(bookmark ->
                        bookmark.getTargetId()
                )
                .collect(Collectors.toSet());
    }

    private void validatePostAuthor(
            Post post,
            Long authorId
    ) {
        if (!post.getAuthor()
                .getId()
                .equals(authorId)) {

            throw new IllegalArgumentException(
                    "게시글 작성자만 수정할 수 있습니다."
            );
        }
    }

    private void validateCreateRequest(
            PostCreateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "게시글 작성 정보가 필요합니다."
            );
        }

        if (request.authorId() == null) {
            throw new IllegalArgumentException(
                    "작성자 ID는 필수입니다."
            );
        }

        if (request.category() == null) {
            throw new IllegalArgumentException(
                    "게시글 카테고리는 필수입니다."
            );
        }

        validateCommonPostFields(
                request.title(),
                request.description(),
                request.eventDate(),
                request.place(),
                request.campus(),
                request.maxCount()
        );
    }

    private void validateUpdateRequest(
            PostUpdateRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "게시글 수정 정보가 필요합니다."
            );
        }

        if (request.authorId() == null) {
            throw new IllegalArgumentException(
                    "작성자 ID는 필수입니다."
            );
        }

        validateCommonPostFields(
                request.title(),
                request.description(),
                request.eventDate(),
                request.place(),
                request.campus(),
                request.maxCount()
        );
    }

    private void validateCommonPostFields(
            String title,
            String description,
            java.time.LocalDateTime eventDate,
            String place,
            PostCampus campus,
            int maxCount
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "게시글 제목은 필수입니다."
            );
        }

        if (description == null
                || description.isBlank()) {
            throw new IllegalArgumentException(
                    "게시글 내용은 필수입니다."
            );
        }

        if (eventDate == null) {
            throw new IllegalArgumentException(
                    "모임 또는 행사 일시는 필수입니다."
            );
        }

        if (place == null || place.isBlank()) {
            throw new IllegalArgumentException(
                    "장소는 필수입니다."
            );
        }

        if (campus == null) {
            throw new IllegalArgumentException(
                    "캠퍼스는 필수입니다."
            );
        }

        if (maxCount < 2) {
            throw new IllegalArgumentException(
                    "모집 인원은 작성자를 포함하여 2명 이상이어야 합니다."
            );
        }
    }

    private List<String> normalizeTags(
            List<String> tags
    ) {
        if (tags == null) {
            return List.of();
        }

        return tags.stream()
                .filter(tag -> tag != null)
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }

    private void validateOptionalUser(Long userId) {
        if (userId == null) {
            return;
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 사용자입니다. userId="
                            + userId
            );
        }
    }

    private void validatePageCondition(
            int page,
            int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "페이지 크기는 1 이상 100 이하여야 합니다."
            );
        }
    }
    /**
 * 모집글 직접 마감
 *
 * 작성자 본인만 마감할 수 있다.
 */
@Transactional
public PostDetailResponse closePost(
        Long postId,
        Long authorId
) {
    Post post = postRepository
            .findById(postId)
            .orElseThrow(PostNotFoundException::new);

    validatePostAuthor(post, authorId);

    post.close();

    return PostDetailResponse.from(post);
}

/**
 * 모집글 삭제
 *
 * 작성자 본인만 삭제할 수 있다.
 */
@Transactional
public void deletePost(
        Long postId,
        Long authorId
) {
    Post post = postRepository
            .findById(postId)
            .orElseThrow(PostNotFoundException::new);

    validatePostAuthor(post, authorId);

    bookmarkRepository
            .deleteAllByTargetTypeAndTargetId(
                    BookmarkTargetType.POST,
                    postId
            );

    postRepository.delete(post);
}
}