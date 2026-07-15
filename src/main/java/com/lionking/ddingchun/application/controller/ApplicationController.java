package com.lionking.ddingchun.application.controller;

import com.lionking.ddingchun.application.dto.*;
import com.lionking.ddingchun.application.entity.ApplicationStatus;
import com.lionking.ddingchun.application.service.ApplicationService;
import com.lionking.ddingchun.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/applications")
@RequiredArgsConstructor
@Tag(name = "Application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "모집글 참여 신청")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApplicationCreateResponse> apply(
            @PathVariable Long postId,
            @Valid @RequestBody ApplicationCreateRequest request
    ) {
        ApplicationCreateResponse response =
                applicationService.apply(postId, request);

        return new ApiResponse<>(
                true,
                "COMMON201",
                "참여 신청이 완료되었습니다.",
                response
        );
    }

    @Operation(summary = "신청자 목록 조회 (작성자 전용)")
    @GetMapping
    public ApiResponse<PostApplicantsResponse> getApplicants(
            @PathVariable Long postId,
            @RequestParam String email
    ) {
        PostApplicantsResponse response =
                applicationService.getApplicants(postId, email);

        return new ApiResponse<>(
                true,
                "COMMON200",
                "신청자 목록 조회에 성공했습니다.",
                response
        );
    }

    @Operation(summary = "신청 수락/거절 (작성자 전용)")
    @PatchMapping("/{applicationId}")
    public ApiResponse<ApplicationStatusUpdateResponse> updateStatus(
            @PathVariable Long postId,
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        ApplicationStatusUpdateResponse response =
                applicationService.updateStatus(
                        postId,
                        applicationId,
                        request.email(),
                        request.status()
                );

        String message = request.status() == ApplicationStatus.ACCEPTED
                ? "신청을 수락했습니다."
                : "신청을 거절했습니다.";

        return new ApiResponse<>(
                true,
                "COMMON200",
                message,
                response
        );
    }
}