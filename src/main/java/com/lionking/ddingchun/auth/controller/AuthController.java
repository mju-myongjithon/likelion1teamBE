package com.lionking.ddingchun.auth.controller;

import com.lionking.ddingchun.auth.dto.EmailSendRequest;
import com.lionking.ddingchun.auth.dto.EmailVerifyRequest;
import com.lionking.ddingchun.auth.dto.SignupRequest;
import com.lionking.ddingchun.auth.exception.EmailNotVerifiedException;
import com.lionking.ddingchun.auth.service.EmailVerificationService;
import com.lionking.ddingchun.global.response.ApiResponse;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.exception.DuplicateEmailException;
import com.lionking.ddingchun.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Operation(summary = "학교 이메일 인증번호 발송")
    @PostMapping("/email/send")
    public ApiResponse<Void> sendVerificationEmail(
            @Valid @RequestBody EmailSendRequest request
    ) {
        emailVerificationService.sendVerificationCode(request.email());

        return new ApiResponse<>(true, "COMMON200", "인증메일이 발송되었습니다.", null);
    }

    @Operation(summary = "학교 이메일 인증번호 확인")
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmail(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        emailVerificationService.verifyCode(request.email(), request.code());

        return new ApiResponse<>(true, "COMMON200", "이메일 인증이 완료되었습니다.", null);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<Void> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        if (!emailVerificationService.isVerified(request.email())) {
            throw new EmailNotVerifiedException();
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        User user = User.builder()
                .email(request.email())
                .course(request.course())
                .campus(request.campus())
                .college(request.college())
                .department(request.department())
                .studentId(request.studentId())
                .tags(request.tags())
                .build();

        userRepository.save(user);

        return new ApiResponse<>(true, "COMMON201", "회원가입 성공", null);
    }

}