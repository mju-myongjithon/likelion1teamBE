package com.lionking.ddingchun.chat.repository;

import com.lionking.ddingchun.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPost_Id(Long postId);
}
