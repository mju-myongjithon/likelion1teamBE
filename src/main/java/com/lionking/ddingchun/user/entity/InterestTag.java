package com.lionking.ddingchun.user.entity;

import com.lionking.ddingchun.user.exception.InvalidUserFieldException;

import java.util.Arrays;

public enum InterestTag {
    CONTEST("공모전"),
    SCHOLARSHIP("장학금"),
    FESTIVAL("축제"),
    FLASH_MEETUP("번개모임"),
    CLUB("동아리"),
    EXHIBITION("전시"),
    RUNNING("러닝"),
    EMPLOYMENT("취업"),
    STUDY("스터디");

    private final String label;

    InterestTag(String label) {
        this.label = label;
    }

    public static InterestTag fromLabel(String label) {
        return Arrays.stream(values())
                .filter(tag -> tag.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new InvalidUserFieldException("올바른 관심 태그를 선택해주세요."));
    }
}