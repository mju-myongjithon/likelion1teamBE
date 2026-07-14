package com.lionking.ddingchun.post.repository;

import com.lionking.ddingchun.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    /*
     * 제목, 본문, 태그 중 하나라도 검색어를 포함하면 조회한다.
     */
    @Query(
            value = """
                    SELECT DISTINCT p
                    FROM Post p
                    LEFT JOIN p.tags tag
                    WHERE LOWER(p.title)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(p.description)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(tag)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                    ORDER BY p.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM Post p
                    LEFT JOIN p.tags tag
                    WHERE LOWER(p.title)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(p.description)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(tag)
                              LIKE LOWER(CONCAT('%', :keyword, '%'))
                    """
    )
    Page<Post> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}