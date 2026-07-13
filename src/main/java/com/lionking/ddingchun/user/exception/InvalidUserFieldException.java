package com.lionking.ddingchun.user.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidUserFieldException extends BusinessException {
    public InvalidUserFieldException(String message) {
        super(HttpStatus.BAD_REQUEST, "USER400", message);
    }
}