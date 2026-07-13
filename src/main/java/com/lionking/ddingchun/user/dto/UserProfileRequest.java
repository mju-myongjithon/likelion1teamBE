package com.lionking.ddingchun.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "과정을 선택해주세요.")
        String course,

        @NotBlank(message = "캠퍼스를 선택해주세요.")
        String campus,

        @NotBlank(message = "단과대학을 선택해주세요.")
        String college,

        @NotBlank(message = "학과를 입력해주세요.")
        String department,

        @NotBlank(message = "학번을 입력해주세요.")
        String studentId

) {}