package com.lionking.ddingchun.chat.dto;

import com.lionking.ddingchun.chat.entity.ChatMessage;
import com.lionking.ddingchun.chat.entity.ChatRoomMember;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomListResponse(

        List<ChatRoomSummary> chatRooms

) {

    public static ChatRoomListResponse from(List<ChatRoomSummary> chatRooms) {
        return new ChatRoomListResponse(chatRooms);
    }

    /*
     * 채팅 목록 화면 한 줄에 필요한 정보를 모은 요약
     */
    public record ChatRoomSummary(

            Long roomId,
            Long postId,
            String postTitle,
            String memberSummary,
            String lastMessage,
            LocalDateTime lastMessageAt,
            long unreadCount

    ) {

        public static ChatRoomSummary of(
                ChatRoomMember member,
                String memberSummary,
                ChatMessage lastMessage,
                long unreadCount
        ) {
            return new ChatRoomSummary(
                    member.getChatRoom().getId(),
                    member.getChatRoom().getPost().getId(),
                    member.getChatRoom().getPost().getTitle(),
                    memberSummary,
                    lastMessage == null ? null : lastMessage.getContent(),
                    lastMessage == null ? null : lastMessage.getSentAt(),
                    unreadCount
            );
        }
    }
}