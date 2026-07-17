package com.lionking.ddingchun.chat.dto;

import com.lionking.ddingchun.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(

        Long messageId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt,
        boolean mine

) {

    public static ChatMessageResponse from(
            ChatMessage message,
            Long viewerId
    ) {
        return new ChatMessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getContent(),
                message.getSentAt(),
                message.getSender().getId().equals(viewerId)
        );
    }
}