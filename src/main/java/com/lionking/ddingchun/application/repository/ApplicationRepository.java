package com.lionking.ddingchun.application.repository;

import com.lionking.ddingchun.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByPost_IdAndApplicant_Id(
            Long postId,
            Long applicantId
    );
}