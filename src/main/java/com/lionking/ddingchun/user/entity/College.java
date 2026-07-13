package com.lionking.ddingchun.user.entity;

import com.lionking.ddingchun.user.exception.InvalidUserFieldException;

import java.util.Arrays;

public enum College {
    HONOR("아너칼리지"),
    HUMANITIES("인문대학"),
    BUSINESS("경영대학"),
    SOCIAL_SCIENCE("사회과학대학"),
    MEDIA_HUMAN_LIFE("미디어휴먼라이프대학"),
    AI_SOFTWARE_CONVERGENCE("인공지능소프트웨어융합대학"),
    FUTURE_CONVERGENCE("미래융합대학"),
    CHEMISTRY_LIFE_SCIENCE("화학생명과학대학"),
    SMART_SYSTEM_ENGINEERING("스마트시스템공과대학"),
    SEMICONDUCTOR_ICT("반도체ICT대학"),
    SPORTS_ART("스포츠예술대학"),
    ARCHITECTURE("건축대학");

    private final String label;

    College(String label) {
        this.label = label;
    }

    public static College fromLabel(String label) {
        return Arrays.stream(values())
                .filter(college -> college.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new InvalidUserFieldException("올바른 단과대학을 선택해주세요."));
    }
}