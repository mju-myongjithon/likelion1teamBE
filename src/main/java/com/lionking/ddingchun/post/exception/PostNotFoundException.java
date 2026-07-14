package com.lionking.ddingchun.post.exception;

import com.lionking.ddingchun.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                "POST4041",
                "존재하지 않는 모집글입니다."
        );
    }
}