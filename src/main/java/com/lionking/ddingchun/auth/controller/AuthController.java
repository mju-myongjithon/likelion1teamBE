package com.lionking.ddingchun.auth.controller;

import com.lionking.ddingchun.auth.dto.EmailSendRequest;
import com.lionking.ddingchun.auth.dto.EmailVerifyRequest;
import com.lionking.ddingchun.auth.service.EmailVerificationService;
import com.lionking.ddingchun.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "인증 메일 발송")
    @PostMapping("/email/send")
    public ApiResponse<Void> sendVerificationEmail(
            @Valid @RequestBody EmailSendRequest request
    ) {
        emailVerificationService.sendVerificationCode(request.email());

        return new ApiResponse<>(true, "COMMON200", "인증메일이 발송되었습니다.", null);
    }

    @Operation(summary = "인증번호 확인")
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmail(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        emailVerificationService.verifyCode(request.email(), request.code());

        return new ApiResponse<>(true, "COMMON200", "이메일 인증이 완료되었습니다.", null);
    }

}