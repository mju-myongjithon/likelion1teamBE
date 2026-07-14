package com.lionking.ddingchun.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean isSuccess;
    private String code;
    private String message;
    private T data;

}

