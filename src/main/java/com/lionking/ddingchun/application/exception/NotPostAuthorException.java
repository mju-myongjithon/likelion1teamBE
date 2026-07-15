package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotPostAuthorException
        extends BusinessException {

    public NotPostAuthorException() {
        super(
                HttpStatus.FORBIDDEN,
                "AUTH403",
                "작성자만 접근할 수 있습니다."
        );
    }
}
