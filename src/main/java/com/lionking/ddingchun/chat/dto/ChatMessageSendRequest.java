package com.lionking.ddingchun.chat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageSendRequest(

        /*
         * JWT 인증 구현 전까지 임시 사용
         */
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "메시지 내용을 입력해주세요.")
        String content

) {
}