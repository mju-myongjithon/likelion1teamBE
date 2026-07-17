package com.lionking.ddingchun.post.entity;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostCampus {
    HUMANITIES("인문캠퍼스"),
    NATURAL_SCIENCE("자연캠퍼스"),
    ALL("무관");

    private final String value;

    PostCampus(String value) {
        this.value = value;
    }

    @JsonValue // 이 어노테이션이 핵심!
    public String getValue() {
        return value;
    }
}