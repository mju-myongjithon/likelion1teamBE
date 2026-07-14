package com.lionking.ddingchun.search.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidSearchConditionException
        extends BusinessException {

    public InvalidSearchConditionException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                "SEARCH4001",
                message
        );
    }
}