package com.lionking.ddingchun.auth.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidSchoolEmailException extends BusinessException {
    public InvalidSchoolEmailException() {
        super(HttpStatus.BAD_REQUEST, "AUTH400", "학교 이메일(@mju.ac.kr)만 인증할 수 있습니다.");
    }
}