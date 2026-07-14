package com.lionking.ddingchun.auth.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailVerificationNotFoundException extends BusinessException {
    public EmailVerificationNotFoundException() {
        super(HttpStatus.NOT_FOUND, "AUTH404", "인증 요청 내역이 없습니다. 인증메일을 먼저 요청해주세요.");
    }
}