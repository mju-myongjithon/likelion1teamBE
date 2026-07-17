package com.lionking.ddingchun.chat.service;

import com.lionking.ddingchun.chat.dto.ChatMessageResponse;
import com.lionking.ddingchun.chat.dto.ChatMessageSendRequest;
import com.lionking.ddingchun.chat.dto.ChatRoomDetailResponse;
import com.lionking.ddingchun.chat.dto.ChatRoomListResponse;
import com.lionking.ddingchun.chat.entity.ChatMessage;
import com.lionking.ddingchun.chat.entity.ChatRoom;
import com.lionking.ddingchun.chat.entity.ChatRoomMember;
import com.lionking.ddingchun.chat.exception.ChatRoomNotFoundException;
import com.lionking.ddingchun.chat.exception.NotChatRoomMemberException;
import com.lionking.ddingchun.chat.repository.ChatMessageRepository;
import com.lionking.ddingchun.chat.repository.ChatRoomMemberRepository;
import com.lionking.ddingchun.chat.repository.ChatRoomRepository;
import com.lionking.ddingchun.post.entity.Post;
import com.lionking.ddingchun.user.entity.User;
import com.lionking.ddingchun.user.exception.UserNotFoundException;
import com.lionking.ddingchun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    /*
     * 신청 수락 시 호출된다.
     * 모집글에 채팅방이 없으면 새로 만들고, 작성자와 수락된 신청자를 멤버로 추가한다.
     */
    @Transactional
    public void addAcceptedApplicant(
            Post post,
            User acceptedApplicant
    ) {
        ChatRoom chatRoom = chatRoomRepository.findByPost_Id(post.getId())
                .orElseGet(() -> createRoom(post));

        joinIfAbsent(chatRoom, post.getAuthor());
        joinIfAbsent(chatRoom, acceptedApplicant);
    }

    private ChatRoom createRoom(Post post) {
        return chatRoomRepository.save(
                ChatRoom.builder()
                        .post(post)
                        .build()
        );
    }

    private void joinIfAbsent(
            ChatRoom chatRoom,
            User user
    ) {
        boolean alreadyMember = chatRoomMemberRepository
                .existsByChatRoom_IdAndUser_Id(chatRoom.getId(), user.getId());

        if (!alreadyMember) {
            chatRoomMemberRepository.save(
                    ChatRoomMember.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    public ChatRoomListResponse getMyChatRooms(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<ChatRoomMember> myMemberships =
                chatRoomMemberRepository.findByUser_Id(user.getId());

        List<ChatRoomListResponse.ChatRoomSummary> summaries = myMemberships.stream()
                .map(member -> toSummary(member, user))
                .toList();

        return ChatRoomListResponse.from(summaries);
    }

    private ChatRoomListResponse.ChatRoomSummary toSummary(
            ChatRoomMember member,
            User viewer
    ) {
        Long roomId = member.getChatRoom().getId();

        List<ChatRoomMember> allMembers =
                chatRoomMemberRepository.findByChatRoom_Id(roomId);

        String memberSummary = buildMemberSummary(allMembers, viewer);

        ChatMessage lastMessage = chatMessageRepository
                .findTopByChatRoom_IdOrderBySentAtDesc(roomId)
                .orElse(null);

        long unreadCount = chatMessageRepository
                .countByChatRoom_IdAndSentAtAfterAndSender_IdNot(
                        roomId,
                        member.getLastReadAt(),
                        viewer.getId()
                );

        return ChatRoomListResponse.ChatRoomSummary.of(
                member,
                memberSummary,
                lastMessage,
                unreadCount
        );
    }

    private String buildMemberSummary(
            List<ChatRoomMember> members,
            User viewer
    ) {
        List<String> otherNames = members.stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(viewer.getId()))
                .map(User::getName)
                .toList();

        if (otherNames.isEmpty()) {
            return "나";
        }

        if (otherNames.size() == 1) {
            return otherNames.get(0);
        }

        return "%s 외 %d명".formatted(otherNames.get(0), otherNames.size() - 1);
    }

    @Transactional
    public ChatRoomDetailResponse getMessages(
            Long roomId,
            String email
    ) {
        ChatRoomMember member = requireMember(roomId, email);

        List<ChatMessage> messages =
                chatMessageRepository.findByChatRoom_IdOrderBySentAtAsc(roomId);

        member.markRead();

        return ChatRoomDetailResponse.of(
                member.getChatRoom(),
                messages,
                member.getUser().getId()
        );
    }

    @Transactional
    public ChatMessageResponse sendMessage(
            Long roomId,
            ChatMessageSendRequest request
    ) {
        ChatRoomMember member = requireMember(roomId, request.email());

        ChatMessage message = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(member.getChatRoom())
                        .sender(member.getUser())
                        .content(request.content().trim())
                        .build()
        );

        member.markRead();

        return ChatMessageResponse.from(message, member.getUser().getId());
    }

    private ChatRoomMember requireMember(
            Long roomId,
            String email
    ) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new ChatRoomNotFoundException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return chatRoomMemberRepository
                .findByChatRoom_IdAndUser_Id(roomId, user.getId())
                .orElseThrow(NotChatRoomMemberException::new);
    }
}