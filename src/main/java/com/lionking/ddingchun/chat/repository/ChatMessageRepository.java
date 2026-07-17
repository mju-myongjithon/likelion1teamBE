package com.lionking.ddingchun.chat.repository;

import com.lionking.ddingchun.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_IdOrderBySentAtAsc(Long chatRoomId);

    Optional<ChatMessage> findTopByChatRoom_IdOrderBySentAtDesc(Long chatRoomId);

    long countByChatRoom_IdAndSentAtAfterAndSender_IdNot(
            Long chatRoomId,
            LocalDateTime after,
            Long senderId
    );
}