package com.lionking.ddingchun.user.entity;

import com.lionking.ddingchun.user.exception.InvalidUserFieldException;

import java.util.Arrays;

public enum Campus {
    HUMANITIES("인문캠"),
    NATURAL_SCIENCE("자연캠");

    private final String label;

    Campus(String label) {
        this.label = label;
    }

    public static Campus fromLabel(String label) {
        return Arrays.stream(values())
                .filter(campus -> campus.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new InvalidUserFieldException("올바른 캠퍼스를 선택해주세요."));
    }
}