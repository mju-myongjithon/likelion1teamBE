package com.lionking.ddingchun.user.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super(HttpStatus.BAD_REQUEST, "AUTH402", "이메일 인증이 완료되지 않았습니다.");
    }
}