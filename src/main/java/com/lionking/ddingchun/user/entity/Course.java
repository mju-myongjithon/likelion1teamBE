package com.lionking.ddingchun.user.entity;

import com.lionking.ddingchun.user.exception.InvalidUserFieldException;

import java.util.Arrays;

public enum Course {
    UNDERGRADUATE("학부생"),
    GRADUATE("대학원생");

    private final String label;

    Course(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Course fromLabel(String label) {
        return Arrays.stream(values())
                .filter(course -> course.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new InvalidUserFieldException("올바른 과정을 선택해주세요."));
    }
}