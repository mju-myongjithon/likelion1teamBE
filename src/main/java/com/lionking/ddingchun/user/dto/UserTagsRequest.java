package com.lionking.ddingchun.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserTagsRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Size(min = 3, message = "관심 태그를 3개 이상 선택해주세요.")
        List<String> tags

) {
}