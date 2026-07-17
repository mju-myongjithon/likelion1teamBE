package com.lionking.ddingchun.chat.dto;

import com.lionking.ddingchun.chat.entity.ChatMessage;
import com.lionking.ddingchun.chat.entity.ChatRoom;

import java.util.List;

public record ChatRoomDetailResponse(

        Long roomId,
        Long postId,
        String postTitle,
        List<ChatMessageResponse> messages

) {

    public static ChatRoomDetailResponse of(
            ChatRoom chatRoom,
            List<ChatMessage> messages,
            Long viewerId
    ) {
        return new ChatRoomDetailResponse(
                chatRoom.getId(),
                chatRoom.getPost().getId(),
                chatRoom.getPost().getTitle(),
                messages.stream()
                        .map(message -> ChatMessageResponse.from(message, viewerId))
                        .toList()
        );
    }
}