package com.lionking.ddingchun.application.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ApplicationNotFoundException
        extends BusinessException {

    public ApplicationNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                "APPLY4041",
                "존재하지 않는 신청 내역입니다."
        );
    }
}
