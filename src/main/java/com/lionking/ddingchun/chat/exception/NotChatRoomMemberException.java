package com.lionking.ddingchun.chat.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotChatRoomMemberException extends BusinessException {

    public NotChatRoomMemberException() {
        super(
                HttpStatus.FORBIDDEN,
                "CHAT403",
                "채팅방 참여자만 접근할 수 있습니다."
        );
    }
}