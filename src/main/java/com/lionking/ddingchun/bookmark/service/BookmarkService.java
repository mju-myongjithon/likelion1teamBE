package com.lionking.ddingchun.bookmark.service;

import com.lionking.ddingchun.bookmark.dto.MyBookmarkItemResponse;
import com.lionking.ddingchun.bookmark.dto.MyBookmarkListResponse;
import com.lionking.ddingchun.bookmark.entity.Bookmark;
import com.lionking.ddingchun.bookmark.entity.BookmarkFilterType;
import com.lionking.ddingchun.bookmark.entity.BookmarkSortType;
import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;
import com.lionking.ddingchun.bookmark.repository.BookmarkRepository;
import com.lionking.ddingchun.crawler.domain.Notice;
import com.lionking.ddingchun.crawler.repository.NoticeRepository;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.repository.PostRepository;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 모집글 또는 공지를 찜한다.
     * 이미 찜한 경우 기존 데이터를 반환한다.
     */
    @Transactional
    public Bookmark addBookmark(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    ) {
        validateUser(userId);
        validateTarget(targetType, targetId);

        return bookmarkRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId,
                        targetType,
                        targetId
                )
                .orElseGet(() ->
                        bookmarkRepository.save(
                                new Bookmark(
                                        userId,
                                        targetType,
                                        targetId
                                )
                        )
                );
    }

    /**
     * 모집글 또는 공지의 찜을 해제한다.
     */
    @Transactional
    public void removeBookmark(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    ) {
        validateUser(userId);

        bookmarkRepository
                .deleteByUserIdAndTargetTypeAndTargetId(
                        userId,
                        targetType,
                        targetId
                );
    }

    /**
     * 특정 모집글 또는 공지를 찜했는지 확인한다.
     */
    @Transactional(readOnly = true)
    public boolean isBookmarked(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    ) {
        validateUser(userId);

        return bookmarkRepository
                .existsByUserIdAndTargetTypeAndTargetId(
                        userId,
                        targetType,
                        targetId
                );
    }

    /**
     * 마이페이지 찜 목록을 조회한다.
     *
     * type:
     * ALL = 전체
     * POST = 모집글
     * NOTICE = 학교 공지
     *
     * sort:
     * LATEST = 최근 찜한 순서
     * DEADLINE = 마감 임박순
     */
    @Transactional(readOnly = true)
    public MyBookmarkListResponse getMyBookmarkList(
            Long userId,
            BookmarkFilterType type,
            BookmarkSortType sort
    ) {
        validateUser(userId);

        BookmarkFilterType safeType =
                type == null
                        ? BookmarkFilterType.ALL
                        : type;

        BookmarkSortType safeSort =
                sort == null
                        ? BookmarkSortType.DEADLINE
                        : sort;

        List<MyBookmarkItemResponse> items =
                bookmarkRepository
                        .findAllByUserIdOrderByCreatedAtDesc(userId)
                        .stream()
                        .filter(bookmark ->
                                matchesFilter(
                                        bookmark,
                                        safeType
                                )
                        )
                        .map(this::convertToResponse)
                        .flatMap(Optional::stream)
                        .toList();

        List<MyBookmarkItemResponse> sortedItems =
                sortBookmarkItems(
                        items,
                        safeSort
                );

        return MyBookmarkListResponse.from(sortedItems);
    }

    /**
     * 필터 조건과 찜 종류가 일치하는지 확인한다.
     */
    private boolean matchesFilter(
            Bookmark bookmark,
            BookmarkFilterType filterType
    ) {
        if (filterType == BookmarkFilterType.ALL) {
            return true;
        }

        if (filterType == BookmarkFilterType.POST) {
            return bookmark.getTargetType()
                    == BookmarkTargetType.POST;
        }

        if (filterType == BookmarkFilterType.NOTICE) {
            return bookmark.getTargetType()
                    == BookmarkTargetType.NOTICE;
        }

        return false;
    }

    /**
     * Bookmark 엔티티를 마이페이지 응답 형태로 변환한다.
     *
     * 찜한 뒤 원본 게시글 또는 공지가 삭제됐다면
     * 해당 항목은 목록에서 제외한다.
     */
    private Optional<MyBookmarkItemResponse> convertToResponse(
            Bookmark bookmark
    ) {
        if (bookmark.getTargetType()
                == BookmarkTargetType.POST) {

            Optional<Post> postOptional =
                    postRepository.findById(
                            bookmark.getTargetId()
                    );

            return postOptional.map(post ->
                    MyBookmarkItemResponse.fromPost(
                            bookmark,
                            post
                    )
            );
        }

        if (bookmark.getTargetType()
                == BookmarkTargetType.NOTICE) {

            Optional<Notice> noticeOptional =
                    noticeRepository.findById(
                            bookmark.getTargetId()
                    );

            return noticeOptional.map(notice ->
                    MyBookmarkItemResponse.fromNotice(
                            bookmark,
                            notice
                    )
            );
        }

        return Optional.empty();
    }

    /**
     * 마이페이지 찜 목록을 정렬한다.
     */
    private List<MyBookmarkItemResponse> sortBookmarkItems(
            List<MyBookmarkItemResponse> items,
            BookmarkSortType sortType
    ) {
        if (sortType == BookmarkSortType.DEADLINE) {
            return items.stream()
                    .sorted(
                            Comparator
                                    .comparingInt(
                                            this::getDeadlineSortValue
                                    )
                                    .thenComparing(
                                            MyBookmarkItemResponse::bookmarkedAt,
                                            Comparator.nullsLast(
                                                    Comparator.reverseOrder()
                                            )
                                    )
                    )
                    .toList();
        }

        return items.stream()
                .sorted(
                        Comparator.comparing(
                                MyBookmarkItemResponse::bookmarkedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder()
                                )
                        )
                )
                .toList();
    }

    /**
     * D-day가 작을수록 앞에 표시한다.
     *
     * 마감일이 없거나 이미 마감된 항목은 뒤로 보낸다.
     */
    private int getDeadlineSortValue(
            MyBookmarkItemResponse item
    ) {
        Integer dday = item.dday();

        if (dday == null || dday < 0) {
            return Integer.MAX_VALUE;
        }

        return dday;
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID는 필수입니다."
            );
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "존재하지 않는 사용자입니다. userId="
                            + userId
            );
        }
    }

    private void validateTarget(
            BookmarkTargetType targetType,
            Long targetId
    ) {
        if (targetType == null) {
            throw new IllegalArgumentException(
                    "찜 대상 종류는 필수입니다."
            );
        }

        if (targetId == null) {
            throw new IllegalArgumentException(
                    "찜 대상 ID는 필수입니다."
            );
        }

        if (targetType == BookmarkTargetType.POST) {
            if (!postRepository.existsById(targetId)) {
                throw new IllegalArgumentException(
                        "존재하지 않는 모집글입니다. postId="
                                + targetId
                );
            }

            return;
        }

        if (targetType == BookmarkTargetType.NOTICE) {
            if (!noticeRepository.existsById(targetId)) {
                throw new IllegalArgumentException(
                        "존재하지 않는 공지입니다. noticeId="
                                + targetId
                );
            }

            return;
        }

        throw new IllegalArgumentException(
                "지원하지 않는 찜 대상입니다."
        );
    }
}