package com.lionking.ddingchun.application.controller;

import com.lionking.ddingchun.application.dto.ApplicationCreateRequest;
import com.lionking.ddingchun.application.dto.ApplicationCreateResponse;
import com.lionking.ddingchun.application.service.ApplicationService;
import com.lionking.ddingchun.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}