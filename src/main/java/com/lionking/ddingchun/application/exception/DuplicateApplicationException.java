package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateApplicationException
        extends BusinessException {

    public DuplicateApplicationException() {
        super(
                HttpStatus.CONFLICT,
                "APPLY4091",
                "이미 신청한 모집글입니다."
        );
    }
}