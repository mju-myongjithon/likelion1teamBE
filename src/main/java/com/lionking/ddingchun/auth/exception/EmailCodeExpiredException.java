package com.lionking.ddingchun.auth.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailCodeExpiredException extends BusinessException {
    public EmailCodeExpiredException() {
        super(HttpStatus.BAD_REQUEST, "AUTH410", "인증번호가 만료되었습니다. 인증메일을 다시 요청해주세요.");
    }
}