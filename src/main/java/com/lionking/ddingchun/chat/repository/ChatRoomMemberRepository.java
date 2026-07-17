package com.lionking.ddingchun.chat.repository;

import com.lionking.ddingchun.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    boolean existsByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    Optional<ChatRoomMember> findByChatRoom_IdAndUser_Id(Long chatRoomId, Long userId);

    List<ChatRoomMember> findByChatRoom_Id(Long chatRoomId);

    /*
     * 내가 속한 채팅방 목록. 정렬은 서비스에서 마지막 메시지 기준으로 처리한다.
     */
    List<ChatRoomMember> findByUser_Id(Long userId);
}