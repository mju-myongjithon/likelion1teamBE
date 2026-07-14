package com.lionking.ddingchun.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationCreateRequest(

        /*
         * JWT 인증 구현 전까지 임시 사용
         */
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Size(
                max = 100,
                message = "한 줄 소개는 100자 이하여야 합니다."
        )
        String introduction

) {
}