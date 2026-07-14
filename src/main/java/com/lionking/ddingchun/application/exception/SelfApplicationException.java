package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SelfApplicationException
        extends BusinessException {

    public SelfApplicationException() {
        super(
                HttpStatus.CONFLICT,
                "APPLY4093",
                "본인이 작성한 글에는 신청할 수 없습니다."
        );
    }
}