package com.lionking.ddingchun.post.service;

import com.lionking.ddingchun.post.dto.PostDetailResponse;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.post.exception.PostNotFoundException;
import com.lionking.ddingchun.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostDetailResponse.from(post);
    }
}