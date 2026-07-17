package com.lionking.ddingchun.user.controller;

import com.lionking.ddingchun.global.response.ApiResponse;
import com.lionking.ddingchun.user.dto.MyPageResponse;
import com.lionking.ddingchun.user.dto.UserProfileCreateResponse;
import com.lionking.ddingchun.user.dto.UserProfileRequest;
import com.lionking.ddingchun.user.dto.UserTagsRequest;
import com.lionking.ddingchun.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "소속 정보 저장")
    @PostMapping("/profile")
    public ApiResponse<UserProfileCreateResponse> saveProfile(
            @Valid @RequestBody UserProfileRequest request
    ) {
        UserProfileCreateResponse response = userService.saveProfile(request);

        return new ApiResponse<>(true, "COMMON201", "소속 정보가 저장되었습니다.", response);
    }

    @Operation(summary = "관심 태그 저장")
    @PostMapping("/tags")
    public ApiResponse<Void> saveTags(
            @Valid @RequestBody UserTagsRequest request
    ) {
        userService.saveTags(request);

        return new ApiResponse<>(true, "COMMON200", "관심 태그가 저장되었습니다.", null);
    }

    @Operation(summary = "마이페이지 조회")
    @GetMapping("/mypage")
    public ApiResponse<MyPageResponse> getMyPage(
            @RequestParam String email
    ) {
        MyPageResponse response = userService.getMyPage(email);

        return new ApiResponse<>(true, "COMMON200", "마이페이지 조회에 성공했습니다.", response);
    }

}