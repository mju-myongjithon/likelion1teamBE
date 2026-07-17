package com.lionking.ddingchun.chat.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ChatRoomNotFoundException extends BusinessException {

    public ChatRoomNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                "CHAT404",
                "존재하지 않는 채팅방입니다."
        );
    }
}