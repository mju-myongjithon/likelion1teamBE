package com.lionking.ddingchun.post.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostStatus {

    RECRUITING("모집 중"),
    CLOSED("모집 마감");

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PostStatus from(String value) {
        for (PostStatus status : PostStatus.values()) {
            // 영문(상수명) 혹은 한글 설명 어느 쪽으로 들어와도 매칭
            if (status.name().equals(value) || status.getDescription().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 상태입니다: " + value);
    }
}