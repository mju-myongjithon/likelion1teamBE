package com.lionking.ddingchun.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(min = 2, max = 10, message = "이름은 2~10자여야 합니다.")
        String name,

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