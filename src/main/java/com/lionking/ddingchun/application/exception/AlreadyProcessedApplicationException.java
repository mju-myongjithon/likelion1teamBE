package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AlreadyProcessedApplicationException
        extends BusinessException {

    public AlreadyProcessedApplicationException() {
        super(
                HttpStatus.CONFLICT,
                "APPLY4094",
                "이미 처리된 신청입니다."
        );
    }
}