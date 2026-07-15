package com.lionking.ddingchun.application.dto;

import com.lionking.ddingchun.application.entity.Application;

public record ApplicationStatusUpdateResponse(

        Long applicationId,
        String status,
        int currentCount,
        int maxCount

) {

    public static ApplicationStatusUpdateResponse from(Application application) {
        return new ApplicationStatusUpdateResponse(
                application.getId(),
                application.getStatus().name(),
                application.getPost().getCurrentCount(),
                application.getPost().getMaxCount()
        );
    }
}
