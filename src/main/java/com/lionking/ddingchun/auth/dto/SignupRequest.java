package com.lionking.ddingchun.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SignupRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "전공을 선택해주세요.")
        String course,

        @NotBlank(message = "캠퍼스를 선택해주세요.")
        String campus,

        @NotBlank(message = "단과대학을 선택해주세요.")
        String college,

        @NotBlank(message = "학과를 입력해주세요.")
        String department,

        @NotBlank(message = "학번을 입력해주세요.")
        String studentId,

        @Size(min = 3, message = "관심 태그를 3개 이상 선택해주세요.")
        List<String> tags

) {
}