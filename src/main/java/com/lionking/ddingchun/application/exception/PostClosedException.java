package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PostClosedException
        extends BusinessException {

    public PostClosedException() {
        super(
                HttpStatus.CONFLICT,
                "APPLY4092",
                "모집 인원이 마감되었습니다."
        );
    }
}