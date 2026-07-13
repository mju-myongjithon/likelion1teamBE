package com.lionking.ddingchun.user.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException() {
        super(HttpStatus.CONFLICT, "USER409", "이미 가입된 이메일입니다.");
    }
}