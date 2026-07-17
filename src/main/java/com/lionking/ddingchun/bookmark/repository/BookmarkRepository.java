package com.lionking.ddingchun.bookmark.repository;

import com.lionking.ddingchun.bookmark.entity.Bookmark;
import com.lionking.ddingchun.bookmark.entity.BookmarkTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    );

    Optional<Bookmark> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    );

    List<Bookmark> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    long deleteByUserIdAndTargetTypeAndTargetId(
            Long userId,
            BookmarkTargetType targetType,
            Long targetId
    );
    long deleteAllByTargetTypeAndTargetId(
        BookmarkTargetType targetType,
        Long targetId
    );
}