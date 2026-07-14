package com.lionking.ddingchun.auth.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailCodeMismatchException extends BusinessException {
    public EmailCodeMismatchException() {
        super(HttpStatus.BAD_REQUEST, "AUTH401", "인증번호가 일치하지 않습니다.");
    }
}