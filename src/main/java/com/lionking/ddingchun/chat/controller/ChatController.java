package com.lionking.ddingchun.chat.controller;

import com.lionking.ddingchun.chat.dto.ChatMessageResponse;
import com.lionking.ddingchun.chat.dto.ChatMessageSendRequest;
import com.lionking.ddingchun.chat.dto.ChatRoomDetailResponse;
import com.lionking.ddingchun.chat.dto.ChatRoomListResponse;
import com.lionking.ddingchun.chat.service.ChatService;
import com.lionking.ddingchun.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "내 채팅방 목록 조회")
    @GetMapping
    public ApiResponse<ChatRoomListResponse> getMyChatRooms(
            @RequestParam String email
    ) {
        ChatRoomListResponse response =
                chatService.getMyChatRooms(email);

        return new ApiResponse<>(
                true,
                "COMMON200",
                "채팅방 목록 조회에 성공했습니다.",
                response
        );
    }

    @Operation(summary = "채팅방 메시지 조회")
    @GetMapping("/{roomId}/messages")
    public ApiResponse<ChatRoomDetailResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam String email
    ) {
        ChatRoomDetailResponse response =
                chatService.getMessages(roomId, email);

        return new ApiResponse<>(
                true,
                "COMMON200",
                "채팅 메시지 조회에 성공했습니다.",
                response
        );
    }

    @Operation(summary = "채팅 메시지 전송")
    @PostMapping("/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageSendRequest request
    ) {
        ChatMessageResponse response =
                chatService.sendMessage(roomId, request);

        return new ApiResponse<>(
                true,
                "COMMON201",
                "메시지를 전송했습니다.",
                response
        );
    }
}