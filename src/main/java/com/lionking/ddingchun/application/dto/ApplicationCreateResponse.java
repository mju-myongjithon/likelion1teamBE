package com.lionking.ddingchun.application.dto;

import com.lionking.ddingchun.application.entity.Application;

public record ApplicationCreateResponse(

        Long applicationId,
        String status

) {

    public static ApplicationCreateResponse from(
            Application application
    ) {
        return new ApplicationCreateResponse(
                application.getId(),
                application.getStatus().name()
        );
    }
}