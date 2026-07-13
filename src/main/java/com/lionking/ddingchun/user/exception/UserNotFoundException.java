package com.lionking.ddingchun.user.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "USER404", "사용자 정보를 찾을 수 없습니다. 소속 정보를 먼저 등록해주세요.");
    }
}