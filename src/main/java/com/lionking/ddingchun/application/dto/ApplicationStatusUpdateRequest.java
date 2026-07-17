package com.lionking.ddingchun.application.dto;

import com.lionking.ddingchun.application.entity.ApplicationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequest(

        /*
         * JWT 인증 구현 전까지 임시 사용
         */
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotNull(message = "status는 필수입니다.")
        ApplicationStatus status

) {
}
